package org.bsharp.pubmed;

import gov.nih.nlm.ncbi.eutils.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
        
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

/**
 * Contains an Abstract from a PubmedArticle returned by efetch.
 * Instance variables are public, there are no setters/getters.
 *
 * Includes static methods for fetching abstracts.
 */
public class Abstract {

    // ERROR field
    public String error;
    
    // PubmedArticle fields
    public String pmid;
    public String title;
    public String text;
    public String pmcid;
    public String doi;
    public List<String> keywords;

    /**
     * Construct from a PubmedArticleType
     */
    public Abstract(PubmedArticleType pubmedArticleType) {
        ArticleType articleType = pubmedArticleType.getMedlineCitation().getArticle();
        AbstractType abstractType = articleType.getAbstract();
        if (abstractType!=null) {
            this.pmid = pubmedArticleType.getMedlineCitation().getPMID();
            this.title = articleType.getArticleTitle();
            this.text = abstractType.getAbstractText();
            for (ArticleIdType id : pubmedArticleType.getPubmedData().getArticleIdList().getArticleIdArray()) {
                if (id.getIdType() == ArticleIdType.IdType.PMCID) {
                    this.pmcid = id.getStringValue();
                } else if (id.getIdType() == ArticleIdType.IdType.DOI) {
                    this.doi = id.getStringValue();
                }
            }
            this.keywords = new ArrayList<>();
            for (KeywordListType keywordListType : pubmedArticleType.getMedlineCitation().getKeywordListArray()) {
                for (KeywordType keywordType : keywordListType.getKeywordArray()) {
                    this.keywords.add(keywordType.getStringValue());
                }
            }
        }
    }

    /**
     * Construct from an org.bsharp.pubmed.xml.esearch.ERROR.
     */
    Abstract(org.bsharp.pubmed.xml.esearch.ERROR esearchError) {
        this.error = esearchError.getvalue();
    }

    /**
     * Return a List of Abstracts that match a given search term in the abstract text. Indicate maximum number with retmax.
     *
     * @param term the search term
     * @retmax the maximum number of articles to be returned
     * @return a comma-separated list of PMIDs
     */
    public static List<Abstract> esearchText(String term, int retmax, String apikey)
        throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax="+retmax+"&term="+URLEncoder.encode(term,"UTF-8")+"[Abstract]";
        if (apikey!=null) uri += "&api_key=" + apikey;
        org.bsharp.pubmed.xml.esearch.ESearchResult result = getESearchResult(uri);
        int count = 0;
        List<String> idList = new ArrayList<>();
        // TO DO PUT THIS IN ITS OWN CLASS
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esearch.ERROR) {
                List<Abstract> abstracts = new ArrayList<>();
                abstracts.add(new Abstract((org.bsharp.pubmed.xml.esearch.ERROR) o));
                return abstracts;
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.Count) {
                count = Integer.parseInt(((org.bsharp.pubmed.xml.esearch.Count)o).getvalue());
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.RetMax) {
                // retmax = Integer.parseInt(((org.bsharp.pubmed.xml.esearch.RetMax)o).getvalue());
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.RetStart) {
                // retstart = Integer.parseInt(((org.bsharp.pubmed.xml.esearch.RetStart)o).getvalue());
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.QueryKey) {
                // do nothing
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.WebEnv) {
                // do nothing
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.IdList) {
                for (org.bsharp.pubmed.xml.esearch.Id id : ((org.bsharp.pubmed.xml.esearch.IdList)o).getId()) {
                    idList.add(id.getvalue());
                }
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.TranslationSet) {
                // do nothing
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.TranslationStack) {
                // do nothing
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.QueryTranslation) {
                // do nothing
            }
        }
        // concatenate ids and get Abstract list
        if (idList.size()>0) {
            String pmids = idList.get(0);
            for (int i=1; i<idList.size(); i++) pmids += "," + idList.get(i);
            return getAbstracts(pmids, apikey);
        } else {
            return new ArrayList<Abstract>();
        }
    }

    /**
     * Return a Abstract searching on DOI with an optional API key.
     *
     * @param doi the DOI
     * @param apikey an optional PubMed API key
     * @return a Abstract
     */
    public static Abstract esearchDOI(String doi, String apikey)
        throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+URLEncoder.encode(doi,"UTF-8")+"[DOI]";
        if (apikey!=null) uri += "&api_key="+apikey;
        org.bsharp.pubmed.xml.esearch.ESearchResult result = getESearchResult(uri);
        int count = 0;
        List<String> idList = new ArrayList<>();
        // TO DO PUT THIS IN ITS OWN CLASS
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esearch.ERROR) {
                return new Abstract((org.bsharp.pubmed.xml.esearch.ERROR) o);
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.Count) {
                count = Integer.parseInt(((org.bsharp.pubmed.xml.esearch.Count)o).getvalue());
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.RetMax) {
                // retmax = Integer.parseInt(((org.bsharp.pubmed.xml.esearch.RetMax)o).getvalue());
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.RetStart) {
                // retstart = Integer.parseInt(((org.bsharp.pubmed.xml.esearch.RetStart)o).getvalue());
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.QueryKey) {
                // do nothing
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.WebEnv) {
                // do nothing
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.IdList) {
                for (org.bsharp.pubmed.xml.esearch.Id id : ((org.bsharp.pubmed.xml.esearch.IdList)o).getId()) {
                    idList.add(id.getvalue());
                }
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.TranslationSet) {
                // do nothing
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.TranslationStack) {
                // do nothing
            } else if (o instanceof org.bsharp.pubmed.xml.esearch.QueryTranslation) {
                // do nothing
            }
        }
        // get Abstract and return it
        if (idList.size()==1) {
            String pmid = idList.get(0);
            List<Abstract> abstracts = getAbstracts(pmid, apikey);
            return abstracts.get(0);
        } else {
            // no error but no articles, may never happen.
            return null;
        }
    }

    /**
     * Retrieve a List of Abstracts for a comma-separated list of article PMIDs.
     *
     * @param ids a comma-separated list of PMIDs
     * @param apikey an optional PubMed API key
     * @return a list of Abstract objects
     */
    public static List<Abstract> getAbstracts(String pmids, String apikey) throws JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
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
            formatter.printHelp("Abstract", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("Abstract", options);
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
            List<Abstract> abstracts = Abstract.getAbstracts(pmids, apikey);
            for (Abstract a : abstracts) {
                System.out.println("--------------------");
                System.out.println(a.toString());
            }
        }
            
        if (cmd.hasOption("text")) {
            String text = cmd.getOptionValue("text");
            System.out.println("esearch text="+text+" retmax="+retmax);
            List<Abstract> abstracts = Abstract.esearchText(text, retmax, apikey);
            for (Abstract a : abstracts) {
                System.out.println("--------------------");
                System.out.println(a.toString());
            }
        }
        
        if (cmd.hasOption("doi")) {
            String doi = cmd.getOptionValue("doi");
            System.out.println("esearch DOI="+doi);
            Abstract a = Abstract.esearchDOI(doi, apikey);
            if (a!=null) {
                System.out.println("--------------------");
                System.out.println(a.toString());
            }
        }
    }

    /**
     * String representation of this Abstract object.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PMID: " + pmid + "\n");
        if (doi!=null) sb.append("DOI: " + doi + "\n");
        if (pmcid!=null) sb.append("PMCID: " + pmcid + "\n");
        sb.append("Title: " + title + "\n");
        sb.append("Abstract: " + "\n");
        sb.append(text + "\n");
        sb.append("Keywords: " + "\n");
        sb.append(keywords);
        return sb.toString();
    }
}
