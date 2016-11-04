package com.wb.nextgenlibrary.activity;

import android.content.res.Configuration;
import android.os.Bundle;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NextGenAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.fragment.AbstractECGalleryViewFragment;
import com.wb.nextgenlibrary.fragment.ECGalleryViewFragment;
import com.wb.nextgenlibrary.fragment.ECTurnTableViewFragment;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.NextGenFragmentTransactionEngine;

/**
 * Created by gzcheng on 3/7/16.
 */
public class ECGalleryActivity extends AbstractECView {

    AbstractECGalleryViewFragment galleryFragment;
    NextGenFragmentTransactionEngine nextGenFragmentTransactionEngine;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nextGenFragmentTransactionEngine = new NextGenFragmentTransactionEngine(this);
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
                    nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_gallery_content_view, galleryFragment);
                } else if (galleryFragment == null){
                    galleryFragment = new ECTurnTableViewFragment();
                    nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_gallery_content_view, galleryFragment);
                }
            } else {
                if (galleryFragment != null && galleryFragment instanceof ECTurnTableViewFragment){
                    galleryFragment = new ECGalleryViewFragment();
                    super.onBackPressed();
                    nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_gallery_content_view, galleryFragment);
                } else if (galleryFragment == null){
                    galleryFragment = new ECGalleryViewFragment();
                    nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_gallery_content_view, galleryFragment);
                }
            }
            if (!TabletUtils.isTablet())
                galleryFragment.setShouldShowFullScreenBtn(false);
            galleryFragment.setCurrentGallery(galleryItem);
            NextGenAnalyticData.reportEvent(this, null, "EC Gallery", NextGenAnalyticData.AnalyticAction.ACTION_CLICK, ec.title);


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
