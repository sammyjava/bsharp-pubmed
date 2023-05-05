package org.bsharp.pubmed;

import gov.nih.nlm.ncbi.eutils.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import org.xml.sax.SAXException;

/**
 * Static methods and command-line utility for populating Abstract and Summary objects.
 */
public class Pubmed {

    /**
     * Retrieve a single Abstract for a single PMID, null if not found.
     *
     * @param pmid a PMID
     * @param apikey an optional PubMed API key
     * @return an Abstract, null if not found.
     */
    public static Abstract getAbstract(String pmid, String apikey) throws JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&rettype=abstract&id="+pmid;
        if (apikey!=null) uri += "&api_key=" + apikey;
        PubmedArticleSetDocument articleSet = getPubmedArticleSet(uri);
        List<Abstract> abstracts = getAbstracts(articleSet);
        if (abstracts.size()>0) {
            return abstracts.get(0);
        } else {
            return null;
        }
    }

    /**
     * Retrieve a List of Abstracts for a List of article PMIDs.
     *
     * @param idList a List of PMIDs
     * @param apikey an optional PubMed API key
     * @return a list of Abstract objects
     */
    public static List<Abstract> getAbstracts(List<String> idList, String apikey) throws JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String pmids = idList.toString().replace("[","").replace("]","").replace(" ","");
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&rettype=abstract&id="+pmids;
        if (apikey!=null) uri += "&api_key=" + apikey;
        PubmedArticleSetDocument articleSet = getPubmedArticleSet(uri);
        return getAbstracts(articleSet);
    }
    
    /**
     * Return a List of Abstracts extracted from a PubmedArticleSet.
     *
     * @param articleSet a PubmedArticleSet
     * @return a list of Abstract objects
     */
    static List<Abstract> getAbstracts(PubmedArticleSetDocument articleSetDocument) {
        List<Abstract> abstractList = new ArrayList<>();
        for (PubmedArticleType pubmedArticleType : articleSetDocument.getPubmedArticleSet().getPubmedArticleArray()) {
            abstractList.add(new Abstract(pubmedArticleType));
        }
        return abstractList;
    }

    /**
     * Unmarshal an org.bsharp.pubmed.xml.esearch.ESearchResult from a given URI.
     * TODO: put this in its own class.
     *
     * @param uri full PubMed esearch URI
     * @return an ESearchResult
     */
    public static org.bsharp.pubmed.xml.esearch.ESearchResult getESearchResult(String uri) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(org.bsharp.pubmed.xml.esearch.ESearchResult.class);
        return (org.bsharp.pubmed.xml.esearch.ESearchResult) context.createUnmarshaller().unmarshal(new StreamSource(uri));
    }

    /**
     * Unmarshal a PubmedArticleSet from a given efetch URI.
     *
     * @param uri full PubMed efetch URI
     * @return a PubmedArticleSet
     */
    public static PubmedArticleSetDocument getPubmedArticleSet(String uri) throws JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        // do this because of namespace mismatch between "" and "http://www.ncbi.nlm.nih.gov/eutils"
        Map<String,String> nses = new HashMap<>();
        nses.put("", "http://www.ncbi.nlm.nih.gov/eutils");
        XmlOptions options = new XmlOptions()
            .setLoadSubstituteNamespaces​(nses);
        return PubmedArticleSetDocument.Factory.parse(new URL(uri), options);
    }
    
    /**
     * Return the count from an ESearchResult, 0 if not found.
     */
    static int getESearchResultCount(org.bsharp.pubmed.xml.esearch.ESearchResult result) {
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esearch.Count) {
                return Integer.parseInt(((org.bsharp.pubmed.xml.esearch.Count)o).getvalue());
            }
        }
        return 0;
    }

    /**
     * Return the error from an ESearchResult, null if not found.
     */
    static org.bsharp.pubmed.xml.esearch.ERROR getESearchResultERROR(org.bsharp.pubmed.xml.esearch.ESearchResult result) {
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esearch.ERROR) {
                return (org.bsharp.pubmed.xml.esearch.ERROR) o;
            }
        }
        return null;
    }

    /**
     * Return the List of PMIDs from an ESearchResult, empty if none given.
     */
    static List<String> getESearchResultIdList(org.bsharp.pubmed.xml.esearch.ESearchResult result) {
        List<String> idList = new ArrayList<>();
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esearch.IdList) {
                for (org.bsharp.pubmed.xml.esearch.Id id : ((org.bsharp.pubmed.xml.esearch.IdList)o).getId()) {
                    idList.add(id.getvalue());
                }
            }
        }
        return idList;
    }
    
    /**
     * Return a Abstract searching on DOI with an optional API key; null if not found.
     *
     * @param doi the DOI
     * @param apikey an optional PubMed API key
     * @return an Abstract, null if not found
     */
    public static Abstract esearchDOI(String doi, String apikey)
        throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+URLEncoder.encode(doi,"UTF-8")+"[DOI]";
        if (apikey!=null) uri += "&api_key="+apikey;
        org.bsharp.pubmed.xml.esearch.ESearchResult result = getESearchResult(uri);
        org.bsharp.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) return new Abstract(error);
        List<String> idList = getESearchResultIdList(result);
        // get single Abstract and return it
        if (idList.size()>0) {
            return getAbstract(idList.get(0), apikey);
        } else {
            return null;
        }
    }

    /**
     * Return a List of Abstracts that match a given search term in the abstract text. Limit the number with retmax.
     *
     * @param term the search term
     * @param retmax the maximum number of articles to be returned
     * @param apikey optional PubMed API key
     * @return a List of Abstracts, empty if none found
     */
    public static List<Abstract> esearchText(String term, int retmax, String apikey)
        throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax="+retmax+"&term="+URLEncoder.encode(term,"UTF-8")+"[Abstract]";
        if (apikey!=null) uri += "&api_key=" + apikey;
        org.bsharp.pubmed.xml.esearch.ESearchResult result = getESearchResult(uri);
        org.bsharp.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) {
            List<Abstract> abstracts = new ArrayList<>();
            abstracts.add(new Abstract(error));
            return abstracts;
        }
        List<String> idList = getESearchResultIdList(result);
        if (idList.size()>0) {
            // concatenate ids and return List of Abstracts
            return getAbstracts(idList, apikey);
        } else {
            // none found, return empty List
            return new ArrayList<Abstract>();
        }
    }

    /**
     * Command-line utility.
     */
    public static void main(String[] args) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException  {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option apikeyOption = new Option("a", "apikey", true, "PubMed API key [optional]");
        apikeyOption.setRequired(false);
        options.addOption(apikeyOption);
        
        Option pmidsOption = new Option("p", "pmids", true, "comma-separated list of PMIDs for esummary request");
        pmidsOption.setRequired(false);
        options.addOption(pmidsOption);

        Option textOption = new Option("t", "text", true, "value of text for esearch request");
        textOption.setRequired(false);
        options.addOption(textOption);

        Option doiOption = new Option("d", "doi", true, "value of DOI for esearch request");
        doiOption.setRequired(false);
        options.addOption(doiOption);
        
        Option retmaxOption = new Option("m", "retmax", true, "maximum number of returned Summaries [20]");
        retmaxOption.setRequired(false);
        options.addOption(retmaxOption);
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("Pubmed", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("Pubmed", options);
            System.exit(1);
            return;
        }

        String apikey = null;
        if (cmd.hasOption("apikey")) apikey = cmd.getOptionValue("apikey");

        int retmax = 20;
        if (cmd.hasOption("retmax")) retmax = Integer.parseInt(cmd.getOptionValue("retmax"));
        
        if (cmd.hasOption("pmids")) {
            String pmids = cmd.getOptionValue("pmids");
            System.out.println("esummary "+pmids);
            List<String> idList = Arrays.asList(pmids.split(","));
            List<Abstract> abstracts = getAbstracts(idList, apikey);
            for (Abstract a : abstracts) {
                System.out.println("--------------------");
                System.out.println(a.toString());
            }
        }
            
        if (cmd.hasOption("text")) {
            String text = cmd.getOptionValue("text");
            System.out.println("esearch text="+text+" retmax="+retmax);
            List<Abstract> abstracts = esearchText(text, retmax, apikey);
            for (Abstract a : abstracts) {
                System.out.println("--------------------");
                System.out.println(a.toString());
            }
        }
        
        if (cmd.hasOption("doi")) {
            String doi = cmd.getOptionValue("doi");
            System.out.println("esearch DOI="+doi);
            Abstract a = esearchDOI(doi, apikey);
            if (a!=null) {
                System.out.println("--------------------");
                System.out.println(a.toString());
            }
        }
    }

}
