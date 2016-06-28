package com.wb.nextgen.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.fragment.ECGalleryViewFragment;

/**
 * Created by gzcheng on 3/7/16.
 */
public class ECGalleryActivity extends AbstractECView {
    ECGalleryViewFragment galleryFrame;
    /*private ViewPager galleryViewPager;
    private MovieMetaData.ExperienceData currentGallery;
    private GalleryPagerAdapter adapter;
    private ImageButton fullscreenToggleBtn;*/

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        galleryFrame = (ECGalleryViewFragment) getSupportFragmentManager().findFragmentById(R.id.ec_gallery_left_frame);

    }

    public int getContentViewId(){
        return R.layout.next_gen_gallery_view;
    }

    @Override
    public int getListItemViewLayoutId(){
        return R.layout.ec_gallery_list_item;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && isContentFullScreen){
            newConfig.orientation = Configuration.ORIENTATION_LANDSCAPE;
            super.onConfigurationChanged(newConfig);
        }else{
            super.onConfigurationChanged(newConfig);
        }
    }
    @Override
    public void onRequestToggleFullscreen(){
/*
        if (!isContentFullScreen){    // make it full screen
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {                     // shrink it
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }*/
        super.onRequestToggleFullscreen();
        galleryFrame.onRequestToggleFullscreen(isContentFullScreen);

    }


    void onFullScreenChange(boolean bFullscreen){

    }

    @Override
    public void onLeftListItemSelected(MovieMetaData.ExperienceData ec){
        if (ec != null){
           galleryFrame.setCurrentGallery(ec.galleryItems.get(0));

        }
    }
}
