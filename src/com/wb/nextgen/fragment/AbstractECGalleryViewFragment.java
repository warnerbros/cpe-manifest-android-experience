package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.activity.AbstractECView;
import com.wb.nextgen.data.MovieMetaData;

/**
 * Created by gzcheng on 7/13/16.
 */
abstract public class AbstractECGalleryViewFragment extends AbstractNextGenFragment  {
    private TextView galleryNameTextView;
    protected MovieMetaData.ECGalleryItem currentGallery;
    private boolean shouldHideMetaData = false;
    private ImageButton fullscreenToggleBtn;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        galleryNameTextView = (TextView)view.findViewById(R.id.ec_content_name);

        if (galleryNameTextView != null){
            galleryNameTextView.setVisibility(shouldHideMetaData ? View.GONE : View.VISIBLE);
        }

        fullscreenToggleBtn = (ImageButton)view.findViewById(R.id.gallery_fullscreen_toggle);

        if (fullscreenToggleBtn != null){
            fullscreenToggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof AbstractECView) {
                        ((AbstractECView) getActivity()).onRequestToggleFullscreen();
                    }
                }
            });
        }
    }

    public void setCurrentGallery(MovieMetaData.ECGalleryItem gallery){
        currentGallery = gallery;
        if (galleryNameTextView != null){
            galleryNameTextView.setText(gallery.getTitle());
        }
    }


    public void setShouldHideMetaData(boolean bHide){
        shouldHideMetaData = bHide;
    }

    public void onRequestToggleFullscreen(boolean isContentFullScreen){
        galleryNameTextView.setVisibility(isContentFullScreen && !shouldHideMetaData? View.GONE : View.VISIBLE);
        fullscreenToggleBtn.setImageDrawable(getResources().getDrawable(isContentFullScreen ? R.drawable.controller_shrink : R.drawable.controller_expand));

    }
}
