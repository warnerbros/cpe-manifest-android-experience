package com.wb.nextgen.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.interfaces.ContentViewFullscreenRequestInterface;
import com.wb.nextgen.interfaces.NextGenPlayerInterface;

/**
 * Created by gzcheng on 3/9/16.
 */
public class ECMediaController extends NextGenMediaController {
    //private VideoView videoView;

    int statupSystemUIView = 0;

    ImageButton maxminButton;
    public ECMediaController(Context context, NextGenPlayerInterface player) {
        super(context, player);
        statupSystemUIView = mBaseSystemUIView.getSystemUiVisibility();
    }


    @Override
    public void requestTogggleFullScreen(){
        if (mContext instanceof ContentViewFullscreenRequestInterface) {
            ((ContentViewFullscreenRequestInterface) mContext).onRequestToggleFullscreen();
        }
    }

    public void onToggledFullScreen(boolean isFullScreen){
        if (!isFullScreen){
            mBaseSystemUIView.setSystemUiVisibility(statupSystemUIView );
        }else{
            mBaseSystemUIView.setSystemUiVisibility(mBaseSystemUIView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && mContext instanceof  Activity ){
            ((Activity)mContext).onBackPressed();
            return false;
        }else
            return super.dispatchKeyEvent(event);
    }
}
