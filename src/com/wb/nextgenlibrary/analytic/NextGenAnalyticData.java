package com.wb.nextgenlibrary.analytic;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.activity.ActorGalleryActivity;
import com.wb.nextgenlibrary.activity.ECGalleryActivity;
import com.wb.nextgenlibrary.activity.ECVideoActivity;
import com.wb.nextgenlibrary.activity.NextGenActivity;
import com.wb.nextgenlibrary.activity.NextGenExtraActivity;
import com.wb.nextgenlibrary.activity.NextGenPlayer;
import com.wb.nextgenlibrary.activity.phone.NextGenActorsActivity_Phone;
import com.wb.nextgenlibrary.fragment.ECGalleryViewFragment;
import com.wb.nextgenlibrary.fragment.ECSceneLocationMapFragment;
import com.wb.nextgenlibrary.fragment.ECTrviaViewFragment;
import com.wb.nextgenlibrary.fragment.ECTurnTableViewFragment;
import com.wb.nextgenlibrary.fragment.ECVideoViewFragment;
import com.wb.nextgenlibrary.fragment.IMEECMapViewFragment;
import com.wb.nextgenlibrary.fragment.NextGenActorDetailFragment;
import com.wb.nextgenlibrary.fragment.TheTakeCategoryGridFragment;
import com.wb.nextgenlibrary.fragment.TheTakeFrameProductsFragment;
import com.wb.nextgenlibrary.fragment.TheTakeProductDetailFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gzcheng on 9/27/16.
 */

public class NextGenAnalyticData {

    public static enum AnalyticAction{
        ACTION_START("Start"), ACTION_CLICK("Click"), ACTION_CLOSE("Close"), ACTION_PLAY("Play");
        final String sValue;
        AnalyticAction(String value){
            sValue = value;
        }
    }

    public final static Map<Class, String> classObjectToReportNameMap = new HashMap<>();
    static{
        // ec fragments
        classObjectToReportNameMap.put(ECVideoViewFragment.class, "EC Video");
        classObjectToReportNameMap.put(ECGalleryViewFragment.class, "EC Gallery");
        classObjectToReportNameMap.put(ECSceneLocationMapFragment.class, "EC Scene Location");
        classObjectToReportNameMap.put(ECTurnTableViewFragment.class, "EC Turntable Gallery");
        classObjectToReportNameMap.put(NextGenActorDetailFragment.class, "Actor Detail");
        classObjectToReportNameMap.put(IMEECMapViewFragment.class, "IME Map");
        classObjectToReportNameMap.put(TheTakeCategoryGridFragment.class, "Shop category");
        classObjectToReportNameMap.put(TheTakeProductDetailFragment.class, "Shop Product Detail");

        // ime fragments
        classObjectToReportNameMap.put(ECTrviaViewFragment.class, "Trivia");
        classObjectToReportNameMap.put(TheTakeFrameProductsFragment.class, "IME Shop");

        // activities
        classObjectToReportNameMap.put(NextGenActivity.class, "Start Screen");
        classObjectToReportNameMap.put(ECVideoActivity.class, "EC Video Group Screen");
        classObjectToReportNameMap.put(ECGalleryActivity.class, "EC Gallery Group Screen");
        classObjectToReportNameMap.put(ActorGalleryActivity.class, "Actor Gallery");
        classObjectToReportNameMap.put(NextGenPlayer.class, "Main Movie Screen");
        classObjectToReportNameMap.put(NextGenExtraActivity.class, "Extras Screen");
        classObjectToReportNameMap.put(NextGenActorsActivity_Phone.class, "Actors Screen (Phone)");
    }

    public static void reportEvent(Activity activity, Fragment fragment, String button, AnalyticAction action, String value){
        String activityName = activity != null ? classObjectToReportNameMap.get(activity.getClass()) : "";
        String fragmentName = fragment != null ? classObjectToReportNameMap.get(fragment.getClass()) : "";

        if (NextGenExperience.getNextGenEventHandler() != null)
            NextGenExperience.getNextGenEventHandler().userEventLog(activityName, fragmentName, button, action.sValue, value);

    }
}
