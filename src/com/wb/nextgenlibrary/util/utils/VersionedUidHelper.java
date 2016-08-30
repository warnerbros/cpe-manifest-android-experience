package com.wb.nextgenlibrary.util.utils;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

/**
 * A helper class that supports all Android OS versions.
 */
public class VersionedUidHelper {
	
	/**
     * Hashed alphanumeric unique identifier for the device based on SHA-512 (or SHA-256 if unavailable)
     * 
     * The name "deviceHash" is somewhat a misnomer now as we are hashing off ANDROID_ID, which regenerates itself after
     * factory reset.
     */
    public static String getDeviceHash(TelephonyManager tm, ContentResolver cr) {
        return convertToSha512Hash(getUniqueIdentifier(tm, cr));
    }
    
    /**
     * @deprecated Use individual identifier
     * @return A plain-text unique identifier for ad services, using telephony device id and android_id for phones,
     *         serial number for tablets
     */
    private static String getAdUid(TelephonyManager tm, ContentResolver cr) {
        final int sdkVersion = F.API_LEVEL;
        DeviceIdentifier identifier;
        if (sdkVersion < Build.VERSION_CODES.FROYO) {
            identifier = new CupcakeDeviceIdentifier();
        } else if (Build.VERSION_CODES.HONEYCOMB <= sdkVersion && sdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            identifier = new GingerbreadDeviceIdentifier();
        } else {
            identifier = new FroyoDeviceIdentifier();
        }
        return identifier.getUid(tm, cr);
    }

    /** @return Hashed unique identifier for the device aka "udt" */
    public static String getUdt(TelephonyManager tm, ContentResolver cr) {
        return convertToNumericHash(getOsBasedDeviceIdentifier().getUid(tm, cr));
    }

    /* Order of return is ANDROID_ID if available, then telephony device id, then random id */
    private static String getUniqueIdentifier(TelephonyManager tm, ContentResolver cr) {
        String id;
        if ((id = getAndroidId(cr)) != null) {
        } else if ((id = getTelephonyDeviceId(tm)) != null) {
        } else {
            id = randomId();
        }
        return id;
    }

    /** @return Telephone device id when available, null otherwise */
    public static String getTelephonyDeviceId(TelephonyManager tm) {
        return DeviceIdentifier.getTelephonyId(tm);
    }

    /** @return Settings.Secure.ANDROID_ID when available, null otherwise */
    public static String getAndroidId(ContentResolver cr) {
        return getOsBasedDeviceIdentifier().getAndroidId(cr);
    }

    /** @return Build.SERIAL when available, null otherwise */
    public static String getSerialNumber() {
        return getOsBasedDeviceIdentifier().getSerialNumber();
    }

    /* @see Android dev blog article "How to have your (Cup)cake and eat it too" */
    private static DeviceIdentifier getOsBasedDeviceIdentifier() {
        final int sdkVersion = F.API_LEVEL;
        DeviceIdentifier identifier;
        if (sdkVersion < Build.VERSION_CODES.FROYO) {
            identifier = new CupcakeDeviceIdentifier();
        } else if (sdkVersion < Build.VERSION_CODES.GINGERBREAD) {
            identifier = new FroyoDeviceIdentifier();
        } else {
            identifier = new GingerbreadDeviceIdentifier();
        }
        return identifier;
    }

    private static String convertToNumericHash(String s) {
        return s != null && !"".equals(s) ? String.valueOf(Math.abs(s.hashCode())) : DeviceIdentifier.defaultId();
    }

    /* Helper classes */

    private abstract static class DeviceIdentifier {

        /* @return Unique identifier for the device based on telephony device id and OS recommended id */
        protected String getUid(TelephonyManager tm, ContentResolver cr) {
            String id;
            if ((id = getTelephonyId(tm)) != null) {
            } else if ((id = getRecommendedId(cr)) != null) {
            } else {
                id = defaultId();
            }
            return id;
        }

        /* OS recommended identifier */
        protected abstract String getRecommendedId(ContentResolver cr);

        /*
         * Settings.Secure.ANDROID_ID is a 64-bit quantity that is generated and stored when the device first boots. It
         * is reset when the device is wiped. It is not 100% reliable on releases of Android prior to Froyo.
         */
        protected abstract String getAndroidId(ContentResolver cr);

        /*
         * Since Android Gingerbread a serial number is available via android.os.Build.SERIAL. Devices without telephony
         * are required to report a unique device ID here; some phones may do so also.
         */
        protected abstract String getSerialNumber();

        /* Telephony device id is available for all API levels */
        protected static String getTelephonyId(TelephonyManager tm) {
            return tm.getDeviceId();
        }

        protected static String defaultId() {
            return "0";
        }
    }

    private static class GingerbreadDeviceIdentifier extends FroyoDeviceIdentifier {
        @Override
        protected String getRecommendedId(ContentResolver cr) {
            return getSerialNumber();
        }

        @Override
        /** @since API Level 9 */
        protected String getSerialNumber() {
            return Build.SERIAL;
        }
    }

    private static class FroyoDeviceIdentifier extends CupcakeDeviceIdentifier {
        @Override
        protected String getRecommendedId(ContentResolver cr) {
            return getAndroidId(cr);
        }
    }

    private static class CupcakeDeviceIdentifier extends DeviceIdentifier {
        @Override
        protected String getRecommendedId(ContentResolver cr) {
            return null;
        }

        @Override
        /** @since API Level 3 */
        protected String getAndroidId(ContentResolver cr) {
            return Secure.getString(cr, Secure.ANDROID_ID);
        }

        @Override
        protected String getSerialNumber() {
            return null;
        }
    }

    private static String convertToSha512Hash(String s) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException ex) {
                return randomId();
            }
        }
        byte[] hash = md.digest(s.getBytes());
        return String.format("%0128x", new BigInteger(1, hash));
    }
    
    private static Random r = new Random();
    private static String randomId() {
        return String.valueOf(Math.abs(r.nextLong()));
        // return UUID.randomUUID().toString();
    }
}
