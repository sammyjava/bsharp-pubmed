//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.04.28 at 03:40:37 PM UTC 
//


package org.bsharp.pubmed.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MedlineJournalInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MedlineJournalInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}Country" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}MedlineTA"/&gt;
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}NlmUniqueID" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/eutils}ISSNLinking" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MedlineJournalInfoType", propOrder = {
    "country",
    "medlineTA",
    "nlmUniqueID",
    "issnLinking"
})
public class MedlineJournalInfoType {

    @XmlElement(name = "Country")
    protected String country;
    @XmlElement(name = "MedlineTA", required = true)
    protected String medlineTA;
    @XmlElement(name = "NlmUniqueID")
    protected String nlmUniqueID;
    @XmlElement(name = "ISSNLinking")
    protected String issnLinking;

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the medlineTA property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedlineTA() {
        return medlineTA;
    }

    /**
     * Sets the value of the medlineTA property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedlineTA(String value) {
        this.medlineTA = value;
    }

    /**
     * Gets the value of the nlmUniqueID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNlmUniqueID() {
        return nlmUniqueID;
    }

    /**
     * Sets the value of the nlmUniqueID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNlmUniqueID(String value) {
        this.nlmUniqueID = value;
    }

    /**
     * Gets the value of the issnLinking property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getISSNLinking() {
        return issnLinking;
    }

    /**
     * Sets the value of the issnLinking property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setISSNLinking(String value) {
        this.issnLinking = value;
    }

}
