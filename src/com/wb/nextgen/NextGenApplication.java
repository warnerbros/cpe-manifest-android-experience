package com.wb.nextgen;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.parser.ManifestXMLParser;
import com.wb.nextgen.model.NextGenSettings;
import com.wb.nextgen.parser.manifest.schema.v1_4.MediaManifestType;

import net.flixster.android.FlixsterCacheManager;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.security.auth.x500.X500Principal;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenApplication extends Application {
    private static boolean isDiagnosticMode = true;
    public static final String PREFS_NAME = "NextGenPrefs";
    private static final String PREFS_CACHEPOLICY = "PREFS_CACHEPOLICY";
    // //////////////////////////////////////////////
    // Certificate
    private static boolean debugCertExists;
    private static boolean rcCertExists;
    //public static boolean prodCert;
    private static final X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");
    private static final long RC_CERT_SERIAL_NUMBER = 0x52265208;
    private static final String PREFS_DIAGNOSTIC_MODE = "PREFS_DIAGNOSTIC_MODE";

    private static SharedPreferences sSettings;
    private static SharedPreferences.Editor sEditor;
    public static Date sToday;
    public static int sDay;
    private static Context sApplicationContext;
    private static float deviceScreenDensity = 0.0f;
    private static int deviceScreenWidth = -1;
    private static int deviceScreenHeight = -1;
    public static int sCachePolicy;
    private static String sUserAgent = null;
    private static String sVersionName = null;
    private static int sVersionCode = 0;
    private static Locale locale;
    private static String sCountryCode = null;
    private static String sClientCountryCode = null;
    private static String sClientLanguageCode = null;
    private static PackageInfo sNextGenInfo = null;

    private static NextGenSettings sAppSettings;

    private static MediaManifestType manifest;
    // ///////////////////////////////////////////////////////
    // Flixster Cache

    public static FlixsterCacheManager sFlixsterCacheManager;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            sSettings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            sDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            sApplicationContext = this.getApplicationContext();
            sFlixsterCacheManager = new FlixsterCacheManager(this);
            sCachePolicy = sSettings.getInt(PREFS_CACHEPOLICY, FlixsterCacheManager.POLICY_MEDIUM);
            /// Construct user Agent string
            sNextGenInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            //setLocalization();

            sAppSettings = new NextGenSettings();

            sVersionName = sNextGenInfo.versionName;
            sVersionCode = sNextGenInfo.versionCode;
            sUserAgent = "Android/" + sVersionName + " (Linux; U; Android " + Build.VERSION.RELEASE + "; " + locale
                    + "; " + Build.MODEL + ")";
            DemoData.parseDemoJSONData();

            manifest = new ManifestXMLParser().startParsing();

            /*
            JAXBContext jc = JAXBContext.newInstance(MediaManifestType.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<MediaManifestType> unmarshalledRoot = unmarshaller.unmarshal(new StreamSource(new File("")), MediaManifestType.class);
            MediaManifestType manifest = unmarshalledRoot.getValue();*/

            //System.out.println(manifest.getInventory().getMetadata().get(0).getBasicMetadata().getLocalizedInfo().get(0).getOriginalTitle());

        }catch (Exception ex){}
    }

    /** @return The application context */
    public static Context getContext() {
        return sApplicationContext;
    }

    public static void checkSigningCert(Context ctx) {
        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for (int i = 0; i < signatures.length; i++) {
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debugCertExists = cert.getSubjectX500Principal().equals(DEBUG_DN);
                rcCertExists = cert.getSerialNumber().longValue() == RC_CERT_SERIAL_NUMBER;
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (CertificateException e) {
        }
    }

    public static boolean isDebugBuild() {
        return true;//debugCertExists;
    }

    public static boolean isRcBuild() {
        return rcCertExists;
    }

    public static boolean isDiagnosticMode() {
        return isDiagnosticMode;
    }

    public static void enableDiagnosticMode() {
        isDiagnosticMode = true;
        setSharedPrefs(PREFS_DIAGNOSTIC_MODE + sDay, true);
    }

    private static void setSharedPrefs(String key, String value) {
        sEditor.putString(key, value);
        sEditor.commit();
    }

    private static void setSharedPrefs(String key, boolean value) {
        sEditor.putBoolean(key, value);
        sEditor.commit();
    }

    private static void setSharedPrefs(String key, int value) {
        sEditor.putInt(key, value);
        sEditor.commit();
    }

    private static void removeSharedPrefs(String key) {
        sEditor.remove(key);
        sEditor.commit();
    }

    private static void removeSharedPrefs(String... keys) {
        for (int i=0; keys != null && i<keys.length; i++) {
            sEditor.remove(keys[i]);
        }
        sEditor.commit();
    }

    public static void setCachePolicy(int policy) {
        sCachePolicy = policy;
        setSharedPrefs(PREFS_CACHEPOLICY, sCachePolicy);
    }

    public static float getScreenDensity(Context ctx) {
        if (deviceScreenDensity == 0.0f && ctx != null)
            deviceScreenDensity = ctx.getResources().getDisplayMetrics().density;
        return deviceScreenDensity;
    }

    public static int getScreenWidth(Context ctx){
        if (deviceScreenWidth == -1 && ctx != null){
            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getRealMetrics(metrics);
            deviceScreenWidth = metrics.widthPixels;
        }
        return deviceScreenWidth;
    }

    public static int getScreenHeight(Context ctx){
        if (deviceScreenHeight == -1 && ctx != null){
            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getRealMetrics(metrics);
            deviceScreenHeight = metrics.heightPixels;
        }
        return deviceScreenHeight;
    }


    public static int getCachePolicy() {
        return sCachePolicy;
    }

    public static String getUserAgent() {
        return sUserAgent;
    }

    public static String getClientCountryCode(){
        return sClientCountryCode;
    }

    public static void setClientCountryCode(String country) {
        sClientCountryCode = country;

    }

    public static Locale getLocale() {
        return Locale.US;
    }

    public static void setSubtitleOn(boolean bOnOf){
        sAppSettings.setSubtitleOn(bOnOf);
    }

    public static boolean getSubtitleOn(){
        return sAppSettings.getSubtitleOn();
    }
}
