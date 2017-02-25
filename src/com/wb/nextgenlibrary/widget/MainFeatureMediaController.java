package com.wb.nextgenlibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.wb.nextgenlibrary.interfaces.NGEPlayerInterface;

/**
 * Created by gzcheng on 3/28/16.
 */
public class MainFeatureMediaController extends  NextGenMediaController {
    ImageButton shareClipButton;
    public MainFeatureMediaController(Context context, NGEPlayerInterface player) {
        super(context, player, false);
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
}
