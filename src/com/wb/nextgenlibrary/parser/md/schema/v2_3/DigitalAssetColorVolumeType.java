//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.08 at 03:02:05 PM PST 
//


package com.wb.nextgenlibrary.parser.md.schema.v2_3;

import java.math.BigDecimal;
import com.wb.nextgenlibrary.parser.XmlAccessType;
import com.wb.nextgenlibrary.parser.XmlAccessorType;
import com.wb.nextgenlibrary.parser.XmlElement;
import com.wb.nextgenlibrary.parser.XmlType;


/**
 * <p>Java class for DigitalAssetColorVolume-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DigitalAssetColorVolume-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PrimaryRChromaticity" type="{http://www.movielabs.com/schema/md/v2.3/md}DigitalAssetChromaticity-type"/&gt;
 *         &lt;element name="PrimaryGChromaticity" type="{http://www.movielabs.com/schema/md/v2.3/md}DigitalAssetChromaticity-type"/&gt;
 *         &lt;element name="PrimaryBChromaticity" type="{http://www.movielabs.com/schema/md/v2.3/md}DigitalAssetChromaticity-type"/&gt;
 *         &lt;element name="WhitePointChromaticity" type="{http://www.movielabs.com/schema/md/v2.3/md}DigitalAssetChromaticity-type"/&gt;
 *         &lt;element name="LuminanceMin" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="LuminanceMax" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DigitalAssetColorVolume-type", propOrder = {
    "primaryRChromaticity",
    "primaryGChromaticity",
    "primaryBChromaticity",
    "whitePointChromaticity",
    "luminanceMin",
    "luminanceMax"
})
public class DigitalAssetColorVolumeType {

    @XmlElement(name = "PrimaryRChromaticity", required = true)
    protected DigitalAssetChromaticityType primaryRChromaticity;
    @XmlElement(name = "PrimaryGChromaticity", required = true)
    protected DigitalAssetChromaticityType primaryGChromaticity;
    @XmlElement(name = "PrimaryBChromaticity", required = true)
    protected DigitalAssetChromaticityType primaryBChromaticity;
    @XmlElement(name = "WhitePointChromaticity", required = true)
    protected DigitalAssetChromaticityType whitePointChromaticity;
    @XmlElement(name = "LuminanceMin", required = true)
    protected BigDecimal luminanceMin;
    @XmlElement(name = "LuminanceMax", required = true)
    protected BigDecimal luminanceMax;

    /**
     * Gets the value of the primaryRChromaticity property.
     * 
     * @return
     *     possible object is
     *     {@link DigitalAssetChromaticityType }
     *     
     */
    public DigitalAssetChromaticityType getPrimaryRChromaticity() {
        return primaryRChromaticity;
    }

    /**
     * Sets the value of the primaryRChromaticity property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigitalAssetChromaticityType }
     *     
     */
    public void setPrimaryRChromaticity(DigitalAssetChromaticityType value) {
        this.primaryRChromaticity = value;
    }

    /**
     * Gets the value of the primaryGChromaticity property.
     * 
     * @return
     *     possible object is
     *     {@link DigitalAssetChromaticityType }
     *     
     */
    public DigitalAssetChromaticityType getPrimaryGChromaticity() {
        return primaryGChromaticity;
    }

    /**
     * Sets the value of the primaryGChromaticity property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigitalAssetChromaticityType }
     *     
     */
    public void setPrimaryGChromaticity(DigitalAssetChromaticityType value) {
        this.primaryGChromaticity = value;
    }

    /**
     * Gets the value of the primaryBChromaticity property.
     * 
     * @return
     *     possible object is
     *     {@link DigitalAssetChromaticityType }
     *     
     */
    public DigitalAssetChromaticityType getPrimaryBChromaticity() {
        return primaryBChromaticity;
    }

    /**
     * Sets the value of the primaryBChromaticity property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigitalAssetChromaticityType }
     *     
     */
    public void setPrimaryBChromaticity(DigitalAssetChromaticityType value) {
        this.primaryBChromaticity = value;
    }

    /**
     * Gets the value of the whitePointChromaticity property.
     * 
     * @return
     *     possible object is
     *     {@link DigitalAssetChromaticityType }
     *     
     */
    public DigitalAssetChromaticityType getWhitePointChromaticity() {
        return whitePointChromaticity;
    }

    /**
     * Sets the value of the whitePointChromaticity property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigitalAssetChromaticityType }
     *     
     */
    public void setWhitePointChromaticity(DigitalAssetChromaticityType value) {
        this.whitePointChromaticity = value;
    }

    /**
     * Gets the value of the luminanceMin property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLuminanceMin() {
        return luminanceMin;
    }

    /**
     * Sets the value of the luminanceMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLuminanceMin(BigDecimal value) {
        this.luminanceMin = value;
    }

    /**
     * Gets the value of the luminanceMax property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLuminanceMax() {
        return luminanceMax;
    }

    /**
     * Sets the value of the luminanceMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLuminanceMax(BigDecimal value) {
        this.luminanceMax = value;
    }

}
