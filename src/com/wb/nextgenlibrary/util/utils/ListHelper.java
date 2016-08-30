package com.wb.nextgenlibrary.util.utils;

import java.util.ArrayList;
import java.util.List;

public class ListHelper {

    /** Shadow copy */
    public static <T> List<T> copy(List<T> source) {
        List<T> destination = new ArrayList<T>(source.size());
        destination.addAll(source);
        return destination;
    }

    /** Shadow copy */
    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> clone(ArrayList<T> source) {
        return (ArrayList<T>) source.clone();
    }

}
