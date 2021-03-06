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
import com.wb.nextgenlibrary.parser.XmlAttribute;
import com.wb.nextgenlibrary.parser.XmlElement;
import com.wb.nextgenlibrary.parser.XmlSchemaType;
import com.wb.nextgenlibrary.parser.XmlType;
import com.wb.nextgenlibrary.parser.adapters.CollapsedStringAdapter;
import com.wb.nextgenlibrary.parser.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for BasicMetadata-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BasicMetadata-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UpdateNum" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LocalizedInfo" type="{http://www.movielabs.com/schema/md/v2.3/md}BasicMetadataInfo-type" maxOccurs="unbounded"/&gt;
 *         &lt;element name="RunLength" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/&gt;
 *         &lt;element name="ReleaseYear" type="{http://www.w3.org/2001/XMLSchema}gYear"/&gt;
 *         &lt;element name="ReleaseDate" type="{http://www.movielabs.com/schema/md/v2.3/md}YearDateOrTime-type" minOccurs="0"/&gt;
 *         &lt;element name="ReleaseHistory" type="{http://www.movielabs.com/schema/md/v2.3/md}ReleaseHistory-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="WorkType" type="{http://www.movielabs.com/schema/md/v2.3/md}string-WorkType"/&gt;
 *         &lt;element name="WorkTypeDetail" type="{http://www.movielabs.com/schema/md/v2.3/md}string-WorkTypeDetail" minOccurs="0"/&gt;
 *         &lt;element name="PictureColorType" type="{http://www.movielabs.com/schema/md/v2.3/md}ColorType-type" minOccurs="0"/&gt;
 *         &lt;element name="PictureFormat" type="{http://www.movielabs.com/schema/md/v2.3/md}string-PictureFormat" minOccurs="0"/&gt;
 *         &lt;element name="ThreeD" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="AspectRatio" type="{http://www.movielabs.com/schema/md/v2.3/md}string-AspectRatio" minOccurs="0"/&gt;
 *         &lt;element name="AltIdentifier" type="{http://www.movielabs.com/schema/md/v2.3/md}ContentIdentifier-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="RatingSet" type="{http://www.movielabs.com/schema/md/v2.3/md}ContentRating-type" minOccurs="0"/&gt;
 *         &lt;element name="People" type="{http://www.movielabs.com/schema/md/v2.3/md}BasicMetadataPeople-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CountryOfOrigin" type="{http://www.movielabs.com/schema/md/v2.3/md}Region-type" minOccurs="0"/&gt;
 *         &lt;element name="PrimarySpokenLanguage" type="{http://www.movielabs.com/schema/md/v2.3/md}language-redefine" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="OriginalLanguage" type="{http://www.movielabs.com/schema/md/v2.3/md}language-redefine" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="VersionLanguage" type="{http://www.movielabs.com/schema/md/v2.3/md}language-redefine" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="AssociatedOrg" type="{http://www.movielabs.com/schema/md/v2.3/md}AssociatedOrg-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SequenceInfo" type="{http://www.movielabs.com/schema/md/v2.3/md}ContentSequenceInfo-type" minOccurs="0"/&gt;
 *         &lt;element name="Parent" type="{http://www.movielabs.com/schema/md/v2.3/md}BasicMetadataParent-type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ContentID" use="required" type="{http://www.movielabs.com/schema/md/v2.3/md}ContentID-type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BasicMetadata-type", propOrder = {
    "updateNum",
    "localizedInfo",
    "runLength",
    "releaseYear",
    "releaseDate",
    "releaseHistory",
    "workType",
    "workTypeDetail",
    "pictureColorType",
    "pictureFormat",
    "threeD",
    "aspectRatio",
    "altIdentifier",
    "ratingSet",
    "people",
    "countryOfOrigin",
    "primarySpokenLanguage",
    "originalLanguage",
    "versionLanguage",
    "associatedOrg",
    "sequenceInfo",
    "parent"
})
public class BasicMetadataType {

    @XmlElement(name = "UpdateNum")
    protected Integer updateNum;
    @XmlElement(name = "LocalizedInfo", required = true)
    protected List<BasicMetadataInfoType> localizedInfo;
    @XmlElement(name = "RunLength")
    protected Duration runLength;
    @XmlElement(name = "ReleaseYear", required = true)
    @XmlSchemaType(name = "gYear")
    protected String releaseYear;
    @XmlElement(name = "ReleaseDate")
    @XmlSchemaType(name = "anySimpleType")
    protected String releaseDate;
    @XmlElement(name = "ReleaseHistory")
    protected List<ReleaseHistoryType> releaseHistory;
    @XmlElement(name = "WorkType", required = true)
    protected String workType;
    @XmlElement(name = "WorkTypeDetail")
    protected String workTypeDetail;
    @XmlElement(name = "PictureColorType")
    @XmlSchemaType(name = "string")
    protected ColorTypeType pictureColorType;
    @XmlElement(name = "PictureFormat")
    protected String pictureFormat;
    @XmlElement(name = "ThreeD")
    protected Boolean threeD;
    @XmlElement(name = "AspectRatio")
    protected String aspectRatio;
    @XmlElement(name = "AltIdentifier")
    protected List<ContentIdentifierType> altIdentifier;
    @XmlElement(name = "RatingSet")
    protected ContentRatingType ratingSet;
    @XmlElement(name = "People")
    protected List<BasicMetadataPeopleType> people;
    @XmlElement(name = "CountryOfOrigin")
    protected RegionType countryOfOrigin;
    @XmlElement(name = "PrimarySpokenLanguage")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected List<String> primarySpokenLanguage;
    @XmlElement(name = "OriginalLanguage")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected List<String> originalLanguage;
    @XmlElement(name = "VersionLanguage")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected List<String> versionLanguage;
    @XmlElement(name = "AssociatedOrg")
    protected List<AssociatedOrgType> associatedOrg;
    @XmlElement(name = "SequenceInfo")
    protected ContentSequenceInfoType sequenceInfo;
    @XmlElement(name = "Parent")
    protected List<BasicMetadataParentType> parent;
    @XmlAttribute(name = "ContentID", required = true)
    protected String contentID;

    /**
     * Gets the value of the updateNum property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUpdateNum() {
        return updateNum;
    }

    /**
     * Sets the value of the updateNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUpdateNum(Integer value) {
        this.updateNum = value;
    }

    /**
     * Gets the value of the localizedInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localizedInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalizedInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BasicMetadataInfoType }
     * 
     * 
     */
    public List<BasicMetadataInfoType> getLocalizedInfo() {
        if (localizedInfo == null) {
            localizedInfo = new ArrayList<BasicMetadataInfoType>();
        }
        return this.localizedInfo;
    }

    /**
     * Gets the value of the runLength property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getRunLength() {
        return runLength;
    }

    /**
     * Sets the value of the runLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setRunLength(Duration value) {
        this.runLength = value;
    }

    /**
     * Gets the value of the releaseYear property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public String getReleaseYear() {
        return releaseYear;
    }

    /**
     * Sets the value of the releaseYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReleaseYear(String value) {
        this.releaseYear = value;
    }

    /**
     * Gets the value of the releaseDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets the value of the releaseDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReleaseDate(String value) {
        this.releaseDate = value;
    }

    /**
     * Gets the value of the releaseHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the releaseHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReleaseHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReleaseHistoryType }
     * 
     * 
     */
    public List<ReleaseHistoryType> getReleaseHistory() {
        if (releaseHistory == null) {
            releaseHistory = new ArrayList<ReleaseHistoryType>();
        }
        return this.releaseHistory;
    }

    /**
     * Gets the value of the workType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkType() {
        return workType;
    }

    /**
     * Sets the value of the workType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkType(String value) {
        this.workType = value;
    }

    /**
     * Gets the value of the workTypeDetail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkTypeDetail() {
        return workTypeDetail;
    }

    /**
     * Sets the value of the workTypeDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkTypeDetail(String value) {
        this.workTypeDetail = value;
    }

    /**
     * Gets the value of the pictureColorType property.
     * 
     * @return
     *     possible object is
     *     {@link ColorTypeType }
     *     
     */
    public ColorTypeType getPictureColorType() {
        return pictureColorType;
    }

    /**
     * Sets the value of the pictureColorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorTypeType }
     *     
     */
    public void setPictureColorType(ColorTypeType value) {
        this.pictureColorType = value;
    }

    /**
     * Gets the value of the pictureFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPictureFormat() {
        return pictureFormat;
    }

    /**
     * Sets the value of the pictureFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPictureFormat(String value) {
        this.pictureFormat = value;
    }

    /**
     * Gets the value of the threeD property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isThreeD() {
        return threeD;
    }

    /**
     * Sets the value of the threeD property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setThreeD(Boolean value) {
        this.threeD = value;
    }

    /**
     * Gets the value of the aspectRatio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAspectRatio() {
        return aspectRatio;
    }

    /**
     * Sets the value of the aspectRatio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAspectRatio(String value) {
        this.aspectRatio = value;
    }

    /**
     * Gets the value of the altIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the altIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAltIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContentIdentifierType }
     * 
     * 
     */
    public List<ContentIdentifierType> getAltIdentifier() {
        if (altIdentifier == null) {
            altIdentifier = new ArrayList<ContentIdentifierType>();
        }
        return this.altIdentifier;
    }

    /**
     * Gets the value of the ratingSet property.
     * 
     * @return
     *     possible object is
     *     {@link ContentRatingType }
     *     
     */
    public ContentRatingType getRatingSet() {
        return ratingSet;
    }

    /**
     * Sets the value of the ratingSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentRatingType }
     *     
     */
    public void setRatingSet(ContentRatingType value) {
        this.ratingSet = value;
    }

    /**
     * Gets the value of the people property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the people property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPeople().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BasicMetadataPeopleType }
     * 
     * 
     */
    public List<BasicMetadataPeopleType> getPeople() {
        if (people == null) {
            people = new ArrayList<BasicMetadataPeopleType>();
        }
        return this.people;
    }

    /**
     * Gets the value of the countryOfOrigin property.
     * 
     * @return
     *     possible object is
     *     {@link RegionType }
     *     
     */
    public RegionType getCountryOfOrigin() {
        return countryOfOrigin;
    }

    /**
     * Sets the value of the countryOfOrigin property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegionType }
     *     
     */
    public void setCountryOfOrigin(RegionType value) {
        this.countryOfOrigin = value;
    }

    /**
     * Gets the value of the primarySpokenLanguage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the primarySpokenLanguage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrimarySpokenLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPrimarySpokenLanguage() {
        if (primarySpokenLanguage == null) {
            primarySpokenLanguage = new ArrayList<String>();
        }
        return this.primarySpokenLanguage;
    }

    /**
     * Gets the value of the originalLanguage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the originalLanguage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOriginalLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOriginalLanguage() {
        if (originalLanguage == null) {
            originalLanguage = new ArrayList<String>();
        }
        return this.originalLanguage;
    }

    /**
     * Gets the value of the versionLanguage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the versionLanguage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVersionLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getVersionLanguage() {
        if (versionLanguage == null) {
            versionLanguage = new ArrayList<String>();
        }
        return this.versionLanguage;
    }

    /**
     * Gets the value of the associatedOrg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the associatedOrg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssociatedOrg().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AssociatedOrgType }
     * 
     * 
     */
    public List<AssociatedOrgType> getAssociatedOrg() {
        if (associatedOrg == null) {
            associatedOrg = new ArrayList<AssociatedOrgType>();
        }
        return this.associatedOrg;
    }

    /**
     * Gets the value of the sequenceInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ContentSequenceInfoType }
     *     
     */
    public ContentSequenceInfoType getSequenceInfo() {
        return sequenceInfo;
    }

    /**
     * Sets the value of the sequenceInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentSequenceInfoType }
     *     
     */
    public void setSequenceInfo(ContentSequenceInfoType value) {
        this.sequenceInfo = value;
    }

    /**
     * Gets the value of the parent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BasicMetadataParentType }
     * 
     * 
     */
    public List<BasicMetadataParentType> getParent() {
        if (parent == null) {
            parent = new ArrayList<BasicMetadataParentType>();
        }
        return this.parent;
    }

    /**
     * Gets the value of the contentID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentID() {
        return contentID;
    }

    /**
     * Sets the value of the contentID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentID(String value) {
        this.contentID = value;
    }

}
