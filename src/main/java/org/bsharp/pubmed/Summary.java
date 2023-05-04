package org.bsharp.pubmed;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

/**
 * Contains the contents of a PubMed eSummaryResult in public instance variables.
 *
 * @author Sam Hokin
 */
public class Summary {
    
    public static String CHARSET = "UTF-8";

    // ERROR fields
    public String error;

    // DocSum fields
    public String pmid;
    public String pubDate;
    public String ePubDate;
    public String source;
    public String lastAuthor;
    public String title;
    public String volume;
    public String issue;
    public String pages;
    public String nlmUniqueId;
    public String issn;
    public String essn;
    public String recordStatus;
    public String pubStatus;
    public String doi;
    public boolean hasAbstract;
    public int pmcRefCount;
    public String fullJournalName;
    public String eLocationId;
    public String so;

    // public List<String> authorList = new ArrayList<String>();
    // public List<String> langList = new ArrayList<String>();
    // public List<String> pubTypeList = new ArrayList<String>();
    // public Map<String,String> articleIds = new LinkedHashMap<String,String>();
    // public Map<String,String> history = new LinkedHashMap<String,String>();
    // public Map<String,String> references = new LinkedHashMap<String,String>();

    /**
     * Construct from a org.bsharp.pubmed.xml.esummary.DocSum
     */ 
    public Summary(org.bsharp.pubmed.xml.esummary.DocSum docSum) {
        this.pmid = docSum.getId();
        // PROBLEM: this only returns top-level Items, not those within a List!
        List<org.bsharp.pubmed.xml.esummary.Item> items = docSum.getItem();
        for (org.bsharp.pubmed.xml.esummary.Item item : items) {
            switch (item.getName()) {
            case "PubDate": pubDate = item.getvalue(); break;
            case "EPubDate": ePubDate = item.getvalue(); break;
            case "Source": source = item.getvalue(); break;
            case "AuthorList":
                break;
            case "LastAuthor": lastAuthor = item.getvalue(); break;
            case "Title": title = item.getvalue(); break;
            case "Volume": volume = item.getvalue(); break;
            case "Issue": issue = item.getvalue(); break;
            case "Pages": pages = item.getvalue(); break;
            case "LangList":
                break;
            case "NlmUniqueID": nlmUniqueId = item.getvalue(); break;
            case "ISSN": issn = item.getvalue(); break;
            case "ESSN": essn = item.getvalue(); break;
            case "PubTypeList":
                break;
            case "RecordStatus": recordStatus = item.getvalue(); break;
            case "PubStatus": pubStatus = item.getvalue(); break;
            case "ArticleIds": 
                break;
            case "DOI": doi = item.getvalue(); break;
            case "History":
                break;
            case "References":
                break;
            case "HasAbstract": hasAbstract = (item.getvalue().equals("1")); break;
            case "PmcRefCount": pmcRefCount = Integer.parseInt(item.getvalue()); break;
            case "FullJournalName": fullJournalName = item.getvalue(); break;
            case "ELocationID": eLocationId = item.getvalue(); break;
            case "SO": so = item.getvalue(); break;
            default:
                System.out.println(item.getName() + ":" + item.getvalue());
                break;
            }
        }
    }

    /**
     * Construct from an org.bsharp.pubmed.xml.esummary.ERROR.
     */
    Summary(org.bsharp.pubmed.xml.esummary.ERROR esummaryError) {
        this.error = esummaryError.getvalue();
    }

    /**
     * Construct from an org.bsharp.pubmed.xml.esearch.ERROR.
     */
    Summary(org.bsharp.pubmed.xml.esearch.ERROR esearchError) {
        this.error = esearchError.getvalue();
    }

    /**
     * Return true if this Summary is an ERROR.
     */
    public boolean isError() {
        return (error != null);
    }

