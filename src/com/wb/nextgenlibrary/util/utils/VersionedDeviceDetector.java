package com.wb.nextgenlibrary.util.utils;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

/**
 * A helper class that supports all Android OS versions.
 * 
 * Honeycomb tablet specs: API level 11, screen size xlarge; or API level 13+, screen size large or xlarge
 * 
 * Google TV specs: API level 12+, screen size large and tvdpi, has feature
 */
public class VersionedDeviceDetector {
    private static final String FEATURE_GTV = "com.google.android.tv";

    // private static final int SMALLEST_TABLET_WIDTH_DP = 600;

    /** Detects a Honeycomb tablet device */
    public static boolean identifyHcTablet(Resources res) {
        final int sdkVersion = F.API_LEVEL;
        if (sdkVersion < Build.VERSION_CODES.HONEYCOMB) {
            return false;
        } else {
            DonutScreenDetector screen = new DonutScreenDetector();
            if (sdkVersion < Build.VERSION_CODES.HONEYCOMB_MR2) {
                return screen.isXlarge(res);
            } else {
                // return new HoneycombMr2ScreenDetector().isScreenWidthDpGreaterThan(res, SMALLEST_TABLET_WIDTH_DP);
                return screen.isXlarge(res) || screen.isLarge(res);
            }
        }
    }

    /** Detects a Google TV device */
    public static boolean identifyGtv(Resources res, PackageManager packageManager) {
        final int sdkVersion = F.API_LEVEL;
        if (sdkVersion < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return false;
        } else {
            boolean hasGtvFeature = new EclairDetector().hasSystemFeature(packageManager, FEATURE_GTV); // !packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);
            DonutScreenDetector screenDetector = new DonutScreenDetector();
            return hasGtvFeature && screenDetector.isLarge(res) && screenDetector.isAtLeastTvdpi(res);
        }
    }

    public static boolean isAtLeastHdpi(Resources res) {
        final int sdkVersion = F.API_LEVEL;
        if (sdkVersion < Build.VERSION_CODES.DONUT) {
            return false;
        } else {
            return new DonutScreenDetector().isAtLeastHdpi(res);
        }
    }

    public static boolean isScreenLarge(Resources res) {
        final int sdkVersion = F.API_LEVEL;
        if (sdkVersion < Build.VERSION_CODES.DONUT) {
            return false;
        } else {
            return new DonutScreenDetector().isLarge(res);
        }
    }

    public static boolean isScreenXlarge(Resources res) {
        final int sdkVersion = F.API_LEVEL;
        if (sdkVersion < Build.VERSION_CODES.DONUT) {
            return false;
        } else {
            return new DonutScreenDetector().isXlarge(res);
        }
    }

    /* Compatible with API level 4 or higher */
    private static class DonutScreenDetector {
        private boolean isXlarge(Resources res) {
            return (res.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_XLARGE) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
        }

        private boolean isLarge(Resources res) {
            return (res.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE;
        }

        private boolean isAtLeastHdpi(Resources res) {
            return res.getDisplayMetrics().densityDpi >= DisplayMetrics.DENSITY_HIGH;
        }

        private boolean isAtLeastTvdpi(Resources res) {
            NextGenLogger.i(F.TAG, "DonutScreenDetector.densityDpi=" + res.getDisplayMetrics().densityDpi);
            return res.getDisplayMetrics().densityDpi >= DisplayMetrics.DENSITY_TV;
        }
    }

    /* Compatible with API level 5 or higher */
    private static class EclairDetector {
        private boolean hasSystemFeature(PackageManager packageManager, String featureName) {
            return packageManager.hasSystemFeature(featureName);
        }
    }

    /* Compatible with API level 13 or higher */
    /*
     * private static class HoneycombMr2ScreenDetector { private boolean isScreenWidthDpGreaterThan(Resources res, int
     * dp) { return res.getConfiguration().smallestScreenWidthDp >= dp; } }
     */
}
