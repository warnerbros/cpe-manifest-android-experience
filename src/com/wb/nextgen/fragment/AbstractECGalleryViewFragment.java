package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.activity.AbstractECView;
import com.wb.nextgen.data.MovieMetaData;

/**
 * Created by gzcheng on 7/13/16.
 */
abstract public class AbstractECGalleryViewFragment extends ECViewFragment  {
    private TextView galleryNameTextView;
    protected MovieMetaData.ECGalleryItem currentGallery;
    private ImageButton fullscreenToggleBtn;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        galleryNameTextView = (TextView)view.findViewById(R.id.ec_content_name);

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

    public void onFullScreenChange(boolean bFullscreen){
        super.onFullScreenChange(bFullscreen);
        if (bFullscreen) {
            fullscreenToggleBtn.setVisibility(View.GONE);
            closeBtn.setVisibility(View.VISIBLE);
        } else {
            fullscreenToggleBtn.setVisibility(View.VISIBLE);
            closeBtn.setVisibility(View.GONE);
        }

    }
}
