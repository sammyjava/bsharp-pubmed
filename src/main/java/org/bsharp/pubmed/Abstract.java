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

/**
 * Contains an Abstract from a PubmedArticle.
 */
public class Abstract {

    String error;
    String pmid;
    String title;
    String text;
    String pmcid;
    String doi;
    
    List<String> keywords = new ArrayList<>();
    
    /**
     * Construct from a PubmedArticleType
     */
    public Abstract(PubmedArticleType pubmedArticleType) {
        ArticleType articleType = pubmedArticleType.getMedlineCitation().getArticle();
        AbstractType abstractType = articleType.getAbstract();
        if (abstractType!=null) {
            setPMID(pubmedArticleType.getMedlineCitation().getPMID());
            setTitle(articleType.getArticleTitle());
            setText(abstractType.getAbstractText());
            for (ArticleIdType id : pubmedArticleType.getPubmedData().getArticleIdList().getArticleIdArray()) {
                if (id.getIdType() == ArticleIdType.IdType.PMCID) {
                    setPMCID(id.getStringValue());
                } else if (id.getIdType() == ArticleIdType.IdType.DOI) {
                    setDOI(id.getStringValue());
                }
            }
            for (KeywordListType keywordListType : pubmedArticleType.getMedlineCitation().getKeywordListArray()) {
                for (KeywordType keywordType : keywordListType.getKeywordArray()) {
                    addKeyword(keywordType.getStringValue());
                }
            }
        }
    }

    /**
     * Construct from an org.bsharp.pubmed.xml.esearch.ERROR.
     */
    Abstract(org.bsharp.pubmed.xml.esearch.ERROR esearchError) {
        setError(esearchError.getvalue());
    }

    public void setError(String s) {
        this.error = s;
    }
    public String getError() {
        return error;
    }
    public void setPMID(String s) {
        this.pmid = s;
    }
    public String getPMID() {
        return pmid;
    }
    public void setTitle(String s) {
        this.title = s;
    }
    public String getTitle() {
        return title;
    }
    public void setText(String s) {
        this.text = s;
    }
    public String getText() {
        return text;
    }
    public void setPMCID(String s) {
        this.pmcid = s;
    }
    public String getPMCID() {
        return pmcid;
    }
    public void setDOI(String s) {
        this.doi = s;
    }
    public String getDOI() {
        return doi;
    }
    public void setKeywords(List<String> list) {
        this.keywords = list;
    }
    public void addKeyword(String s) {
        keywords.add(s);
    }
    public List<String> getKeywords() {
        return keywords;
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
