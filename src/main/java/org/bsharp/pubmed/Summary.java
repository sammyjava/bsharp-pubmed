package org.bsharp.pubmed;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.similarity.LevenshteinDistance;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

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

    // maximum allowable dissimilarity between search title and retreived title
    public static int MAX_LEVENSHTEIN_DISTANCE = 10;

    // eSummaryResult/DocSum fields
    public int id;
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
    public List<String> authorList = new ArrayList<String>();
    public List<String> langList = new ArrayList<String>();
    public List<String> pubTypeList = new ArrayList<String>();
    public Map<String,String> articleIds = new LinkedHashMap<String,String>();
    public Map<String,String> history = new LinkedHashMap<String,String>();
    public Map<String,String> references = new LinkedHashMap<String,String>();

    /**
     * Construct from a populated Document.
     */
    public Summary(Document doc) {
        this.id = Integer.parseInt(doc.getElementsByTagName("Id").item(0).getTextContent());
        NodeList items = doc.getElementsByTagName("Item");
        for (int i=0; i<items.getLength(); i++) {
            Node item = items.item(i);
            Node itemName = item.getAttributes().getNamedItem("Name");
            String itemValue = item.getTextContent();
            boolean inList = false;
            // process singletons
            switch (itemName.getTextContent()) {
            case "PubDate": pubDate = itemValue; break;
            case "EPubDate": ePubDate = itemValue; break;
            case "Source": source = itemValue; break;
            case "AuthorList":
                inList = true;
                while (inList) {
                    Node listItem = items.item(++i);
                    Node listItemName = listItem.getAttributes().getNamedItem("Name");
                    String listItemValue = listItem.getTextContent();
                    if (listItemName.getTextContent().equals("Author")) {
                        authorList.add(listItemValue);
                    } else {
                        i--;
                        inList = false;
                    }
                }
                break;
            case "LastAuthor": lastAuthor = itemValue; break;
            case "Title": title = itemValue; break;
            case "Volume": volume = itemValue; break;
            case "Issue": issue = itemValue; break;
            case "Pages": pages = itemValue; break;
            case "LangList":
                inList = true;
                while (inList) {
                    Node listItem = items.item(++i);
                    Node listItemName = listItem.getAttributes().getNamedItem("Name");
                    String listItemValue = listItem.getTextContent();
                    if (listItemName.getTextContent().equals("Lang")) {
                        langList.add(listItemValue);
                    } else {
                        i--;
                        inList = false;
                    }
                }
                break;
            case "NlmUniqueID": nlmUniqueId = itemValue; break;
            case "ISSN": issn = itemValue; break;
            case "ESSN": essn = itemValue; break;
            case "PubTypeList":
                inList = true;
                while (inList) {
                    Node listItem = items.item(++i);
                    Node listItemName = listItem.getAttributes().getNamedItem("Name");
                    String listItemValue = listItem.getTextContent();
                    if (listItemName.getTextContent().equals("PubType")) {
                        pubTypeList.add(listItemValue);
                    } else {
                        i--;
                        inList = false;
                    }
                }
                break;
            case "RecordStatus": recordStatus = itemValue; break;
            case "PubStatus": pubStatus = itemValue; break;
            case "ArticleIds": break; // bail on this one for now
            case "DOI": doi = itemValue; break;
            case "History": break;    // bail on this one for now
            case "References": break; // bail on this one for now
            case "HasAbstract": hasAbstract = (itemValue.equals("1")); break;
            case "PmcRefCount": pmcRefCount = Integer.parseInt(itemValue); break;
            case "FullJournalName": fullJournalName = itemValue; break;
            case "ELocationID": eLocationId = itemValue; break;
            case "SO": so = itemValue; break;
            default: break;
            }
        }
    }

    /**
     * Return a Summary given its id, or null if not found.
     */
    public static Summary esummary(int id) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
        // URL without API key
        String url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id="+id;
        // parse the URL response
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(url);
        doc.getDocumentElement().normalize(); // recommended
        // if article doesn't exist, response has ERROR tag
        boolean exists = doc.getElementsByTagName("ERROR").item(0)==null;
        if (exists) {
            return new Summary(doc);
        } else {
            return null;
        }
    }

    /**
     * Return a Summary given its id and an API key, null if not found.
     */
    public static Summary esummary(int id, String apiKey) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
        // URL with API key
        String url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&api_key="+apiKey+"&id="+id;
        // parse the URL response
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(url);
        doc.getDocumentElement().normalize(); // recommended
        // if article doesn't exist, response has ERROR tag
        boolean exists = doc.getElementsByTagName("ERROR").item(0)==null;
        if (exists) {
            return new Summary(doc);
        } else {
            return null;
        }
    }

    /**
     * Search for a title without an API key, basing match on Levenshtein distance. Return null if a close match isn't found.
     */
    public static Summary esearchTitle(String title) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
        // URL without API key
        String searchUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+URLEncoder.encode(title,"UTF-8")+"[Title]";
        // parse the URL response
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(searchUrl);
        doc.getDocumentElement().normalize(); // recommended
        // if article exists, count>0
        Node countNode = doc.getElementsByTagName("Count").item(0);
        int count = Integer.parseInt(countNode.getTextContent());
        int id = 0;
        if (count>0) {
            Node idNode = doc.getElementsByTagName("Id").item(0);
            id = Integer.parseInt(idNode.getTextContent());
        }
        if (id>0) {
            Summary summary = esummary(id);
            // similar titles?
            LevenshteinDistance distance = new LevenshteinDistance();
            int dist = distance.apply(title.toLowerCase(), summary.title.toLowerCase());
            if (dist>MAX_LEVENSHTEIN_DISTANCE) {
                return null;
            } else {
                return summary;
            }
        } else {
            return null;
        }
    }

    /**
     * Find a Summary by searching on title with an API key.
     */
    public static Summary esearchTitle(String title, String apiKey) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
        // URL without API key
        String searchUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&api_key="+apiKey+"&term="+URLEncoder.encode(title,"UTF-8")+"[Title]";
        // parse the URL response
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(searchUrl);
        doc.getDocumentElement().normalize(); // recommended
        // if article exists, count>0
        Node countNode = doc.getElementsByTagName("Count").item(0);
        int count = Integer.parseInt(countNode.getTextContent());
        int id = 0;
        if (count>0) {
            Node idNode = doc.getElementsByTagName("Id").item(0);
            id = Integer.parseInt(idNode.getTextContent());
        }
        if (id>0) {
            Summary summary = esummary(id, apiKey);
            // similar titles?
            LevenshteinDistance distance = new LevenshteinDistance();
            int dist = distance.apply(title.toLowerCase(), summary.title.toLowerCase());
            if (dist>MAX_LEVENSHTEIN_DISTANCE) {
                return null;
            } else {
                return summary;
            }
        } else {
            return null;
        }
    }

    /**
     * Find a Summary by DOI without an API key.
     */
    public static Summary esearchDOI(String doi) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
        // URL without API key
        String searchUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+URLEncoder.encode(doi,"UTF-8")+"[DOI]";
        // parse the URL response
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(searchUrl);
        doc.getDocumentElement().normalize(); // recommended
        // if article exists, count>0
        Node countNode = doc.getElementsByTagName("Count").item(0);
        int count = Integer.parseInt(countNode.getTextContent());
        int id = 0;
        if (count>0) {
            Node idNode = doc.getElementsByTagName("Id").item(0);
            id = Integer.parseInt(idNode.getTextContent());
        }
        if (id>0) {
            return esummary(id);
        } else {
            return null;
        }
    }

    /**
     * Return a Summary based on DOI with an API key.
     */
    public static Summary esearchDOI(String doi, String apiKey) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
        // URL without API key
        String searchUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&api_key="+apiKey+"&term="+URLEncoder.encode(doi,"UTF-8")+"[DOI]";
        // parse the URL response
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(searchUrl);
        doc.getDocumentElement().normalize(); // recommended
        // if article exists, count>0
        Node countNode = doc.getElementsByTagName("Count").item(0);
        int count = Integer.parseInt(countNode.getTextContent());
        int id = 0;
        if (count>0) {
            Node idNode = doc.getElementsByTagName("Id").item(0);
            id = Integer.parseInt(idNode.getTextContent());
        }
        if (id>0) {
            return esummary(id, apiKey);
        } else {
            return null;
        }
    }

    /**
     * Spit out a string representation of this summary.
     */
    public String toString() {
        String out = "";
        out += "ID:"+id+"\n";
        out += "PubDate:"+pubDate+"\n";
        out += "EPubDate:"+ePubDate+"\n";
        out += "Source:"+source+"\n";
        for (String author : authorList) {
            out += "Author:"+author+"\n";
        }
        out += "LastAuthor:"+lastAuthor+"\n";
        out += "Title:"+title+"\n";
        out += "Volume:"+volume+"\n";
        out += "Issue:"+issue+"\n";
        out += "Pages:"+pages+"\n";
        for (String lang : langList) {
            out += "Lang:"+lang+"\n";
        }
        out += "NlmUniqueID:"+nlmUniqueId+"\n";
        out += "ISSN:"+issn+"\n";
        out += "ESSN:"+essn+"\n";
        for (String pubType : pubTypeList) {
            out += "PubType:"+pubType+"\n";
        }
        out += "RecordStatus:"+recordStatus+"\n";
        out += "PubStatus:"+pubStatus+"\n";
        out += "DOI:"+doi+"\n";
        out += "HasAbstract:"+hasAbstract+"\n";
        out += "PmcRefCount:"+pmcRefCount+"\n";
        out += "FullJournalName:"+fullJournalName+"\n";
        out += "ELocationID:"+eLocationId+"\n";
        out += "SO:"+so+"\n";
        return out;
    }

    /**
     * Command-line queries
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option pmidOption = new Option("p", "pmid", true, "value of PMID for esummary request");
        pmidOption.setRequired(false);
        options.addOption(pmidOption);

        Option titleOption = new Option("t", "title", true, "value of title for esearch request");
        titleOption.setRequired(false);
        options.addOption(titleOption);

        Option doiOption = new Option("d", "doi", true, "value of DOI for esearch request");
        doiOption.setRequired(false);
        options.addOption(doiOption);
        
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

        if (cmd.hasOption("pmid")) {
            int id = Integer.parseInt(cmd.getOptionValue("pmid"));
            System.out.println("esummary "+id);
            System.out.println("");
            Summary summary = Summary.esummary(id);
            if (summary!=null) {
                System.out.println(summary.toString());
            }
        }
            
        if (cmd.hasOption("title")) {
            String title = cmd.getOptionValue("title");
            System.out.println("esearch title="+title);
            System.out.println("");
            Summary summary = Summary.esearchTitle(title);
            if (summary!=null) {
                System.out.println(summary.toString());
            }
        }
        
        if (cmd.hasOption("doi")) {
            String doi = cmd.getOptionValue("doi");
            System.out.println("esearch DOI="+doi);
            System.out.println("");
            Summary summary = Summary.esearchDOI(doi);
            if (summary!=null) {
                System.out.println(summary.toString());
            }
        }

    }
}

