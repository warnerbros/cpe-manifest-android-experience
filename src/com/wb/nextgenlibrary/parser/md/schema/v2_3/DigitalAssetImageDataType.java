//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.08 at 03:02:05 PM PST 
//


package com.wb.nextgenlibrary.parser.md.schema.v2_3;

import java.util.ArrayList;
import java.util.List;
import com.wb.nextgenlibrary.parser.XmlAccessType;
import com.wb.nextgenlibrary.parser.XmlAccessorType;
import com.wb.nextgenlibrary.parser.XmlElement;
import com.wb.nextgenlibrary.parser.XmlSchemaType;
import com.wb.nextgenlibrary.parser.XmlSeeAlso;
import com.wb.nextgenlibrary.parser.XmlType;
import com.wb.nextgenlibrary.parser.adapters.CollapsedStringAdapter;
import com.wb.nextgenlibrary.parser.adapters.XmlJavaTypeAdapter;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.InventoryImageType;


/**
 * <p>Java class for DigitalAssetImageData-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DigitalAssetImageData-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Width" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Height" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Encoding" type="{http://www.movielabs.com/schema/md/v2.3/md}string-Image-Encoding"/&gt;
 *         &lt;element name="Language" type="{http://www.movielabs.com/schema/md/v2.3/md}language-redefine" minOccurs="0"/&gt;
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
@XmlType(name = "DigitalAssetImageData-type", propOrder = {
    "width",
    "height",
    "encoding",
    "language",
    "trackReference",
    "trackIdentifier",
    "_private"
})
@XmlSeeAlso({
    InventoryImageType.class
})
public class DigitalAssetImageDataType {

    @XmlElement(name = "Width")
    protected int width;
    @XmlElement(name = "Height")
    protected int height;
    @XmlElement(name = "Encoding", required = true)
    protected String encoding;
    @XmlElement(name = "Language")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String language;
    @XmlElement(name = "TrackReference")
    protected String trackReference;
    @XmlElement(name = "TrackIdentifier")
    protected List<ContentIdentifierType> trackIdentifier;
    @XmlElement(name = "Private")
    protected PrivateDataType _private;

    /**
     * Gets the value of the width property.
     * 
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     */
    public void setWidth(int value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     */
    public void setHeight(int value) {
        this.height = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
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
