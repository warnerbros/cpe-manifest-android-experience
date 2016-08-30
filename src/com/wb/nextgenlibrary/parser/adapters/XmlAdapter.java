package com.wb.nextgenlibrary.parser.adapters;

/**
 * Created by gzcheng on 3/16/16.
 */
public abstract class XmlAdapter<ValueType,BoundType> {

    /**
     * Do-nothing constructor for the derived classes.
     */
    protected XmlAdapter() {}

    /**
     * Convert a value type to a bound type.
     *
     * @param v
     *      The value to be converted. Can be null.
     * @throws Exception
     *      if there's an error during the conversion. The caller is responsible for
     *      reporting the error to the user through {@link javax.xml.bind.ValidationEventHandler}.
     */
    public abstract BoundType unmarshal(ValueType v) throws Exception;

    /**
     * Convert a bound type to a value type.
     *
     * @param v
     *      The value to be convereted. Can be null.
     * @throws Exception
     *      if there's an error during the conversion. The caller is responsible for
     *      reporting the error to the user through {@link javax.xml.bind.ValidationEventHandler}.
     */
    public abstract ValueType marshal(BoundType v) throws Exception;
}
