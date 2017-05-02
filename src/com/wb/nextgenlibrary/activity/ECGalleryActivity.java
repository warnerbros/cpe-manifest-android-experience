package com.wb.nextgenlibrary.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.fragment.AbstractECGalleryViewFragment;
import com.wb.nextgenlibrary.fragment.ECGalleryViewFragment;
import com.wb.nextgenlibrary.fragment.ECTurnTableViewFragment;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.NGEFragmentTransactionEngine;

/**
 * Created by gzcheng on 3/7/16.
 */
public class ECGalleryActivity extends AbstractECView {

    AbstractECGalleryViewFragment galleryFragment;
    NGEFragmentTransactionEngine fragmentTransactionEngine;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentTransactionEngine = new NGEFragmentTransactionEngine(this);
        //galleryFrame = (ECGalleryViewFragment) getSupportFragmentManager().findFragmentById(R.id.ec_gallery_left_frame);

    }

    public int getContentViewId(){
        return R.layout.next_gen_gallery_view;
    }

    @Override
    public int getListItemViewLayoutId(){
        return R.layout.ec_gallery_list_item;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getCurrentScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            Configuration newConf = new Configuration();
            newConf.orientation = Configuration.ORIENTATION_LANDSCAPE;
            onConfigurationChanged(newConf);
        }
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

        super.onRequestToggleFullscreen();
        galleryFragment.onFullScreenChange(isContentFullScreen);

    }

    @Override
    public void switchFullScreen(boolean bFullScreen){
        super.switchFullScreen(bFullScreen);
        galleryFragment.onFullScreenChange(bFullScreen);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (fragmentTransactionEngine != null)
            fragmentTransactionEngine.onDestroy();
    }


    void onFullScreenChange(boolean bFullscreen){
    }

    @Override
    public void onLeftListItemSelected(MovieMetaData.ExperienceData ec){
        if (ec != null){
            MovieMetaData.ECGalleryItem galleryItem = ec.galleryItems.get(0);
            if (galleryItem.isTurnTable()){
                if (galleryFragment != null && galleryFragment instanceof ECGalleryViewFragment){
                    galleryFragment = new ECTurnTableViewFragment();
                    super.onBackPressed();
                    fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_gallery_content_view, galleryFragment);
                } else if (galleryFragment == null){
                    galleryFragment = new ECTurnTableViewFragment();
                    fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_gallery_content_view, galleryFragment);
                }
            } else {
                if (galleryFragment != null && galleryFragment instanceof ECTurnTableViewFragment){
                    galleryFragment = new ECGalleryViewFragment();
                    super.onBackPressed();
                    fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_gallery_content_view, galleryFragment);
                } else if (galleryFragment == null){
                    galleryFragment = new ECGalleryViewFragment();
                    fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_gallery_content_view, galleryFragment);
                }
            }
            if (!TabletUtils.isTablet())
                galleryFragment.setShouldShowFullScreenBtn(false);
            galleryFragment.setCurrentGallery(galleryItem);
            NGEAnalyticData.reportEvent(this, null, NGEAnalyticData.AnalyticAction.ACTION_SELECT_IMAGE_GALLERY, galleryItem.galleryId, null);


           //galleryFrame.setCurrentGallery(ec.galleryItems.get(0));

        }
    }

    @Override
    public void onBackPressed(){
        if (isContentFullScreen){   // exit full screen if it's full screen
            onRequestToggleFullscreen();
        }else
            finish();
    }
}
