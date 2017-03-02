package com.wb.nextgenlibrary.analytic;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.activity.ActorGalleryActivity;
import com.wb.nextgenlibrary.activity.ECGalleryActivity;
import com.wb.nextgenlibrary.activity.ECSceneLocationActivity;
import com.wb.nextgenlibrary.activity.ECShopItemDetailActivity;
import com.wb.nextgenlibrary.activity.ECVideoActivity;
import com.wb.nextgenlibrary.activity.OutOfMovieActivity;
import com.wb.nextgenlibrary.activity.ShopCategoryActivity;
import com.wb.nextgenlibrary.activity.StartupActivity;
import com.wb.nextgenlibrary.activity.InMovieExperience;
import com.wb.nextgenlibrary.activity.phone.NGEActorsActivity_Phone;
import com.wb.nextgenlibrary.fragment.ActorDetailFragment;
import com.wb.nextgenlibrary.fragment.ECGalleryViewFragment;
import com.wb.nextgenlibrary.fragment.ECSceneLocationMapFragment;
import com.wb.nextgenlibrary.fragment.ECVideoViewFragment;
import com.wb.nextgenlibrary.fragment.IMEECMapViewFragment;
import com.wb.nextgenlibrary.fragment.ActorListFragment;
import com.wb.nextgenlibrary.fragment.ExtraMainTableFragment;
import com.wb.nextgenlibrary.fragment.ShareClipFragment;
import com.wb.nextgenlibrary.fragment.ShopCategoryGridFragment;
import com.wb.nextgenlibrary.fragment.ShopItemDetailFragment;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gzcheng on 9/27/16.
 */

public class NGEAnalyticData {

    public static enum AnalyticAction{
        // Home Screen
        ACTION_PLAY_MOVIE("launch_ime"),
        ACTION_EXTRAS("launch_extras"),
        ACTION_BUY("launch_buy"),
        ACTION_EXIT("exit"),

        // Extras Screen
        ACTION_SELECT_TALENT("select_talent"),
        ACTION_SELECT_VIDEO_GALLERY("select_video_gallery"),
        ACTION_SELECT_IMAGE_GALLERY("select_image_gallery"),
        ACTION_SELECT_SCENE_LOCATION("select_scene_locations"),
        ACTION_SELECT_SHOPPING("select_shopping"),
        ACTION_SELECT_APP("select_app"),

        // Actor Detail Page
        ACTION_SELECT_ACTOR_GALLERY("select_gallery"),
        ACTION_SELECT_ACTOR_SOCIAL("select_social"),
        ACTION_SELECT_ACTOR_FILM("select_film"),

        // Actor Gallery
        ACTION_SELECT_IMAGE("select_image"),

        // EC Video Gallery
        ACTION_SELECT_VIDEO("select_video"),
        ACTION_SET_VIDEO_FULLSCREEN("set_video_full_screen"),

        // EC Image Gallery
        ACTION_SHARE_IMAGE("share_image"),
        ACTION_SET_IMAGE_FULLSCREEN("set_image_gallery_full_screen"),

        // Scene Location
        ACTION_SET_MAP_TYPE("set_map_type"),
        ACTION_SELECT_LOCATION_MARKER("select_location_marker"),
        ACTION_SELECT_LOCATION_THUMBNAIL("select_location_thumbnail"),

        // Shop Items
        ACTION_SELECT_CATEGORY("select_category"),
        ACTION_SELECT_PRODUCT("select_product"),
        ACTION_SHARE_PRODUCT_LINK("select_share_product_link"),
        ACTION_SELECT_SHOP_PRODUCT("select_shop_product"),

        // IME
        ACTION_SKIP_INTERSTITIAL("skip_interstitial"),
        ACTION_SHARE_VIDEO("share_video"),
        ACTION_SELECT_TRIVIA("select_trivia"),
        ACTION_SELECT_CLIP_SHARE("select_clip_share"),
        ACTION_SELECT_NEXT("select_next"),
        ACTION_SELECT_LOCATION("select_location"),
        ACTION_SELECT_PREVIOUS("select_previous"),
        ACTION_ROTATE_SCREEN_SHOW_EXTRAS("rotate_show_extras"),
        ACTION_ROTATE_SCREEN_HIDE_EXTRAS("rotate_hide_extras")