    /**
     * Unmarshall an org.bsharp.pubmed.xml.esummary.ESummaryResult from a given URI.
     *
     * @param uri full PubMed esummary URI
     * @return an ESummaryResult
     */
    public static org.bsharp.pubmed.xml.esummary.ESummaryResult getESummaryResult(String uri) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(org.bsharp.pubmed.xml.esummary.ESummaryResult.class);
        return (org.bsharp.pubmed.xml.esummary.ESummaryResult) context.createUnmarshaller().unmarshal(new StreamSource(uri));
    }

    /**
     * Unmarshall an org.bsharp.pubmed.xml.esearch.ESearchResult from a given URI.
     *
     * @param uri full PubMed esearch URI
     * @return an ESearchResult
     */
    public static org.bsharp.pubmed.xml.esearch.ESearchResult getESearchResult(String uri) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(org.bsharp.pubmed.xml.esearch.ESearchResult.class);
        return (org.bsharp.pubmed.xml.esearch.ESearchResult) context.createUnmarshaller().unmarshal(new StreamSource(uri));
    }
    
    /**
     * Return a List of Summary given a comma-separated list of PMIDs. apikey may be null.
     *
     * @param ids comma-separated list of PMIDs
     * @param apikey optional PubMed API key
     * @return a List of Summary
     */
    public static List<Summary> getSummaries(String ids, String apikey) throws SAXException, JAXBException, XMLStreamException {
        List<Summary> summaries = new ArrayList<>();
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id="+ids;
        if (apikey!=null) uri += "&api_key="+apikey;
        org.bsharp.pubmed.xml.esummary.ESummaryResult result = getESummaryResult(uri);
        for (Object o : result.getDocSumOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esummary.DocSum) {
                summaries.add(new Summary((org.bsharp.pubmed.xml.esummary.DocSum) o));
            } else {
                summaries.add(new Summary((org.bsharp.pubmed.xml.esummary.ERROR) o));
            }
        }
        return summaries;
    }                

    /**
     * Find a List of Summary by searching on title. Set apikey to null if not supplied.
     * The list is empty (but not null) if no summaries are found.
     *
     * @param title the title to search
     * @param retmax the maximum number of returned articles
     * @param apikey optional PubMed API key
     * @return a single Summary resulting from the search
     */
    public static List<Summary> esearchTitle(String title, int retmax, String apikey) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax="+retmax+"&term="+URLEncoder.encode(title,"UTF-8")+"[Title]";
        if (apikey!=null) uri += "&api_key="+apikey;
        org.bsharp.pubmed.xml.esearch.ESearchResult result = getESearchResult(uri);
        int count = 0;
        List<String> idList = new ArrayList<>();
        // TO DO PUT THIS IN ITS OWN CLASS
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esearch.ERROR) {
                List<Summary> summaries = new ArrayList<>();
                summaries.add(new Summary((org.bsharp.pubmed.xml.esearch.ERROR) o));
                return summaries;
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
        // concatenate ids and get Summary list
        if (idList.size()>0) {
            String pmids = idList.get(0);
            for (int i=1; i<idList.size(); i++) pmids += "," + idList.get(i);
            return getSummaries(pmids, apikey);
        } else {
            return new ArrayList<Summary>();
        }
    }

    /**
     * Return a Summary searching on DOI with an optional API key.
     *
     * @param doi the DOI
     * @param apikey an optional PubMed API key
     * @return a Summary
     */
    public static Summary esearchDOI(String doi, String apikey) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+URLEncoder.encode(doi,"UTF-8")+"[DOI]";
        if (apikey!=null) uri += "&api_key="+apikey;
        org.bsharp.pubmed.xml.esearch.ESearchResult result = getESearchResult(uri);
        int count = 0;
        List<String> idList = new ArrayList<>();
        // TO DO PUT THIS IN ITS OWN CLASS
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof org.bsharp.pubmed.xml.esearch.ERROR) {
                return new Summary((org.bsharp.pubmed.xml.esearch.ERROR) o);
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
        // get Summary and return it
        if (idList.size()==1) {
            String pmid = idList.get(0);
            List<Summary> summaries = getSummaries(pmid, apikey);
            return summaries.get(0);
        } else {
            // no error but no articles, may never happen.
            return null;
        }
    }

    /**
     * Spit out a string representation of this summary.
     */
    public String toString() {
        String out = "";
        if (error!=null) {
            out += "ERROR:" + error;
        } else {
            out += "PMID:"+pmid+"\n";
            out += "PubDate:"+pubDate+"\n";
            out += "EPubDate:"+ePubDate+"\n";
            out += "Source:"+source+"\n";
            // for (String author : authorList) {
            //     out += "Author:"+author+"\n";
            // }
            out += "LastAuthor:"+lastAuthor+"\n";
            out += "Title:"+title+"\n";
            out += "Volume:"+volume+"\n";
            out += "Issue:"+issue+"\n";
            out += "Pages:"+pages+"\n";
            // for (String lang : langList) {
            //     out += "Lang:"+lang+"\n";
            // }
            out += "NlmUniqueID:"+nlmUniqueId+"\n";
            out += "ISSN:"+issn+"\n";
            out += "ESSN:"+essn+"\n";
            // for (String pubType : pubTypeList) {
            //     out += "PubType:"+pubType+"\n";
            // }
            out += "RecordStatus:"+recordStatus+"\n";
            out += "PubStatus:"+pubStatus+"\n";
            out += "DOI:"+doi+"\n";
            out += "HasAbstract:"+hasAbstract+"\n";
            out += "PmcRefCount:"+pmcRefCount+"\n";
            out += "FullJournalName:"+fullJournalName+"\n";
            out += "ELocationID:"+eLocationId+"\n";
            out += "SO:"+so+"\n";
        }
        return out;
    }

    /**
     * Command-line queries
     */
    public static void main(String[] args) throws SAXException, JAXBException, XMLStreamException, UnsupportedEncodingException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option apikeyOption = new Option("a", "apikey", true, "PubMed API key [optional]");
        apikeyOption.setRequired(false);
        options.addOption(apikeyOption);
        
        Option pmidOption = new Option("p", "pmid", true, "value of PMID for esummary request");
        pmidOption.setRequired(false);
        options.addOption(pmidOption);

        Option titleOption = new Option("t", "title", true, "value of title for esearch request");
        titleOption.setRequired(false);
        options.addOption(titleOption);

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
            formatter.printHelp("Summary", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("Summary", options);
            System.exit(1);
            return;
        }

        String apikey = null;
        if (cmd.hasOption("apikey")) apikey = cmd.getOptionValue("apikey");

        int retmax = 20;
        if (cmd.hasOption("retmax")) retmax = Integer.parseInt(cmd.getOptionValue("retmax"));

        if (cmd.hasOption("pmid")) {
            String pmid = cmd.getOptionValue("pmid");
            System.out.println("esummary "+pmid);
            List<Summary> summaries = Summary.getSummaries(pmid, apikey);
            for (Summary s : summaries) {
                System.out.println("--------------------");
                System.out.println(s.toString());
            }
        }
            
        if (cmd.hasOption("title")) {
            String title = cmd.getOptionValue("title");
            System.out.println("esearch title="+title+" retmax="+retmax);
            List<Summary> summaries = Summary.esearchTitle(title, retmax, apikey);
            for (Summary s : summaries) {
                System.out.println("--------------------");
                System.out.println(s.toString());
            }
        }
        
        if (cmd.hasOption("doi")) {
            String doi = cmd.getOptionValue("doi");
            System.out.println("esearch DOI="+doi);
            Summary summary = Summary.esearchDOI(doi, apikey);
            if (summary!=null) {
                System.out.println("--------------------");
                System.out.println(summary.toString());
            }
        }

    }
}

