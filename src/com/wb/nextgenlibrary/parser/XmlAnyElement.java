package com.wb.nextgenlibrary.parser;

import com.wb.nextgenlibrary.W3CDomHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by gzcheng on 3/16/16.
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface XmlAnyElement {

    /**
     * Controls the unmarshaller behavior when it sees elements
     * known to the current {@link JAXBContext}.
     *
     * <h3>When false</h3>
     * <p>
     * If false, all the elements that match the property will be unmarshalled
     * to DOM, and the property will only contain DOM elements.
     *
     * <h3>When true</h3>
     * <p>
     * If true, when an element matches a property marked with {@link XmlAnyElement}
     * is known to {@link JAXBContext} (for example, there's a class with
     * {@link XmlRootElement} that has the same tag name, or there's
     * {@link XmlElementDecl} that has the same tag name),
     * the unmarshaller will eagerly unmarshal this element to the JAXB object,
     * instead of unmarshalling it to DOM. Additionally, if the element is
     * unknown but it has a known xsi:type, the unmarshaller eagerly unmarshals
     * the element to a {@link JAXBElement}, with the unknown element name and
     * the JAXBElement value is set to an instance of the JAXB mapping of the
     * known xsi:type.
     *
     * <p>
     * As a result, after the unmarshalling, the property can become heterogeneous;
     * it can have both DOM nodes and some JAXB objects at the same time.
     *
     * <p>
     * This can be used to emulate the "lax" wildcard semantics of the W3C XML Schema.
     */
    boolean lax() default false;

    /**
     * Specifies the {@link DomHandler} which is responsible for actually
     * converting XML from/to a DOM-like data structure.
     */
    Class<? extends DomHandler> value() default W3CDomHandler.class;
}

