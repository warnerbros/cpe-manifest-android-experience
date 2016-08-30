package com.wb.nextgenlibrary.util.utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;


public class StringHelper {
    private static final DecimalFormat US_INT_FORMAT = new DecimalFormat("###,###,###,###.##");

    /** Given "Hello Android World" returns "Hello Android" */
    public static String getFirstTwoWords(String s) {
        if (s == null) {
            return null;
        }
        int index = s.indexOf(" ");
        if (index > -1) {
            index = s.indexOf(" ", index + 1);
        }
        return index > -1 ? s.substring(0, index) : s;
    }

    /** Remove all characters that are not a number or a letter */
    public static String removeNonAlphaNumeric(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "");
    }

    /** Replace all characters that are not a number or a letter */
    public static String replaceNonAlphaNumeric(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "_");
    }

    /** Given "Hello world" returns "Hellow_world" */
    public static String replaceSpace(String s) {
        return s.replaceAll(" ", "_");
    }

    public static String replaceSpace(String s, String replacement) {
        return s.replaceAll(" ", replacement);
    }

    /** Remove all parentheses */
    public static String removeParentheses(String s) {
        return s.replaceAll("\\(", "").replaceAll("\\)", "");
    }



    /** Given "Hellow world" returns "Hello world." */
    public static String appendPeriod(String s) {
        return s.endsWith(".") ? s : (s + ".");
    }
    
    public static String unescapeForwardSlash(String s) {
    	return s.replaceAll("\\\\/", "/");
    }
    
    /**
     * Converts String into UTF-8;
     * 
     * @param operant string that should be regenerated with windows-1252 encoding.
     * @return new regenerated String with correct encoding.
     * @throws UnsupportedEncodingException if encoding of the operant is unsupported.
     */
    
    public static String changeEncoding(String operant) throws UnsupportedEncodingException{
		String result = new String(operant.getBytes("windows-1252"),"UTF-8");
		return result;
	}
    
    public static String changeEncodingContentLocker(String operant) throws UnsupportedEncodingException{
		String result = new String(operant.getBytes("ISO-8859-1"),"UTF-8");
//		String result = new String(operant.getBytes("UTF-8"),"UTF-8");
		return result;
	}

    /**
     * Shorten the string to given charLength and append ellipsis. Ellipsize is placed at whole words. The maximum return
     * string length is charLenght plus 3.
     */
    public static String ellipsize(String s, int charLength) {
        if (s.length() > charLength) {
            // int i = s.indexOf(" ", charLength);
            int i = s.lastIndexOf(" ", charLength);
            if (i > -1) {
                s = s.substring(0, i);
            }
            return s + "...";
        } else {
            return s;
        }
    }
    
    public static boolean isEmpty(String s) {
    	return s == null || s.length() == 0;
    }
    
    /**
     * Returns formatted SpannableStringBuilder as <b>label:</b> value
     * @param text (label: %s)
     * @param value
     * @return
     */
    public static SpannableStringBuilder getFormattedContentMetadata(String text, String value) {
    	SpannableStringBuilder ssb = new SpannableStringBuilder();
    	String formatted = String.format(text, value);
    	int idx = formatted.indexOf(value);
    	ssb.append(formatted);
    	ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, idx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    	return ssb;
    }
    
    public static String getNonSecureHTTPURL(String httpsURL) {
    	if (!isEmpty(httpsURL)){
    		return httpsURL.replaceFirst("https", "http");
    	}else
    		return "";
    }
    
    public static String decodeApostophe(String s){
    	if (!isEmpty(s)){
	    	List<Integer> asciiCodeList = new ArrayList<Integer>();
	    	StringTokenizer textTokenizer = new StringTokenizer(s, "&#");
	    	
	    	while(textTokenizer.hasMoreTokens()){
	    		String thisToken = textTokenizer.nextToken();
	    		if (thisToken.length() >=3){
	    		String asciiCode = thisToken.substring(0, 3);
		    		try{
		    			Integer ascii = Integer.parseInt(asciiCode);
		    			if (!asciiCodeList.contains(ascii))
		    				asciiCodeList.add(ascii);
		    		}catch(Exception ex){
		    			
		    		}
	    		}
	    		
	    	}
	    	
	    	if (asciiCodeList.size() > 0){
	    		for(Integer ascii : asciiCodeList){
	    			String asciiString = (ascii.intValue() < 100) ? (ascii.intValue() < 10) ? "00"+ ascii.toString() : "0" + ascii.toString() : ascii.toString();
	    			
	    			s = s.replace("&#" + asciiString + ";", Character.toString((char) ascii.intValue()));
	    		}
	    	}
    	
    	}
    	return s;
    }

    public static String formatStringWithParameterMappings(String template, String[] parameters, String[] values){
        String retString = template;
        try {
            if (!isEmpty(retString) && parameters != null && parameters.length > 0 && values != null && values.length > 0 && parameters.length == values.length) {
                for (int i = 0; i < parameters.length; i++) {
                    String beforeParam = "", paramString = "", afterParam = "";

                    String searchstring = "%s(" + parameters[i] + ")";

                    int paramPos = retString.indexOf(searchstring);
                    beforeParam = retString.substring(0, paramPos);

                    if (searchstring.length() + beforeParam.length() < retString.length())
                        afterParam = paramString.substring(searchstring.length() + 1);
                    retString = beforeParam + values[i] + afterParam;
                }
            }
        }catch(Exception ex){}
        return retString;
    }

    public static String capitalize(String string){
        if (isEmpty(string))
            return string;
        String startC = string.substring(0,1);
        return string.replaceFirst(startC, startC.toUpperCase());
    }
}
