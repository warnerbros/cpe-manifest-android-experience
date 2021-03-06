//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.16 at 03:29:52 PM PDT 
//


package com.wb.nextgenlibrary.parser.cpestyle;

import com.wb.nextgenlibrary.parser.XmlAccessType;
import com.wb.nextgenlibrary.parser.XmlAccessorType;
import com.wb.nextgenlibrary.parser.XmlAttribute;
import com.wb.nextgenlibrary.parser.XmlElement;
import com.wb.nextgenlibrary.parser.XmlType;


/**
 * <p>Java class for Theme-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Theme-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ColorPalette" type="{http://www.movielabs.com/schema/md/cpestyle/v1.0/cpestyle}ColorPalette-type" minOccurs="0"/&gt;
 *         &lt;element name="ButtonImageSet" type="{http://www.movielabs.com/schema/md/cpestyle/v1.0/cpestyle}ButtonSet-type" minOccurs="0"/&gt;
 *         &lt;element name="Fonts" type="{http://www.movielabs.com/schema/md/cpestyle/v1.0/cpestyle}Fonts-type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ThemeID" use="required" type="{http://www.movielabs.com/schema/md/v2.4/md}id-type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Theme-type", propOrder = {
    "colorPalette",
    "buttonImageSet",
    "fonts"
})
public class ThemeType {

    @XmlElement(name = "ColorPalette")
    protected ColorPaletteType colorPalette;
    @XmlElement(name = "ButtonImageSet")
    protected ButtonSetType buttonImageSet;
    @XmlElement(name = "Fonts")
    protected FontsType fonts;
    @XmlAttribute(name = "ThemeID", required = true)
    protected String themeID;

    /**
     * Gets the value of the colorPalette property.
     * 
     * @return
     *     possible object is
     *     {@link ColorPaletteType }
     *     
     */
    public ColorPaletteType getColorPalette() {
        return colorPalette;
    }

    /**
     * Sets the value of the colorPalette property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorPaletteType }
     *     
     */
    public void setColorPalette(ColorPaletteType value) {
        this.colorPalette = value;
    }

    /**
     * Gets the value of the buttonImageSet property.
     * 
     * @return
     *     possible object is
     *     {@link ButtonSetType }
     *     
     */
    public ButtonSetType getButtonImageSet() {
        return buttonImageSet;
    }

    /**
     * Sets the value of the buttonImageSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link ButtonSetType }
     *     
     */
    public void setButtonImageSet(ButtonSetType value) {
        this.buttonImageSet = value;
    }

    /**
     * Gets the value of the fonts property.
     * 
     * @return
     *     possible object is
     *     {@link FontsType }
     *     
     */
    public FontsType getFonts() {
        return fonts;
    }

    /**
     * Sets the value of the fonts property.
     * 
     * @param value
     *     allowed object is
     *     {@link FontsType }
     *     
     */
    public void setFonts(FontsType value) {
        this.fonts = value;
    }

    /**
     * Gets the value of the themeID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThemeID() {
        return themeID;
    }

    /**
     * Sets the value of the themeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThemeID(String value) {
        this.themeID = value;
    }

}
