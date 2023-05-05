package org.bsharp.pubmed;

import org.bsharp.pubmed.xml.esummary.DocSum;
import org.bsharp.pubmed.xml.esummary.ERROR;
import org.bsharp.pubmed.xml.esummary.Item;

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
    String error;

    // DocSum fields
    String pmid;
    String pubDate;
    String ePubDate;
    String source;
    String lastAuthor;
    String title;
    String volume;
    String issue;
    String pages;
    String nlmUniqueId;
    String issn;
    String essn;
    String recordStatus;
    String pubStatus;
    String doi;
    boolean hasAbstract;
    int pmcRefCount;
    String fullJournalName;
    String eLocationId;
    String so;

    // having trouble with these!
    // public List<String> authorList = new ArrayList<String>();
    // public List<String> langList = new ArrayList<String>();
    // public List<String> pubTypeList = new ArrayList<String>();
    // public Map<String,String> articleIds = new LinkedHashMap<String,String>();
    // public Map<String,String> history = new LinkedHashMap<String,String>();
    // public Map<String,String> references = new LinkedHashMap<String,String>();

    /**
     * Construct from a DocSum
     */ 
    public Summary(DocSum docSum) {
        pmid = docSum.getId();
        // PROBLEM: this only returns top-level Items, not those within a List!
        List<Item> items = docSum.getItem();
        for (Item item : items) {
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
     * Construct from an ERROR.
     */
    Summary(ERROR esummaryError) {
        error = esummaryError.getvalue();
    }

    /**
     * Construct from an org.bsharp.pubmed.xml.esearch.ERROR.
     */
    Summary(org.bsharp.pubmed.xml.esearch.ERROR esearchError) {
        error = esearchError.getvalue();
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

