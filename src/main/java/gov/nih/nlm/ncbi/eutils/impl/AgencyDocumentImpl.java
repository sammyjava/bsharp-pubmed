/*
 * An XML document type.
 * Localname: Agency
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AgencyDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * A document containing one Agency(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public class AgencyDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.AgencyDocument {
    private static final long serialVersionUID = 1L;

    public AgencyDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Agency"),
    };


    /**
     * Gets the "Agency" element
     */
    @Override
    public java.lang.String getAgency() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Agency" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetAgency() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "Agency" element
     */
    @Override
    public void setAgency(java.lang.String agency) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(agency);
        }
    }

    /**
     * Sets (as xml) the "Agency" element
     */
    @Override
    public void xsetAgency(org.apache.xmlbeans.XmlString agency) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(agency);
        }
    }
}
