package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.AbstractECView;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.util.TabletUtils;

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
            if (TabletUtils.isTablet()) {

                fullscreenToggleBtn.setVisibility(shouldShowCloseBtn ? View.VISIBLE : View.GONE);
                fullscreenToggleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof AbstractECView) {
                            ((AbstractECView) getActivity()).onRequestToggleFullscreen();
                        }
                    }
                });
            }else{
                fullscreenToggleBtn.setVisibility(View.GONE);
            }
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
            if (TabletUtils.isTablet())
                closeBtn.setVisibility(View.VISIBLE);
        } else {
            fullscreenToggleBtn.setVisibility(View.VISIBLE);
            if (!shouldShowCloseBtn)
                closeBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public void setShouldShowFullScreenBtn(boolean bShow){
        super.setShouldShowFullScreenBtn(bShow);
        if (fullscreenToggleBtn != null){
            fullscreenToggleBtn.setVisibility(bShow ? View.VISIBLE : View.GONE);
        }
    }
}
