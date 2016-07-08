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
/*
    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        LayoutInflater inflater2 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout wrapperLayout2 = new LinearLayout(mContext);

        inflater2.inflate(R.layout.media_player_shareclip_button, wrapperLayout2, true);
        shareClipButton = (ImageButton)wrapperLayout2.findViewById(R.id.mc_shareclip_button);
        if (mContext != null){


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(view.getWidth(), 0, 5, 20);
            FrameLayout.LayoutParams wrapperParams2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);

            addView(wrapperLayout2, wrapperParams2);

            shareClipButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (shareClipIMEEngine != null) {
                        MovieMetaData.IMEElement item = shareClipIMEEngine.getCurrentIMEElement();
                        if (mContext instanceof NextGenPlayer) {
                            ((NextGenPlayer) mContext).handleSelectedShareClip(item);
                        }
                    }
                }
            });
        }
    }*/

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
