package org.bsharp.pubmed;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the contents of a PubMed eSummaryResult DocSum.
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

    // having trouble with these!
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
}

