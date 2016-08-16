package com.wb.nextgen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.wb.nextgen.activity.NextGenActivity;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.fragment.AbstractNextGenMainMovieFragment;
import com.wb.nextgen.network.BaselineApiDAO;
import com.wb.nextgen.network.NextGenCacheManager;
import com.wb.nextgen.network.TheTakeApiDAO;
import com.wb.nextgen.parser.ManifestXMLParser;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.concurrent.Worker;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;
import com.wb.nextgen.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * Created by gzcheng on 8/10/16.
 */
public class NextGenExperience {


    public static class ManifestItem{
        public final String imageUrl;
        public final String movieName;
        public final String manifestFileUrl;
        public final String appDataFileUrl;
        public final String contentId;
        ManifestItem(String movieName, String cid, String imageUrl, String manifestFileUrl, String appDataFileUrl){
            this.imageUrl = imageUrl;
            contentId = cid;
            this.manifestFileUrl = manifestFileUrl;
            this.movieName = movieName;
            this.appDataFileUrl = appDataFileUrl;
        }
    }

    static public List<ManifestItem> manifestItems;
    private static Context applicationContext;
    private static MovieMetaData movieMetaData;
    private static float deviceScreenDensity = 0.0f;
    private static int deviceScreenWidth = -1;
    private static int deviceScreenHeight = -1;
    public static int sCachePolicy;
    public static NextGenCacheManager sCacheManager;
    private static String sVersionName = null;
    private static int sVersionCode = 0;
    private static Locale locale;
    private static String sCountryCode = null;
    private static String sClientCountryCode = null;
    private static String sClientLanguageCode = null;
    private static PackageInfo sNextGenInfo = null;
    private static boolean isDiagnosticMode = true;
    private static Class<? extends AbstractNextGenMainMovieFragment> mainMovieFragmentClass;
    private static Object nextgenPlaybackObject;

    private static String sUserAgent;

    static {
        manifestItems = new ArrayList<ManifestItem>();
        manifestItems.add(new ManifestItem("Man of Steel v0.7", "urn:dece:cid:eidr-s:DAFF-8AB8-3AF0-FD3A-29EF-Q",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/EB180713D3536025E0405B0A07341ECE",
                "https://d3hu292hohbyvv.cloudfront.net/xml/mos_hls_manifest_r60-v0.7.xml",
                "https://d3hu292hohbyvv.cloudfront.net/xml/mos_appdata_locations_r60-v0.7.xml"));
        manifestItems.add(new ManifestItem("Batman vs Superman w/360", "urn:dece:cid:eidr-s:B257-8696-871C-A12B-B8C1-S",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/2C89FE061219D322E05314345B0AFE72",
                "https://d3hu292hohbyvv.cloudfront.net/xml/bvs_manifest_r60-v1.2-360.xml",
                "https://d3hu292hohbyvv.cloudfront.net/xml/bvs_appdata_locations_r60-v1.2.xml"));
        manifestItems.add(new ManifestItem("Sister", "urn:dece:cid:eidr-s:D2E8-4520-9446-BFAD-B106-4",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/2C89FE061219D322E05314345B0AFE72",
                "https://d3hu292hohbyvv.cloudfront.net/xml/sisters_extended_hls_manifest_v3-generated-spec1.5.xml",
                null));
        manifestItems.add(new ManifestItem("Minions", "urn:dece:cid:eidr-s:F1F8-3CDA-0844-0D78-E520-Q",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/2C89FE061219D322E05314345B0AFE72",
                "https://d3hu292hohbyvv.cloudfront.net/xml/minions_hls_manifest_v6-R60-generated-spec1.5.xml",
                null));


    }



    public static void startNextGenExperience(Context appContext, final Activity launcherActivity, final ManifestItem item, Object playbackObject, Class<? extends AbstractNextGenMainMovieFragment> fragmentClass){
        final ProgressDialog mDialog = ProgressDialog.show(launcherActivity, "", "Loading", false, false);

        nextgenPlaybackObject = playbackObject;

        applicationContext = appContext;
        sCacheManager = new NextGenCacheManager(applicationContext);
        mainMovieFragmentClass = fragmentClass;

        sUserAgent = "Android/" + sVersionName + " (Linux; U; Android " + Build.VERSION.RELEASE + "; " + Build.MODEL + ")";

        if (!StringHelper.isEmpty(item.manifestFileUrl)) {
            Worker.execute(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return startNextGenParsing(item);
                }
            }, new ResultListener<Boolean>() {
                public void onResult(Boolean result) {
                    launcherActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mDialog.dismiss();
                            mDialog.cancel();
                            Intent intent = new Intent(launcherActivity, NextGenActivity.class);
                            launcherActivity.startActivity(intent);
                        }
                    });

                }

                public void onException(Exception e) {
                    mDialog.dismiss();
                    mDialog.cancel();

                }
            });
        }else {
            mDialog.dismiss();
            mDialog.cancel();
        }


    }

    private static boolean startNextGenParsing(ManifestItem manifestItem){
        try{
            NextGenLogger.d("TIME_THIS", "---------------Next Test--------------");

            long systime = SystemClock.currentThreadTimeMillis();
            ManifestXMLParser.NextGenManifestData manifest = new ManifestXMLParser().startParsing(manifestItem.manifestFileUrl, manifestItem.appDataFileUrl);
            long currentTime = SystemClock.currentThreadTimeMillis() - systime;
            NextGenLogger.d("TIME_THIS", "Time to finish parsing: " + currentTime);
            movieMetaData = MovieMetaData.process(manifest);


            currentTime = SystemClock.currentThreadTimeMillis() - currentTime;
            NextGenLogger.d("TIME_THIS", "Time to finish processing: " + currentTime);

            BaselineApiDAO.init();
            TheTakeApiDAO.init();
            return true;
        }catch (Exception ex){
            NextGenLogger.e(F.TAG, ex.getLocalizedMessage());
            NextGenLogger.e(F.TAG, ex.getStackTrace().toString());
            return false;
        }
    }


    public static Context getApplicationContext(){
        return applicationContext;
    }

    public static MovieMetaData getMovieMetaData(){
        return movieMetaData;
    }

    public static Class<? extends AbstractNextGenMainMovieFragment> getMainMovieFragmentClass(){
        return mainMovieFragmentClass;
    }

    public static Object getNextgenPlaybackObject(){
        return nextgenPlaybackObject;
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

    public static void launchChromeWithUrl(String urlString){

        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setData(Uri.parse(urlString));

        try {
            applicationContext.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent.setPackage(null);
            applicationContext.startActivity(intent);
        }
    }


    public static boolean isDebugBuild() {
        return true;
    }



    public static boolean isDiagnosticMode() {
        return isDiagnosticMode;
    }

    public static void enableDiagnosticMode() {
        isDiagnosticMode = true;
        //applicationContext.setSharedPrefs(PREFS_DIAGNOSTIC_MODE + sDay, true);
    }


    public static ManifestItem findManifestItemByCID(String cid){
        for (ManifestItem item : manifestItems){
            if (cid.equals(item.contentId))
                return item;
        }
        return null;
    }
}
