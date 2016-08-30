package com.wb.nextgenlibrary.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities related to postal codes.
 */
public class PostalCodeUtils {

    public static final int COUNTRY_US = 1;
    public static final int COUNTRY_UK = 2;
    public static final int COUNTRY_CA = 3;
    public static final int COUNTRY_AU = 4;

    public static Pattern CANADIAN_POSTAL_CODE_PATTERN = Pattern
            .compile("[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]\\d[ABCEGHJKLMNPRSTVWXYZabceghjklmnprstvwxyz][\\s-]?\\d[ABCEGHJKLMNPRSTVWXYZabceghjklmnprstvwxyz]\\d");
    // Eg: r3m 0k2
    public static Pattern US_ZIP_CODE_PATTERN = Pattern.compile("\\d{5}"); // Eg:
                                                                           // 94103
    public static Pattern AU_POSTAL_CODE_PATTERN = Pattern.compile("\\d{4}");
    public static Pattern UK_POSTCODE_PATTERN = Pattern.compile("[A-Z]{1,2}\\d[A-Z\\d]?( )?(\\d[ABD-HJLNP-UW-Z]{2})?"); // Eg:
                                                                                                                        // SW1A
                                                                                                                        // 0AA
                                                                                                                        // now
                                                                                                                        // allows
                                                                                                                        // no
                                                                                                                        // space
    protected static Pattern CONTAINS_DIGIT = Pattern.compile(".*\\d.*");
    protected static Pattern ALL_NUMERIC = Pattern.compile("\\d+");

    private static boolean isValueMatchesPattern(Pattern pattern, String value) {
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean isValidUnitedStatesZipCode(String value) {
        return isValueMatchesPattern(US_ZIP_CODE_PATTERN, value);
    }

    public static boolean isValidCanadianPostalCode(String value) {
        return isValueMatchesPattern(CANADIAN_POSTAL_CODE_PATTERN, value);
    }

    public static boolean isValidUnitedKingdomPostcode(String value) {
        return isValueMatchesPattern(UK_POSTCODE_PATTERN, value);
    }

    public static boolean isValidAustraliaPostcode(String value) {
        return isValueMatchesPattern(AU_POSTAL_CODE_PATTERN, value);
    }

    public static boolean isProbablePostalCode(String value) {
        return isValueMatchesPattern(CONTAINS_DIGIT, value);
    }

    public static boolean isAllNumeric(String value) {
        return isValueMatchesPattern(ALL_NUMERIC, value);
    }

    /**
     * Returns canonical Canadian Postal Code string "ccc ccc"
     * 
     * @param postalCode
     * @return
     */
    public static String getCanonicalCanadianPostalCode(String postalCode) {

        if (postalCode == null) {
            return null;
        }

        if (postalCode.length() == 6) {

            // missing space
            postalCode = postalCode.substring(0, 3) + " " + postalCode.substring(3);
        } else if (postalCode.length() == 7) {

            // ensure separator is blank space and not hyphen or anything else
            postalCode = postalCode.substring(0, 3) + " " + postalCode.substring(4);
        }

        postalCode = postalCode.toUpperCase();

        return postalCode;
    }

    public static String[] splitPostalCode(int country, String code) {
        if (country == 0 || country > COUNTRY_AU) {
            return new String[] { code, null };
        }
        code = code.trim();
        String first = code;
        String last = null;
        if (country == COUNTRY_US) {
            if (code.indexOf("-") > 0) {
                first = code.substring(0, code.indexOf("-"));
                last = code.substring(code.indexOf("-") + 1, code.length());
            } else if (isValueMatchesPattern(ALL_NUMERIC, code) && code.length() > 5) {
                first = code.substring(0, 5);
                last = code.substring(6, code.length());
            }

        } else if (country == COUNTRY_UK && code.length() <= 4) {
            first = code;
            last = null;
        } else if (country == COUNTRY_UK && code.contains(" ")) {
            String[] pair = code.split(" ");
            first = pair[0];
            last = pair[1];
        } else if (country == COUNTRY_UK && code.length() >= 5) {
            // this part is a hack to support the iphone client, which gives us
            // uk postal codes as one long string
            // without a space. will be fixed in september 2008 update.
            first = code.substring(0, code.length() - 3);
            last = code.substring(code.length() - 3, code.length());
        } else if (country == COUNTRY_CA) {
            if (code.length() == 6) {
                first = code.substring(0, 3) + " " + code.substring(3, 6);
            } else if (code.length() == 7 && code.indexOf("-") > 0) {
                first = code.substring(0, 3) + " " + code.substring(4, 7);
            }
        }
        return new String[] { first, last };
    }

    /**
     * Only supports UK, US, and CA
     */
    public static String parseZipcodeForShowtimes(String value) {

        // String postalCode = value;
        if (value == null || value.length() == 0)
            return null;
        String postal = value.trim().toUpperCase();

        String code;

        if (isValidUnitedStatesZipCode(postal)) {
            code = splitPostalCode(COUNTRY_US, postal)[0];
        } else if (isValidAustraliaPostcode(postal)) {
            code = splitPostalCode(COUNTRY_AU, postal)[0];
        } else if (isValidCanadianPostalCode(postal)) {
            code = splitPostalCode(COUNTRY_CA, postal)[0];
        } else if (isValidUnitedKingdomPostcode(postal)) {
            code = splitPostalCode(COUNTRY_UK, postal)[0];
        } else {
            return null;
        }

        return code;
    }
}