//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.05.03 at 01:05:32 AM UTC 
//


package org.bsharp.pubmed.xml.pubmed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for ArticleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArticleType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Journal" type="{http://www.ncbi.nlm.nih.gov/eutils}JournalType"/&gt;
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}ArticleTitle"/&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="Pagination" type="{http://www.ncbi.nlm.nih.gov/eutils}PaginationType"/&gt;
 *             &lt;element name="ELocationID" type="{http://www.ncbi.nlm.nih.gov/eutils}ELocationIDType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="Abstract" type="{http://www.ncbi.nlm.nih.gov/eutils}AbstractType" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}Affiliation" minOccurs="0"/&gt;
 *         &lt;element name="AuthorList" type="{http://www.ncbi.nlm.nih.gov/eutils}AuthorListType" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}Language" maxOccurs="unbounded"/&gt;
 *         &lt;element name="DataBankList" type="{http://www.ncbi.nlm.nih.gov/eutils}DataBankListType" minOccurs="0"/&gt;
 *         &lt;element name="GrantList" type="{http://www.ncbi.nlm.nih.gov/eutils}GrantListType" minOccurs="0"/&gt;
 *         &lt;element name="PublicationTypeList" type="{http://www.ncbi.nlm.nih.gov/eutils}PublicationTypeListType"/&gt;
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}VernacularTitle" minOccurs="0"/&gt;
 *         &lt;element name="ArticleDate" type="{http://www.ncbi.nlm.nih.gov/eutils}ArticleDateType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="PubModel" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="Print"/&gt;
 *             &lt;enumeration value="Print-Electronic"/&gt;
 *             &lt;enumeration value="Electronic"/&gt;
 *             &lt;enumeration value="Electronic-Print"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArticleType", propOrder = {
    "journal",
    "articleTitle",
    "pagination",
    "eLocationID",
    "_abstract",
    "affiliation",
    "authorList",
    "language",
    "dataBankList",
    "grantList",
    "publicationTypeList",
    "vernacularTitle",
    "articleDate"
})
public class ArticleType {

    @XmlElement(name = "Journal", required = true)
    protected JournalType journal;
    @XmlElement(name = "ArticleTitle", required = true)
    protected String articleTitle;
    @XmlElement(name = "Pagination")
    protected PaginationType pagination;
    @XmlElement(name = "ELocationID")
    protected List<ELocationIDType> eLocationID;
    @XmlElement(name = "Abstract")
    protected AbstractType _abstract;
    @XmlElement(name = "Affiliation")
    protected String affiliation;
    @XmlElement(name = "AuthorList")
    protected AuthorListType authorList;
    @XmlElement(name = "Language", required = true)
    protected List<String> language;
    @XmlElement(name = "DataBankList")
    protected DataBankListType dataBankList;
    @XmlElement(name = "GrantList")
    protected GrantListType grantList;
    @XmlElement(name = "PublicationTypeList", required = true)
    protected PublicationTypeListType publicationTypeList;
    @XmlElement(name = "VernacularTitle")
    protected String vernacularTitle;
    @XmlElement(name = "ArticleDate")
    protected List<ArticleDateType> articleDate;
    @XmlAttribute(name = "PubModel", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pubModel;

    /**
     * Gets the value of the journal property.
     * 
     * @return
     *     possible object is
     *     {@link JournalType }
     *     
     */
    public JournalType getJournal() {
        return journal;
    }

    /**
     * Sets the value of the journal property.
     * 
     * @param value
     *     allowed object is
     *     {@link JournalType }
     *     
     */
    public void setJournal(JournalType value) {
        this.journal = value;
    }

    /**
     * Gets the value of the articleTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArticleTitle() {
        return articleTitle;
    }

    /**
     * Sets the value of the articleTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArticleTitle(String value) {
        this.articleTitle = value;
    }

    /**
     * Gets the value of the pagination property.
     * 
     * @return
     *     possible object is
     *     {@link PaginationType }
     *     
     */
    public PaginationType getPagination() {
        return pagination;
    }

    /**
     * Sets the value of the pagination property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaginationType }
     *     
     */
    public void setPagination(PaginationType value) {
        this.pagination = value;
    }

    /**
     * Gets the value of the eLocationID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eLocationID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getELocationID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ELocationIDType }
     * 
     * 
     */
    public List<ELocationIDType> getELocationID() {
        if (eLocationID == null) {
            eLocationID = new ArrayList<ELocationIDType>();
        }
        return this.eLocationID;
    }

    /**
     * Gets the value of the abstract property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractType }
     *     
     */
    public AbstractType getAbstract() {
        return _abstract;
    }

    /**
     * Sets the value of the abstract property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractType }
     *     
     */
    public void setAbstract(AbstractType value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the affiliation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAffiliation() {
        return affiliation;
    }

    /**
     * Sets the value of the affiliation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAffiliation(String value) {
        this.affiliation = value;
    }

    /**
     * Gets the value of the authorList property.
     * 
     * @return
     *     possible object is
     *     {@link AuthorListType }
     *     
     */
    public AuthorListType getAuthorList() {
        return authorList;
    }

    /**
     * Sets the value of the authorList property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthorListType }
     *     
     */
    public void setAuthorList(AuthorListType value) {
        this.authorList = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the language property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLanguage() {
        if (language == null) {
            language = new ArrayList<String>();
        }
        return this.language;
    }

    /**
     * Gets the value of the dataBankList property.
     * 
     * @return
     *     possible object is
     *     {@link DataBankListType }
     *     
     */
    public DataBankListType getDataBankList() {
        return dataBankList;
    }

    /**
     * Sets the value of the dataBankList property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataBankListType }
     *     
     */
    public void setDataBankList(DataBankListType value) {
        this.dataBankList = value;
    }

    /**
     * Gets the value of the grantList property.
     * 
     * @return
     *     possible object is
     *     {@link GrantListType }
     *     
     */
    public GrantListType getGrantList() {
        return grantList;
    }

    /**
     * Sets the value of the grantList property.
     * 
     * @param value
     *     allowed object is
     *     {@link GrantListType }
     *     
     */
    public void setGrantList(GrantListType value) {
        this.grantList = value;
    }

    /**
     * Gets the value of the publicationTypeList property.
     * 
     * @return
     *     possible object is
     *     {@link PublicationTypeListType }
     *     
     */
    public PublicationTypeListType getPublicationTypeList() {
        return publicationTypeList;
    }

    /**
     * Sets the value of the publicationTypeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublicationTypeListType }
     *     
     */
    public void setPublicationTypeList(PublicationTypeListType value) {
        this.publicationTypeList = value;
    }

    /**
     * Gets the value of the vernacularTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVernacularTitle() {
        return vernacularTitle;
    }

    /**
     * Sets the value of the vernacularTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVernacularTitle(String value) {
        this.vernacularTitle = value;
    }

    /**
     * Gets the value of the articleDate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the articleDate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArticleDate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArticleDateType }
     * 
     * 
     */
    public List<ArticleDateType> getArticleDate() {
        if (articleDate == null) {
            articleDate = new ArrayList<ArticleDateType>();
        }
        return this.articleDate;
    }

    /**
     * Gets the value of the pubModel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubModel() {
        return pubModel;
    }

    /**
     * Sets the value of the pubModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubModel(String value) {
        this.pubModel = value;
    }

}