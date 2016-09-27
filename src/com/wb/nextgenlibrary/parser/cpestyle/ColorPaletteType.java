//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.16 at 03:29:52 PM PDT 
//


package com.wb.nextgenlibrary.parser.cpestyle;

import com.wb.nextgenlibrary.parser.XmlAccessType;
import com.wb.nextgenlibrary.parser.XmlAccessorType;
import com.wb.nextgenlibrary.parser.XmlElement;
import com.wb.nextgenlibrary.parser.XmlType;


/**
 * <p>Java class for ColorPalette-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorPalette-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NeutralPalette"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="light"/&gt;
 *               &lt;enumeration value="dark"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Base" type="{http://www.movielabs.com/schema/md/cpestyle/v1.0/cpestyle}ColorPaletteInstance-type"/&gt;
 *         &lt;element name="Highlight" type="{http://www.movielabs.com/schema/md/cpestyle/v1.0/cpestyle}ColorPaletteInstance-type"/&gt;
 *         &lt;element name="Defocus" type="{http://www.movielabs.com/schema/md/cpestyle/v1.0/cpestyle}ColorPaletteInstance-type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorPalette-type", propOrder = {
    "neutralPalette",
    "base",
    "highlight",
    "defocus"
})
public class ColorPaletteType {

    @XmlElement(name = "NeutralPalette", required = true)
    protected String neutralPalette;
    @XmlElement(name = "Base", required = true)
    protected ColorPaletteInstanceType base;
    @XmlElement(name = "Highlight", required = true)
    protected ColorPaletteInstanceType highlight;
    @XmlElement(name = "Defocus", required = true)
    protected ColorPaletteInstanceType defocus;

    /**
     * Gets the value of the neutralPalette property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNeutralPalette() {
        return neutralPalette;
    }

    /**
     * Sets the value of the neutralPalette property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNeutralPalette(String value) {
        this.neutralPalette = value;
    }

    /**
     * Gets the value of the base property.
     * 
     * @return
     *     possible object is
     *     {@link ColorPaletteInstanceType }
     *     
     */
    public ColorPaletteInstanceType getBase() {
        return base;
    }

    /**
     * Sets the value of the base property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorPaletteInstanceType }
     *     
     */
    public void setBase(ColorPaletteInstanceType value) {
        this.base = value;
    }

    /**
     * Gets the value of the highlight property.
     * 
     * @return
     *     possible object is
     *     {@link ColorPaletteInstanceType }
     *     
     */
    public ColorPaletteInstanceType getHighlight() {
        return highlight;
    }

    /**
     * Sets the value of the highlight property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorPaletteInstanceType }
     *     
     */
    public void setHighlight(ColorPaletteInstanceType value) {
        this.highlight = value;
    }

    /**
     * Gets the value of the defocus property.
     * 
     * @return
     *     possible object is
     *     {@link ColorPaletteInstanceType }
     *     
     */
    public ColorPaletteInstanceType getDefocus() {
        return defocus;
    }

    /**
     * Sets the value of the defocus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorPaletteInstanceType }
     *     
     */
    public void setDefocus(ColorPaletteInstanceType value) {
        this.defocus = value;
    }

}