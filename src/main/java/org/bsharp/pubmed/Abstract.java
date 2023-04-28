package org.bsharp.pubmed;

import org.bsharp.pubmed.xml.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;
        
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

/**
 * Encapsulates an Abstract as returned by a text efetch.
 * Instance variables are public, there are no setters/getters.
 *
 * Includes static methods for fetching abstracts.
 */
public class Abstract {

    public String pmid;
    public String title;
    public String text;
    public String pmcid;
    public String doi;
    public List<String> keywords;

    /**
     * Search for abstracts, given a search term, returning a comma-separated list of PMIDs or null if none found. Indicate maximum number with retmax.
     *
     * @param term the search term
     * @retmax the maximum number of articles to be returned
     * @return a comma-separated list of PMIDs
     */
    public static String searchAbstract(String term, int retmax) throws IOException, UnsupportedEncodingException, ParserConfigurationException, SAXException {
        // URL without API key
        String searchUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax="+retmax+"&term="+URLEncoder.encode(term,"UTF-8")+"[Abstract]";
        // parse the URL response
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(searchUrl);
        doc.getDocumentElement().normalize(); // recommended
        int count = Integer.parseInt(doc.getElementsByTagName("Count").item(0).getTextContent());
        if (count>0) {
            // concatenate ids onto a comma-separated string
            String ids = "";
            NodeList nl = doc.getElementsByTagName("Id");
            for (int i=0; i<nl.getLength(); i++) {
                Node n = nl.item(i);
                if (ids.length()>0) ids += ",";
                ids += n.getTextContent();
            }
            return ids;
        } else {
            return null;
        }
    }

    /**
     * Retrieve a List of Abstracts for a comma-separated list of article PMIDs.
     *
     * @param ids a comma-separated list of PMIDs
     * @return a list of Abstract objects
     */
    static List<Abstract> getAbstracts(String ids) throws IOException, JAXBException, XMLStreamException {
        // fetch URL without API key
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&rettype=abstract&id="+ids;
        // fetch the PubmedArticleSet
        PubmedArticleSet articleSet = getPubmedArticleSet(uri);
        // return abstracts
        return getAbstracts(articleSet);
    }
    
    /**
     * Return a List of Abstracts extracted from a PubmedArticleSet.
     *
     * @param articleSet a PubmedArticleSet
     * @return a list of Abstract objects
     */
    static List<Abstract> getAbstracts(PubmedArticleSet articleSet) {
        List<Abstract> abstractList = new ArrayList<>();
        for (PubmedArticleType pubmedArticleType : articleSet.getPubmedArticle()) {
            ArticleType articleType = pubmedArticleType.getMedlineCitation().getArticle();
            AbstractType abstractType = articleType.getAbstract();
            if (abstractType!=null) {
                Abstract a = new Abstract();
                abstractList.add(a);
                a.pmid = pubmedArticleType.getMedlineCitation().getPMID();
                a.title = articleType.getArticleTitle();
                a.text = abstractType.getAbstractText();
                for (ArticleIdType id : pubmedArticleType.getPubmedData().getArticleIdList().getArticleId()) {
                    switch (id.getIdType()) {
                    case "pmc": a.pmcid = id.getValue(); break;
                    case "doi": a.doi = id.getValue(); break;
                    }
                }
                a.keywords = new ArrayList<>();
                for (KeywordListType keywordListType : pubmedArticleType.getMedlineCitation().getKeywordList()) {
                    for (KeywordType keywordType : keywordListType.getKeyword()) {
                        a.keywords.add(keywordType.getValue());
                    }
                }
            }
        }
        return abstractList;
    }

    /**
     * Unmarshall a PubmedArticleSet from a given URL.
     * Annoyance: specific HTML like i and sup inside the content breaks the JAXB parser so we have to remove those in the XML BEFORE unmarshalling!
     *
     * @param uri full PubMed efetch URI
     * @return a PubmedArticleSet
     */
    static PubmedArticleSet getPubmedArticleSet(String uri) throws IOException, JAXBException, XMLStreamException {
        // use a BufferedReader to plow through the XML
        URL url = new URL(uri);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuffer xml = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null) {
            line = line
                .replaceAll("<i>", "")
                .replaceAll("</i>", "")
                .replaceAll("<sup>", "")
                .replaceAll("</sup>", "")
                .replaceAll("<sub>", "")
                .replaceAll("</sub>", "");
            xml.append(line);
        }
        JAXBContext context = JAXBContext.newInstance(PubmedArticleSet.class);
        return (PubmedArticleSet) context.createUnmarshaller().unmarshal(new StreamSource(new StringReader(xml.toString())));
    }
    
    /**
     * Command-line utility.
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, JAXBException, XMLStreamException  {
        if (args.length<2) {
            System.err.println("Usage: Abstract <retmax> <term>");
            System.exit(1);
        }
        int retmax = Integer.parseInt(args[0]);
        String term = args[1];
        
        String ids = Abstract.searchAbstract(term, retmax);
        if (ids!=null) {
            List<Abstract> abstracts = getAbstracts(ids);
            for (Abstract a : abstracts) {
                System.out.println("----------");
                System.out.println(a);
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
        sb.append("Title: " + title + "\n");
        sb.append("Abstract: " + "\n");
        sb.append(text + "\n");
        sb.append("Keywords: " + "\n");
        sb.append(keywords);
        return sb.toString();
    }
}
