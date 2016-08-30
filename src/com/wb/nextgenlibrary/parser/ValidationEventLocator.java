package com.wb.nextgenlibrary.parser;

/**
 * Created by gzcheng on 3/16/16.
 */
public interface ValidationEventLocator {

    /**
     * Return the name of the XML source as a URL if available
     *
     * @return the name of the XML source as a URL or null if unavailable
     */
    public java.net.URL getURL();

    /**
     * Return the byte offset if available
     *
     * @return the byte offset into the input source or -1 if unavailable
     */
    public int getOffset();

    /**
     * Return the line number if available
     *
     * @return the line number or -1 if unavailable
     */
    public int getLineNumber();

    /**
     * Return the column number if available
     *
     * @return the column number or -1 if unavailable
     */
    public int getColumnNumber();

    /**
     * Return a reference to the object in the Java content tree if available
     *
     * @return a reference to the object in the Java content tree or null if
     *         unavailable
     */
    public java.lang.Object getObject();

    /**
     * Return a reference to the DOM Node if available
     *
     * @return a reference to the DOM Node or null if unavailable
     */
    public org.w3c.dom.Node getNode();

}
