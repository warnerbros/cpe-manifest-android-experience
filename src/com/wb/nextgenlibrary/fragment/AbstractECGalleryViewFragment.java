package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.AbstractECView;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
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

                if (shouldShowFullScreenBtn)
                    showFullScreenBtn();
                else
                    hideFullScreenBtn();

                fullscreenToggleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof AbstractECView) {
                            ((AbstractECView) getActivity()).onRequestToggleFullscreen();
                            NGEAnalyticData.reportEvent(getActivity(), AbstractECGalleryViewFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SET_IMAGE_FULLSCREEN, currentGallery.galleryId, null);
                        }
                    }
                });
            }else{
                hideFullScreenBtn();
            }
        }
    }

    String getReportContentName(){
        if (currentGallery != null)
            return currentGallery.getTitle();
        else
            return null;
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
            hideFullScreenBtn();
            if (TabletUtils.isTablet())
                closeBtn.setVisibility(View.VISIBLE);
        } else {
            showFullScreenBtn();
            if (!shouldShowCloseBtn)
                closeBtn.setVisibility(View.GONE);
        }

    }

    protected void showFullScreenBtn(){
        if (fullscreenToggleBtn != null && shouldShowFullScreenBtn)
            fullscreenToggleBtn.setVisibility(View.VISIBLE);
    }

    protected void hideFullScreenBtn(){
        if (fullscreenToggleBtn != null)
            fullscreenToggleBtn.setVisibility(View.GONE);

    }

    @Override
    public void setShouldShowFullScreenBtn(boolean bShow){
        super.setShouldShowFullScreenBtn(bShow);
        if (fullscreenToggleBtn != null){
            fullscreenToggleBtn.setVisibility(bShow ? View.VISIBLE : View.GONE);
        }
    }
}
