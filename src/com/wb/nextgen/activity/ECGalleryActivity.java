package com.wb.nextgen.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;

import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.fragment.AbstractECGalleryViewFragment;
import com.wb.nextgen.fragment.ECGalleryViewFragment;
import com.wb.nextgen.fragment.ECTurnTableViewFragment;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;

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
        galleryFragment.onRequestToggleFullscreen(isContentFullScreen);

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
            galleryFragment.setCurrentGallery(galleryItem);


           //galleryFrame.setCurrentGallery(ec.galleryItems.get(0));

        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
