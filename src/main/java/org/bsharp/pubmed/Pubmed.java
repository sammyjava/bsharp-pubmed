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
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&rettype=abstract&id="+getCommaSeparatedString(idList);
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
     * Return a single Summary given a PMID.
     */
    public static Summary getSummary(String pmid, String apikey) throws SAXException, JAXBException, XMLStreamException {
        List<Summary> summaries = new ArrayList<>();
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id="+pmid;
        if (apikey!=null) uri += "&api_key="+apikey;
        org.bsharp.pubmed.xml.esummary.ESummaryResult result = getESummaryResult(uri);
        for (Object o : result.getDocSumOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esummary.DocSum) {
                return new Summary((org.bsharp.pubmed.xml.esummary.DocSum) o);
            } else if (o instanceof org.bsharp.pubmed.xml.esummary.ERROR) {
                return new Summary((org.bsharp.pubmed.xml.esummary.ERROR) o);
            }
        }
        return null;
    }
    
    /**
     * Return a List of Summaries given a List of PMIDs, empty if none found.
     *
     * @param idList List of PMIDs
     * @param apikey optional PubMed API key
     * @return a List of Summary objects
     */
    public static List<Summary> getSummaries(List<String> idList, String apikey) throws SAXException, JAXBException, XMLStreamException {
        List<Summary> summaries = new ArrayList<>();
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id="+getCommaSeparatedString(idList);
        if (apikey!=null) uri += "&api_key="+apikey;
        org.bsharp.pubmed.xml.esummary.ESummaryResult result = getESummaryResult(uri);
        for (Object o : result.getDocSumOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esummary.DocSum) {
                summaries.add(new Summary((org.bsharp.pubmed.xml.esummary.DocSum) o));
            } else if (o instanceof org.bsharp.pubmed.xml.esummary.ERROR) {
                summaries.add(new Summary((org.bsharp.pubmed.xml.esummary.ERROR) o));
            }
        }
        return summaries;
    }

    /**
     * Unmarshal an org.bsharp.pubmed.xml.esearch.ESearchResult from a given URI.
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
     * Unmarshal an org.bsharp.pubmed.xml.esummary.ESummaryResult from a given URI.
     *
     * @param uri full PubMed esummary URI
     * @return an ESummaryResult
     */
    public static org.bsharp.pubmed.xml.esummary.ESummaryResult getESummaryResult(String uri) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(org.bsharp.pubmed.xml.esummary.ESummaryResult.class);
        return (org.bsharp.pubmed.xml.esummary.ESummaryResult) context.createUnmarshaller().unmarshal(new StreamSource(uri));
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
    public static Abstract searchAbstractDOI(String doi, String apikey)
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
    public static List<Abstract> searchAbstractText(String term, int retmax, String apikey)
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
     * Return a Summary searching on DOI with an optional API key. Returns null if none found.
     *
     * @param doi the DOI
     * @param apikey an optional PubMed API key
     * @return a Summary
     */
    public static Summary searchSummaryDOI(String doi, String apikey) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+URLEncoder.encode(doi,"UTF-8")+"[DOI]";
        if (apikey!=null) uri += "&api_key="+apikey;
        org.bsharp.pubmed.xml.esearch.ESearchResult result = getESearchResult(uri);
        org.bsharp.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) {
            return new Summary(error);
        }
        List<String> idList = getESearchResultIdList(result);
        if (idList.size()>0) {
            return getSummary(idList.get(0), apikey);
        } else {
            return null;
        }        
    }

    /**
     * Return a List of Summary by searching on title. Set apikey to null if not supplied. Return empty list if none found.
     *
     * @param title the title to search
     * @param retmax the maximum number of returned articles
     * @param apikey optional PubMed API key
     * @return a single Summary resulting from the search
     */
    public static List<Summary> searchSummaryTitle(String title, int retmax, String apikey) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax="+retmax+"&term="+URLEncoder.encode(title,"UTF-8")+"[Title]";
        if (apikey!=null) uri += "&api_key="+apikey;
        org.bsharp.pubmed.xml.esearch.ESearchResult result = getESearchResult(uri);
        org.bsharp.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) {
            List<Summary> list = new ArrayList<>();
            list.add(new Summary(error));
            return list;
        }
        List<String> idList = getESearchResultIdList(result);
        if (idList.size()>0) {
            return getSummaries(idList, apikey);
        } else {
            return new ArrayList<Summary>();
        }
    }

    /**
     * Return a comma-separated String formed from a List of Strings.
     */
    static String getCommaSeparatedString(List<String> list) {
        return list.toString().replace("[","").replace("]","").replace(" ","");
    }
    
    /**
     * Command-line utility.
     */
    public static void main(String[] args) throws SAXException, JAXBException, XMLStreamException, UnsupportedEncodingException, MalformedURLException, XmlException, IOException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option abstractOption = new Option("a", "abstract", false, "Search for abstracts");
        abstractOption.setRequired(false);
        options.addOption(abstractOption);

        Option summaryOption = new Option("s", "summary", false, "Search for summaries");
        summaryOption.setRequired(false);
        options.addOption(summaryOption);
        
        Option apikeyOption = new Option("k", "apikey", true, "PubMed API key [optional]");
        apikeyOption.setRequired(false);
        options.addOption(apikeyOption);
        
        Option pmidsOption = new Option("p", "pmids", true, "comma-separated list of PMIDs for efetch or esummary request");
        pmidsOption.setRequired(false);
        options.addOption(pmidsOption);

        Option textOption = new Option("t", "text", true, "value of text for esearch request");
        textOption.setRequired(false);
        options.addOption(textOption);

        Option doiOption = new Option("d", "doi", true, "value of DOI for esearch request");
        doiOption.setRequired(false);
        options.addOption(doiOption);

        Option retmaxOption = new Option("m", "retmax", true, "maximum number of returned objects [20]");
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

        if (!cmd.hasOption("abstract") && !cmd.hasOption("summary")) {
            System.err.println("You must choose either --abstract or --summary.");
            System.exit(1);
        }

        String apikey = null;
        if (cmd.hasOption("apikey")) apikey = cmd.getOptionValue("apikey");

        int retmax = 20;
        if (cmd.hasOption("retmax")) retmax = Integer.parseInt(cmd.getOptionValue("retmax"));
        
        List<String> idList = null;
        if (cmd.hasOption("pmids")) {
            String pmids = cmd.getOptionValue("pmids");
            idList = Arrays.asList(pmids.split(","));
            if (cmd.hasOption("abstract")) {
                System.out.println("efetch pmids=" + pmids);
                List<Abstract> abstracts = getAbstracts(idList, apikey);
                for (Abstract a : abstracts) {
                    System.out.println("--------------------");
                    System.out.println(a.toString());
                }
            }
            if (cmd.hasOption("summary")) {
                System.out.println("esummary pmids=" + pmids);
                List<Summary> summaries = getSummaries(idList, apikey);
                for (Summary s : summaries) {
                    System.out.println("--------------------");
                    System.out.println(s.toString());
                }
            }
        }
            
        if (cmd.hasOption("text")) {
            String text = cmd.getOptionValue("text");
            System.out.println("esearch text="+text+" retmax="+retmax);
            if (cmd.hasOption("abstract")) {
                List<Abstract> abstracts = searchAbstractText(text, retmax, apikey);
                for (Abstract a : abstracts) {
                    System.out.println("--------------------");
                    System.out.println(a.toString());
                }
            }
            if (cmd.hasOption("summary")) {
                List<Summary> summaries = searchSummaryTitle(text, retmax, apikey);
                for (Summary s : summaries) {
                    System.out.println("--------------------");
                    System.out.println(s.toString());
                }
            }
        }
        
        if (cmd.hasOption("doi")) {
            String doi = cmd.getOptionValue("doi");
            System.out.println("esearch DOI="+doi);
            if (cmd.hasOption("abstract")) {
                Abstract a = searchAbstractDOI(doi, apikey);
                if (a!=null) {
                    System.out.println("--------------------");
                    System.out.println(a.toString());
                }
            }
            if (cmd.hasOption("summary")) {
                Summary summary = searchSummaryDOI(doi, apikey);
                if (summary!=null) {
                    System.out.println("--------------------");
                    System.out.println(summary.toString());
                }
            }
        }
    }
}
