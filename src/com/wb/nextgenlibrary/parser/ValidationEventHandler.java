package com.wb.nextgenlibrary.parser;

/**
 * Created by gzcheng on 3/16/16.
 */
public interface ValidationEventHandler {
    /**
     * Receive notification of a validation warning or error.
     *
     * The ValidationEvent will have a
     * {@link ValidationEventLocator ValidationEventLocator} embedded in it that
     * indicates where the error or warning occurred.
     *
     * <p>
     * If an unchecked runtime exception is thrown from this method, the JAXB
     * provider will treat it as if the method returned false and interrupt
     * the current unmarshal, validate, or marshal operation.
     *
     * @param event the encapsulated validation event information.  It is a
     * provider error if this parameter is null.
     * @return true if the JAXB Provider should attempt to continue the current
     *         unmarshal, validate, or marshal operation after handling this
     *         warning/error, false if the provider should terminate the current
     *         operation with the appropriate <tt>UnmarshalException</tt>,
     *         <tt>ValidationException</tt>, or <tt>MarshalException</tt>.
     * @throws IllegalArgumentException if the event object is null.
     */
    public boolean handleEvent( ValidationEvent event );

}