        ;


        final String sValue;
        AnalyticAction(String value){
            sValue = value;
        }
    }


    public final static Map<Class<? extends Activity>, Map<Class<? extends Fragment>, String>> classObjectToReportNameMap = new HashMap<>();
    static{
        Map<Class<? extends Fragment>, String> map;
        // StartupActivity
        map = new HashMap<>() ;
        map.put(null, "home_action");
        classObjectToReportNameMap.put(StartupActivity.class, map);

        // OutOfMovieActivity
        map = new HashMap<>() ;
        //map.put(null, "extras_action");
        map.put(ActorListFragment.class, "extras_action");
        map.put(ExtraMainTableFragment.class, "extras_action");
        map.put(ActorDetailFragment.class, "extras_talent_action");
        classObjectToReportNameMap.put(OutOfMovieActivity.class, map);

        // ActorGalleryActivity
        map = new HashMap<>() ;
        map.put(null, "extras_talent_gallery_action");
        classObjectToReportNameMap.put(ActorGalleryActivity.class, map);

        // ECVideoActivity
        map = new HashMap<>() ;
        map.put(null, "extras_video_gallery_action");
        map.put(ECVideoViewFragment.class, "extras_video_gallery_action");
        classObjectToReportNameMap.put(ECVideoActivity.class, map);

        // ECGalleryActivity
        map = new HashMap<>() ;
        map.put(null, "extras_image_gallery_action");
        map.put(ECGalleryViewFragment.class, "extras_image_gallery_action");
        classObjectToReportNameMap.put(ECGalleryActivity.class, map);

        // ECSceneLocationActivity
        map = new HashMap<>() ;
        map.put(null, "extras_scene_locations_action");
        map.put(ECSceneLocationMapFragment.class, "extras_scene_locations_action");
        map.put(ECVideoViewFragment.class, "extras_scene_locations_action");
        map.put(ECGalleryViewFragment.class, "extras_scene_locations_action");
        classObjectToReportNameMap.put(ECSceneLocationActivity.class, map);

        // InMovieExperience
        map = new HashMap<>() ;
        map.put(null, "ime_action");
        map.put(ECSceneLocationMapFragment.class, "ime_action");
        map.put(ECGalleryViewFragment.class, "ime_action");
        map.put(ECVideoViewFragment.class, "ime_action");
        map.put(IMEECMapViewFragment.class, "ime_action");
        map.put(ShareClipFragment.class, "ime_clipshare_action");
        map.put(ShopItemDetailFragment.class, "ime_shop_action");
        classObjectToReportNameMap.put(InMovieExperience.class, map);

        // NGEActorsActivity_Phone
        map = new HashMap<>() ;
        map.put(null, "extras_talent_action");
        map.put(ActorDetailFragment.class, "extras_talent_action");
        classObjectToReportNameMap.put(NGEActorsActivity_Phone.class, map);

        map = new HashMap<>() ;
        map.put(null, "extras_shop_action");
        map.put(ShopCategoryGridFragment.class, "extras_shop_action");
        classObjectToReportNameMap.put(ShopCategoryActivity.class, map);

        // ECShopItemDetailActivity
        map = new HashMap<>() ;
        map.put(null, "extras_shop_action");
        map.put(ShopItemDetailFragment.class, "extras_shop_action");
        classObjectToReportNameMap.put(ECShopItemDetailActivity.class, map);
    }

    public static void reportEvent(Activity activity, Fragment fragment, AnalyticAction action, String idValue, String nameValue){
        String activityName = null;
        if (activity != null){
            if (fragment != null){
                activityName = classObjectToReportNameMap.get(activity.getClass()).get(fragment.getClass());
            } else {
                activityName = classObjectToReportNameMap.get(activity.getClass()).get(null);
            }
        }
        if (activity != null && StringHelper.isEmpty(activityName)){
            activityName = activity.getClass().toString();
        }

        if (NextGenExperience.getNextGenEventHandler() != null)
            NextGenExperience.getNextGenEventHandler().userEventLog(NextGenExperience.getNextgenPlaybackObject(), activityName, action.sValue, idValue, nameValue);

    }
}
