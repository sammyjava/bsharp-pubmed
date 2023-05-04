//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.05.03 at 01:05:32 AM UTC 
//


package org.bsharp.pubmed.xml.pubmed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PubDateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PubDateType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;sequence&gt;
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}Year"/&gt;
 *           &lt;choice minOccurs="0"&gt;
 *             &lt;sequence&gt;
 *               &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}Month"/&gt;
 *               &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}Day" minOccurs="0"/&gt;
 *             &lt;/sequence&gt;
 *             &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}Season"/&gt;
 *           &lt;/choice&gt;
 *         &lt;/sequence&gt;
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}MedlineDate"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PubDateType", propOrder = {
    "year",
    "month",
    "day",
    "season",
    "medlineDate"
})
public class PubDateType {

    @XmlElement(name = "Year")
    protected String year;
    @XmlElement(name = "Month")
    protected String month;
    @XmlElement(name = "Day")
    protected String day;
    @XmlElement(name = "Season")
    protected String season;
    @XmlElement(name = "MedlineDate")
    protected String medlineDate;

    /**
     * Gets the value of the year property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYear(String value) {
        this.year = value;
    }

    /**
     * Gets the value of the month property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonth() {
        return month;
    }

    /**
     * Sets the value of the month property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonth(String value) {
        this.month = value;
    }

    /**
     * Gets the value of the day property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDay() {
        return day;
    }

    /**
     * Sets the value of the day property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDay(String value) {
        this.day = value;
    }

    /**
     * Gets the value of the season property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeason() {
        return season;
    }

    /**
     * Sets the value of the season property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeason(String value) {
        this.season = value;
    }

    /**
     * Gets the value of the medlineDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedlineDate() {
        return medlineDate;
    }

    /**
     * Sets the value of the medlineDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedlineDate(String value) {
        this.medlineDate = value;
    }

}