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


    ImageButton maxminButton;
    public ECMediaController(Context context, NextGenPlayerInterface player) {
        super(context, player);
    }
/*
    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        LayoutInflater inflater2 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout wrapperLayout2 = new LinearLayout(mContext);

        inflater2.inflate(R.layout.media_player_maxmin_button, wrapperLayout2, true);
        maxminButton = (ImageButton)wrapperLayout2.findViewById(R.id.mc_expand_shrink);
        if (mContext != null){


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(view.getWidth(), 0, 5, 20);
            FrameLayout.LayoutParams wrapperParams2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);

            addView(wrapperLayout2, wrapperParams2);

            maxminButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //Log.e("media controller", "full screen onclick");

                    //Intent i = new Intent("xyxyxyxhx");

                    //ECMediaController.this.hide();

                    if (mContext instanceof ContentViewFullscreenRequestInterface) {
                        ((ContentViewFullscreenRequestInterface) mContext).onRequestToggleFullscreen();
                    }


                }
            });
        }
    }*/

    @Override
    public void requestTogggleFullScreen(){
        if (mContext instanceof ContentViewFullscreenRequestInterface) {
            ((ContentViewFullscreenRequestInterface) mContext).onRequestToggleFullscreen();
        }
    }

    public void onToggledFullScreen(boolean isFullScreen){
        if (!isFullScreen){
            mBaseSystemUIView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_VISIBLE );
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
