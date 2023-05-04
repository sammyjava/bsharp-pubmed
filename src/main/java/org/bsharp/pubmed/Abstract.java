package org.bsharp.pubmed;

import org.bsharp.pubmed.xml.pubmed.*;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;
        
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
     * Construct from a PubmedArticle
     */
    public Abstract(PubmedArticleType pubmedArticleType) {
        ArticleType articleType = pubmedArticleType.getMedlineCitation().getArticle();
        AbstractType abstractType = articleType.getAbstract();
        if (abstractType!=null) {
            this.pmid = pubmedArticleType.getMedlineCitation().getPMID();
            this.title = articleType.getArticleTitle();
            this.text = abstractType.getAbstractText();
            for (ArticleIdType id : pubmedArticleType.getPubmedData().getArticleIdList().getArticleId()) {
                switch (id.getIdType()) {
                case "pmc": this.pmcid = id.getValue(); break;
                case "doi": this.doi = id.getValue(); break;
                }
            }
            this.keywords = new ArrayList<>();
            for (KeywordListType keywordListType : pubmedArticleType.getMedlineCitation().getKeywordList()) {
                for (KeywordType keywordType : keywordListType.getKeyword()) {
                    this.keywords.add(keywordType.getValue());
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
    public static List<Abstract> esearchText(String term, int retmax, String apikey) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException {
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
    public static Abstract esearchDOI(String doi, String apikey) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException {
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
    public static List<Abstract> getAbstracts(String ids, String apikey) throws JAXBException, XMLStreamException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&rettype=abstract&id="+ids;
        if (apikey!=null) uri += "&api_key=" + apikey;
        PubmedArticleSet articleSet = getPubmedArticleSet(uri);
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
            abstractList.add(new Abstract(pubmedArticleType));
        }
        return abstractList;
    }

    /**
     * Unmarshall an org.bsharp.pubmed.xml.esearch.ESearchResult from a given URI.
     * TODO: put this in its own class.
     *
     * @param uri full PubMed esearch URI
     * @return an ESearchResult
     */
    public static org.bsharp.pubmed.xml.esearch.ESearchResult getESearchResult(String uri) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(org.bsharp.pubmed.xml.esearch.ESearchResult.class);
        return (org.bsharp.pubmed.xml.esearch.ESearchResult) context.createUnmarshaller().unmarshal(new StreamSource(uri));
    }
    
    // /**
    //  * Unmarshall a PubmedArticleSet from a given URL.
    //  * Annoyance: specific HTML like i and sup inside the content breaks the JAXB parser so we have to remove those in the XML BEFORE unmarshalling!
    //  *
    //  * @param uri full PubMed efetch URI
    //  * @return a PubmedArticleSet
    //  */
    // static PubmedArticleSet getPubmedArticleSet(String uri) throws JAXBException, XMLStreamException {
    //     // use a BufferedReader to plow through the XML
    //     URL url = new URL(uri);
    //     BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
    //     StringBuffer xml = new StringBuffer();
    //     String line;
    //     while ((line = in.readLine()) != null) {
    //         line = line
    //             .replaceAll("<i>", "")
    //             .replaceAll("</i>", "")
    //             .replaceAll("<sup>", "")
    //             .replaceAll("</sup>", "")
    //             .replaceAll("<sub>", "")
    //             .replaceAll("</sub>", "");
    //         xml.append(line);
    //     }
    //     JAXBContext context = JAXBContext.newInstance(PubmedArticleSet.class);
    //     return (PubmedArticleSet) context.createUnmarshaller().unmarshal(new StreamSource(new StringReader(xml.toString())));
    // }

    /**
     * Unmarshall a PubmedArticleSet from a given efetch URI.
     *
     * @param uri full PubMed efetch URI
     * @return a PubmedArticleSet
     */
    public static PubmedArticleSet getPubmedArticleSet(String uri) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(PubmedArticleSet.class);
        return (PubmedArticleSet) context.createUnmarshaller().unmarshal(new StreamSource(uri));
    }
    
    /**
     * Command-line utility.
     */
    public static void main(String[] args) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException  {

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
        
        if (cmd.hasOption("pmid")) {
            String pmid = cmd.getOptionValue("pmid");
            System.out.println("esummary "+pmid);
            List<Abstract> abstracts = Abstract.getAbstracts(pmid, apikey);
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
        sb.append("Title: " + title + "\n");
        sb.append("Abstract: " + "\n");
        sb.append(text + "\n");
        sb.append("Keywords: " + "\n");
        sb.append(keywords);
        return sb.toString();
    }
}
