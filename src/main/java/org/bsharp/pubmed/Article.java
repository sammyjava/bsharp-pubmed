package org.bsharp.pubmed;

/**
 * Encapsulates a PubMed Central Article with a lot less data.
 */
public class Article {

    String abstractType;
    
    /**
     * Construct from an ArticleType
     */
    public Article(ArticleType articleType) {
        System.out.println(articleType.getArticleType());
    }

    public void setAbstractType(String s) {
        abstractType = s;
    }
    public String getAbstractType() {
        return abstractType;
    }


    @Override
    public String toString() {
        return abstractType;
    }
}
