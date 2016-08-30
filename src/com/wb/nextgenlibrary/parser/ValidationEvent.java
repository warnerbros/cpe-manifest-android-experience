package com.wb.nextgenlibrary.parser;

/**
 * Created by gzcheng on 3/16/16.
 */
public interface ValidationEvent {

    /**
     * Conditions that are not errors or fatal errors as defined by the
     * XML 1.0 recommendation
     */
    public static final int WARNING     = 0;

    /**
     * Conditions that correspond to the definition of "error" in section
     * 1.2 of the W3C XML 1.0 Recommendation
     */
    public static final int ERROR       = 1;

    /**
     * Conditions that correspond to the definition of "fatal error" in section
     * 1.2 of the W3C XML 1.0 Recommendation
     */
    public static final int FATAL_ERROR = 2;

    /**
     * Retrieve the severity code for this warning/error.
     *
     * <p>
     * Must be one of <tt>ValidationError.WARNING</tt>,
     * <tt>ValidationError.ERROR</tt>, or <tt>ValidationError.FATAL_ERROR</tt>.
     *
     * @return the severity code for this warning/error
     */
    public int getSeverity();

    /**
     * Retrieve the text message for this warning/error.
     *
     * @return the text message for this warning/error or null if one wasn't set
     */
    public String getMessage();

    /**
     * Retrieve the linked exception for this warning/error.
     *
     * @return the linked exception for this warning/error or null if one
     *         wasn't set
     */
    public Throwable getLinkedException();

    /**
     * Retrieve the locator for this warning/error.
     *
     * @return the locator that indicates where the warning/error occurred
     */
    public ValidationEventLocator getLocator();

}

