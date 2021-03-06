//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.08 at 03:02:05 PM PST 
//


package com.wb.nextgenlibrary.parser.md.schema.v2_3;

import com.wb.nextgenlibrary.parser.XmlAccessType;
import com.wb.nextgenlibrary.parser.XmlAccessorType;
import com.wb.nextgenlibrary.parser.XmlAttribute;
import com.wb.nextgenlibrary.parser.XmlElement;
import com.wb.nextgenlibrary.parser.XmlType;


/**
 * <p>Java class for DigitalAssetWatermark-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DigitalAssetWatermark-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Vendor" type="{http://www.movielabs.com/schema/md/v2.3/md}string-Watermark_Vendor"/&gt;
 *         &lt;element name="ProductAndVersionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="guaranteedAbsent" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DigitalAssetWatermark-type", propOrder = {
    "vendor",
    "productAndVersionID",
    "data"
})
public class DigitalAssetWatermarkType {

    @XmlElement(name = "Vendor", required = true)
    protected String vendor;
    @XmlElement(name = "ProductAndVersionID", required = true)
    protected String productAndVersionID;
    @XmlElement(name = "Data")
    protected String data;
    @XmlAttribute(name = "guaranteedAbsent")
    protected Boolean guaranteedAbsent;

    /**
     * Gets the value of the vendor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Sets the value of the vendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendor(String value) {
        this.vendor = value;
    }

    /**
     * Gets the value of the productAndVersionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductAndVersionID() {
        return productAndVersionID;
    }

    /**
     * Sets the value of the productAndVersionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductAndVersionID(String value) {
        this.productAndVersionID = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData(String value) {
        this.data = value;
    }

    /**
     * Gets the value of the guaranteedAbsent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGuaranteedAbsent() {
        return guaranteedAbsent;
    }

    /**
     * Sets the value of the guaranteedAbsent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGuaranteedAbsent(Boolean value) {
        this.guaranteedAbsent = value;
    }

}
