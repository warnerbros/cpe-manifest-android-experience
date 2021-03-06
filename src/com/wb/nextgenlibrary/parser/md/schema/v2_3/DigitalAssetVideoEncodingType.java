//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.08 at 03:02:05 PM PST 
//


package com.wb.nextgenlibrary.parser.md.schema.v2_3;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.wb.nextgenlibrary.parser.XmlAccessType;
import com.wb.nextgenlibrary.parser.XmlAccessorType;
import com.wb.nextgenlibrary.parser.XmlElement;
import com.wb.nextgenlibrary.parser.XmlType;
import javax.xml.datatype.Duration;


/**
 * <p>Java class for DigitalAssetVideoEncoding-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DigitalAssetVideoEncoding-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Codec" type="{http://www.movielabs.com/schema/md/v2.3/md}string-Video-Enc-Codec"/&gt;
 *         &lt;element name="CodecType" type="{http://www.movielabs.com/schema/md/v2.3/md}string-Video-Enc-CodecType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="MPEGProfile" type="{http://www.movielabs.com/schema/md/v2.3/md}string-Video-Enc-MProfile" minOccurs="0"/&gt;
 *         &lt;element name="MPEGLevel" type="{http://www.movielabs.com/schema/md/v2.3/md}string-Video-Enc-MLevel" minOccurs="0"/&gt;
 *         &lt;element name="BitrateMax" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="BitRateAverage" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="VBR" type="{http://www.movielabs.com/schema/md/v2.3/md}string-Video-Enc-VBR" minOccurs="0"/&gt;
 *         &lt;element name="Watermark" type="{http://www.movielabs.com/schema/md/v2.3/md}DigitalAssetWatermark-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ActualLength" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DigitalAssetVideoEncoding-type", propOrder = {
    "codec",
    "codecType",
    "mpegProfile",
    "mpegLevel",
    "bitrateMax",
    "bitRateAverage",
    "vbr",
    "watermark",
    "actualLength"
})
public class DigitalAssetVideoEncodingType {

    @XmlElement(name = "Codec", required = true)
    protected String codec;
    @XmlElement(name = "CodecType")
    protected List<String> codecType;
    @XmlElement(name = "MPEGProfile")
    protected String mpegProfile;
    @XmlElement(name = "MPEGLevel")
    protected String mpegLevel;
    @XmlElement(name = "BitrateMax")
    protected BigInteger bitrateMax;
    @XmlElement(name = "BitRateAverage")
    protected BigInteger bitRateAverage;
    @XmlElement(name = "VBR")
    protected String vbr;
    @XmlElement(name = "Watermark")
    protected List<DigitalAssetWatermarkType> watermark;
    @XmlElement(name = "ActualLength")
    protected Duration actualLength;

    /**
     * Gets the value of the codec property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodec() {
        return codec;
    }

    /**
     * Sets the value of the codec property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodec(String value) {
        this.codec = value;
    }

    /**
     * Gets the value of the codecType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the codecType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCodecType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCodecType() {
        if (codecType == null) {
            codecType = new ArrayList<String>();
        }
        return this.codecType;
    }

    /**
     * Gets the value of the mpegProfile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMPEGProfile() {
        return mpegProfile;
    }

    /**
     * Sets the value of the mpegProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMPEGProfile(String value) {
        this.mpegProfile = value;
    }

    /**
     * Gets the value of the mpegLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMPEGLevel() {
        return mpegLevel;
    }

    /**
     * Sets the value of the mpegLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMPEGLevel(String value) {
        this.mpegLevel = value;
    }

    /**
     * Gets the value of the bitrateMax property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBitrateMax() {
        return bitrateMax;
    }

    /**
     * Sets the value of the bitrateMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBitrateMax(BigInteger value) {
        this.bitrateMax = value;
    }

    /**
     * Gets the value of the bitRateAverage property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBitRateAverage() {
        return bitRateAverage;
    }

    /**
     * Sets the value of the bitRateAverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBitRateAverage(BigInteger value) {
        this.bitRateAverage = value;
    }

    /**
     * Gets the value of the vbr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVBR() {
        return vbr;
    }

    /**
     * Sets the value of the vbr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVBR(String value) {
        this.vbr = value;
    }

    /**
     * Gets the value of the watermark property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the watermark property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWatermark().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DigitalAssetWatermarkType }
     * 
     * 
     */
    public List<DigitalAssetWatermarkType> getWatermark() {
        if (watermark == null) {
            watermark = new ArrayList<DigitalAssetWatermarkType>();
        }
        return this.watermark;
    }

    /**
     * Gets the value of the actualLength property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getActualLength() {
        return actualLength;
    }

    /**
     * Sets the value of the actualLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setActualLength(Duration value) {
        this.actualLength = value;
    }

}
