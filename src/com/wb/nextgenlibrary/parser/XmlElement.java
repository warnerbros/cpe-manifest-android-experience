package com.wb.nextgenlibrary.parser;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME) @Target({FIELD, METHOD, PARAMETER})
public @interface XmlElement {
    /**
     * Name of the XML Schema element.
     * <p> If the value is "##default", then element name is derived from the
     * JavaBean property name.
     */
    String name() default "##default";

    /**
     * Customize the element declaration to be nillable.
     * <p>If nillable() is true, then the JavaBean property is
     * mapped to a XML Schema nillable element declaration.
     */
    boolean nillable() default false;

    /**
     * Customize the element declaration to be required.
     * <p>If required() is true, then Javabean property is mapped to
     * an XML schema element declaration with minOccurs="1".
     * maxOccurs is "1" for a single valued property and "unbounded"
     * for a multivalued property.
     * <p>If required() is false, then the Javabean property is mapped
     * to XML Schema element declaration with minOccurs="0".
     * maxOccurs is "1" for a single valued property and "unbounded"
     * for a multivalued property.
     */

    boolean required() default false;

    /**
     * XML target namespace of the XML Schema element.
     * <p>
     * If the value is "##default", then the namespace is determined
     * as follows:
     * <ol>
     *  <li>
     *  If the enclosing package has {@link XmlSchema} annotation,
     *  and its {@link XmlSchema#elementFormDefault() elementFormDefault}
     *  is {@link XmlNsForm#QUALIFIED QUALIFIED}, then the namespace of
     *  the enclosing class.
     *
     *  <li>
     *  Otherwise &#39;&#39; (which produces unqualified element in the default
     *  namespace.
     * </ol>
     */
    String namespace() default "##default";

    /**
     * Default value of this element.
     *
     * <p>
     * The <pre>'\u0000'</pre> value specified as a default of this annotation element
     * is used as a poor-man's substitute for null to allow implementations
     * to recognize the 'no default value' state.
     */
    String defaultValue() default "\u0000";

    /**
     * The Java class being referenced.
     */
    Class type() default DEFAULT.class;

    /**
     * Used in {@link XmlElement#type()} to
     * signal that the type be inferred from the signature
     * of the property.
     */
    static final class DEFAULT {}
}
