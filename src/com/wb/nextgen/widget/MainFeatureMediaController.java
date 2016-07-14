package com.wb.nextgen.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wb.nextgen.R;
import com.wb.nextgen.activity.NextGenPlayer;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.IMEElementsGroup;
import com.wb.nextgen.interfaces.ContentViewFullscreenRequestInterface;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.interfaces.NextGenPlayerInterface;
import com.wb.nextgen.model.AVGalleryIMEEngine;
import com.wb.nextgen.model.NextGenIMEEngine;

/**
 * Created by gzcheng on 3/28/16.
 */
public class MainFeatureMediaController extends  NextGenMediaController implements NextGenPlaybackStatusListener {
    ImageButton shareClipButton;
    IMEElementsGroup shareClipIMEGroup;
    AVGalleryIMEEngine shareClipIMEEngine;
    public MainFeatureMediaController(Context context, NextGenPlayerInterface player) {
        super(context, player);
    }

    public void setShareClipIMEGroup(IMEElementsGroup shareClipGroup){
        if (shareClipGroup != null) {
            shareClipIMEGroup = shareClipGroup;
            shareClipIMEEngine = new AVGalleryIMEEngine(shareClipIMEGroup.getIMEElementesList());
        }
    }

    public void hideShowControls(boolean bShow){
        super.hideShowControls(bShow);
        if (mContext instanceof Activity) {
            if (bShow) {
                ((AppCompatActivity)mContext).getSupportActionBar().show();
            } else {
                ((AppCompatActivity)mContext).getSupportActionBar().hide();
            }
        }
    }

    public void playbackStatusUpdate(NextGenPlaybackStatus playbackStatus, long timecode){
        if (shareClipIMEEngine != null){
            shareClipIMEEngine.computeCurrentIMEElement(timecode);
            MovieMetaData.IMEElement item = shareClipIMEEngine.getCurrentIMEElement();
            if (item != null){
                shareClipButton.setActivated(true);
                shareClipButton.setColorFilter(null);
            }else {
                shareClipButton.setActivated(false);
                shareClipButton.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
