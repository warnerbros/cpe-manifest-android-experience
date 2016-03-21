//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.08 at 03:02:05 PM PST 
//


package com.wb.nextgen.parser.md.schema.v2_3;

import java.util.ArrayList;
import java.util.List;
import com.wb.nextgen.parser.XmlAccessType;
import com.wb.nextgen.parser.XmlAccessorType;
import com.wb.nextgen.parser.XmlElement;
import com.wb.nextgen.parser.XmlSeeAlso;
import com.wb.nextgen.parser.XmlType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryAudioType;


/**
 * <p>Java class for DigitalAssetAudioData-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DigitalAssetAudioData-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Type" type="{http://www.movielabs.com/schema/md/v2.3/md}string-Audio-Type" minOccurs="0"/&gt;
 *         &lt;element name="Encoding" type="{http://www.movielabs.com/schema/md/v2.3/md}DigitalAssetAudioEncoding-type" minOccurs="0"/&gt;
 *         &lt;element name="Language" type="{http://www.movielabs.com/schema/md/v2.3/md}DigitalAssetAudioLanguage-type"/&gt;
 *         &lt;element name="Channels" type="{http://www.movielabs.com/schema/md/v2.3/md}string-Audio-Channels" minOccurs="0"/&gt;
 *         &lt;element name="TrackReference" type="{http://www.movielabs.com/schema/md/v2.3/md}string-TrackReference" minOccurs="0"/&gt;
 *         &lt;element name="TrackIdentifier" type="{http://www.movielabs.com/schema/md/v2.3/md}ContentIdentifier-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Private" type="{http://www.movielabs.com/schema/md/v2.3/md}PrivateData-type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DigitalAssetAudioData-type", propOrder = {
    "description",
    "type",
    "encoding",
    "language",
    "channels",
    "trackReference",
    "trackIdentifier",
    "_private"
})
@XmlSeeAlso({
    InventoryAudioType.class
})
public class DigitalAssetAudioDataType {

    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Type")
    protected String type;
    @XmlElement(name = "Encoding")
    protected DigitalAssetAudioEncodingType encoding;
    @XmlElement(name = "Language", required = true)
    protected DigitalAssetAudioLanguageType language;
    @XmlElement(name = "Channels")
    protected String channels;
    @XmlElement(name = "TrackReference")
    protected String trackReference;
    @XmlElement(name = "TrackIdentifier")
    protected List<ContentIdentifierType> trackIdentifier;
    @XmlElement(name = "Private")
    protected PrivateDataType _private;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link DigitalAssetAudioEncodingType }
     *     
     */
    public DigitalAssetAudioEncodingType getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigitalAssetAudioEncodingType }
     *     
     */
    public void setEncoding(DigitalAssetAudioEncodingType value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link DigitalAssetAudioLanguageType }
     *     
     */
    public DigitalAssetAudioLanguageType getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigitalAssetAudioLanguageType }
     *     
     */
    public void setLanguage(DigitalAssetAudioLanguageType value) {
        this.language = value;
    }

    /**
     * Gets the value of the channels property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChannels() {
        return channels;
    }

    /**
     * Sets the value of the channels property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChannels(String value) {
        this.channels = value;
    }

    /**
     * Gets the value of the trackReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrackReference() {
        return trackReference;
    }

    /**
     * Sets the value of the trackReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrackReference(String value) {
        this.trackReference = value;
    }

    /**
     * Gets the value of the trackIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trackIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrackIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContentIdentifierType }
     * 
     * 
     */
    public List<ContentIdentifierType> getTrackIdentifier() {
        if (trackIdentifier == null) {
            trackIdentifier = new ArrayList<ContentIdentifierType>();
        }
        return this.trackIdentifier;
    }

    /**
     * Gets the value of the private property.
     * 
     * @return
     *     possible object is
     *     {@link PrivateDataType }
     *     
     */
    public PrivateDataType getPrivate() {
        return _private;
    }

    /**
     * Sets the value of the private property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrivateDataType }
     *     
     */
    public void setPrivate(PrivateDataType value) {
        this._private = value;
    }

}