package com.wb.nextgen.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.interfaces.ContentViewFullscreenRequestInterface;
import com.wb.nextgen.interfaces.NextGenPlayerInterface;
import com.wb.nextgen.videoview.ObservableVideoView;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by gzcheng on 3/16/16.
 */
public class NextGenMediaController extends CustomMediaController {

    Context mContext;
    protected View mBaseSystemUIView;
    NextGenPlayerInterface player;

    public NextGenMediaController(Context context, NextGenPlayerInterface nextGenPlayer) {
        super(new ContextThemeWrapper(context, R.style.MediaControllerStyle), false);
        player = nextGenPlayer;

        init(context);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onPlayerDestroy(){
        mBaseSystemUIView.setOnSystemUiVisibilityChangeListener(null);
        mContext = null;
        mBaseSystemUIView = null;
    }

    public boolean isFullScreen(){
        return this.getWidth() == NextGenApplication.getScreenWidth(NextGenApplication.getContext());
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
    }
/*
    @Override
    public View makeControllerView() {
        View v = super.makeControllerView();

        final int topContainerId1 = getResources().getIdentifier("mediacontroller_progress", "id", "android");
        final SeekBar seekbar = (SeekBar) findViewById(topContainerId1);

        if (seekbar != null){
            seekbar.setThumb(getResources().getDrawable(R.drawable.seek_bar_thumb));
        }
    }*/

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void init(Context context) {
        mContext = context;



        mBaseSystemUIView = ((Activity)context).getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && mBaseSystemUIView != null) {
            mBaseSystemUIView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int currentVis = mBaseSystemUIView.getSystemUiVisibility();

                    if ((currentVis & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
                        if (!isShown()) {
                            show();
                            hideShowControls(true);
                        }
                    }
                }
            });
        }
    }
/*
    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        final int topContainerId1 = getResources().getIdentifier("mediacontroller_progress", "id", "android");
        final SeekBar seekbar = (SeekBar) findViewById(topContainerId1);

        final int origPlayButtonId = getResources().getIdentifier("pause", "id", "android");
        final ImageButton origPlayBtn = (ImageButton) findViewById(origPlayButtonId);

        if (seekbar != null){
            seekbar.setThumb(getResources().getDrawable(R.drawable.seek_bar_thumb));
        }

        if (origPlayBtn != null){

        }

        LayoutInflater inflater2 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout wrapperLayout2 = new LinearLayout(mContext);

        inflater2.inflate(R.layout.media_player_playpause_button, wrapperLayout2, true);
        mPlayPauseBtn = (ImageButton)wrapperLayout2.findViewById(R.id.mc_play_pause);
        if (mContext != null){


            LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, Gravity.LEFT);

            addView(mPlayPauseBtn, params);

            mPlayPauseBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (player.isPlaying())
                        mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                    else
                        mPlayPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                }
            });
        }
    }*/



    @Override
    public void hide() {
        if (player.isPlaying()) {
            super.hide();
            if (isFullScreen())
                dismissNavHandler.sendEmptyMessage(0);
        }
    }

    Handler dismissNavHandler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            dismissSoftButtonsIfPossible();
        }
    };

    public void dismissSoftButtonsIfPossible() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            new Runnable() {

                @Override
                public void run() {
                    if (mBaseSystemUIView == null)
                        return;
                    if (isFullScreen()) {
                        hideShowControls(false);


                    }

                }
            }.run();
        }
    }

    protected void hideShowControls(boolean bShow){
        if (!bShow  ){
            mBaseSystemUIView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN); // hide nav bar
        }else{

        }

    }




}
