package com.wb.nextgenlibrary.util.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @see android.net.Uri
 */
public class UrlHelper {

    /** @return A map of query keys to their values */
    public static Map<String, List<String>> getQueries(String url) {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        String[] urlParts = url.split("\\?");
        String query = urlParts.length > 1 ? urlParts[1] : urlParts[0];
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                String key = pair[0];
                String value = urlDecode(pair[1]);
                List<String> values = params.get(key);
                if (values == null) {
                    values = new ArrayList<String>();
                    params.put(key, values);
                }
                values.add(value);
            }
        }
        return params;
    }

    /** @return The first value corresponding to a key, null if unavailable */
    public static String getSingleQueryValue(String url, String key) {
        List<String> values = getQueries(url).get(key);
        return values != null && values.size() > 0 ? values.get(0) : null;
    }

    public static String getSingleQueryValue(Map<String, List<String>> queries, String key) {
        List<String> values = queries.get(key);
        return values != null && values.size() > 0 ? values.get(0) : null;
    }

    public static String urlDecode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            NextGenLogger.e(F.TAG, "UrlHelper.urlDecode", e);
            return null;
        }
    }

    public static String urlEncode(String value) {
        return urlEncode(value, null);
    }

    public static String urlEncode(String value, String defaultValue) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            NextGenLogger.e(F.TAG_GA, "UrlHelper.urlEncode", e);
            return defaultValue;
        }
    }
}
