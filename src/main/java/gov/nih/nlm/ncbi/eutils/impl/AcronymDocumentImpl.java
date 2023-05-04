/*
 * An XML document type.
 * Localname: Acronym
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AcronymDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * A document containing one Acronym(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public class AcronymDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.AcronymDocument {
    private static final long serialVersionUID = 1L;

    public AcronymDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Acronym"),
    };


    /**
     * Gets the "Acronym" element
     */
    @Override
    public java.lang.String getAcronym() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Acronym" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetAcronym() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "Acronym" element
     */
    @Override
    public void setAcronym(java.lang.String acronym) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(acronym);
        }
    }

    /**
     * Sets (as xml) the "Acronym" element
     */
    @Override
    public void xsetAcronym(org.apache.xmlbeans.XmlString acronym) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(acronym);
        }
    }
}
