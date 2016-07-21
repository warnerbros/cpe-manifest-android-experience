package com.wb.nextgen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.NextGenStyle;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.NextGenLogger;

import net.hockeyapp.android.CrashManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gzcheng on 1/7/16.
 */
public class NextGenActivity extends NextGenHideStatusBarActivity implements View.OnClickListener {
    // wrapper of ProfileViewFragment

    VideoView startupVideoView;

    ImageButton playMovieButton;
    ImageButton extraButton;
    View buttonsLayout;

    private int videoLoopPoint = 0;
    private int buttonAnimationStartTime = 0;
    //final static int TOP_RATIO = 52;
    //final static int LEFT_RATIO = 31;
    //LinearLayout leftPadding, topPadding, rightPortion;
    //RelativeLayout startUpVideoViewFrame;

    private TimerTask startUpTimerTask;
    private Timer startUpTimer;

    private boolean isStartUp = true;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.next_gen_startup_view);

        startupVideoView = (VideoView)findViewById(R.id.startup_video_view);
        //startUpVideoViewFrame = (RelativeLayout)findViewById(R.id.startuo_video_view_frame);


        /*leftPadding = (LinearLayout)findViewById(R.id.startup_left_padding);
        topPadding = (LinearLayout)findViewById(R.id.startup_top_padding);
        rightPortion = (LinearLayout)findViewById(R.id.startup_right_portion);*/

        buttonsLayout = findViewById(R.id.startup_buttons_layout);
        if (buttonsLayout != null){
            buttonsLayout.setVisibility(View.GONE);
        }

        ImageView bg = (ImageView)findViewById(R.id.next_gen_startup_layout);
        if (bg != null){
            String bgImageUri = "android.resource://com.wb.nextgen/" + R.drawable.front_page_bg;
            PicassoTrustAll.loadImageIntoView(NextGenApplication.getContext(), bgImageUri, bg);
        }
        /*movieLogo = (ImageView) findViewById(R.id.next_gen_startup_movie_logo);
        if (movieLogo != null){
            movieLogo.setImageResource(R.drawable.man_of_sett_top_logo);
        }*/
        NextGenLogger.d("GrantTest", "hide buttons");
        playMovieButton = (ImageButton) findViewById(R.id.next_gen_startup_play_button);
        if (playMovieButton != null){
            Glide.with(this).load(NextGenApplication.getMovieMetaData().getStyle().getButtonImageURL(NextGenStyle.NextGenAppearanceType.InMovie)).into(playMovieButton);
            //playMovieButton.setImageResource(R.drawable.front_page_paly_button);
            playMovieButton.setOnClickListener(this);
        }
        extraButton = (ImageButton) findViewById(R.id.next_gen_startup_extra_button);
        if (extraButton != null){
            Glide.with(this).load(NextGenApplication.getMovieMetaData().getStyle().getButtonImageURL(NextGenStyle.NextGenAppearanceType.OutOfMovie)).into(extraButton);
            //extraButton.setImageResource(R.drawable.front_page_extra_button);
            extraButton.setOnClickListener(this);
        }
        videoLoopPoint = (int)(NextGenApplication.getMovieMetaData().getStyle().getBackgroundVideoLoopTime() * 1000);
        buttonAnimationStartTime = (int)(NextGenApplication.getMovieMetaData().getStyle().getBackgroundVideoFadeTime() * 1000);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (startupVideoView != null ){
            if (!isStartUp){
                startupVideoView.seekTo(videoLoopPoint);
                startupVideoView.start();
                return;
            }
            isStartUp = false;
            startupVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    /**************/
                    Size videoSize = new Size(mp.getVideoWidth(), mp.getVideoHeight());



                    NextGenStyle movieStyle = NextGenApplication.getMovieMetaData().getStyle();


                    final ButtonParams mainMoiveParams = computeButtonParams(movieStyle.getButtonCenterOffset(NextGenStyle.NextGenAppearanceType.InMovie),
                            movieStyle.getButtonSizeOffset(NextGenStyle.NextGenAppearanceType.InMovie),
                            videoSize);

                    final ButtonParams extraParams = computeButtonParams(movieStyle.getButtonCenterOffset(NextGenStyle.NextGenAppearanceType.OutOfMovie),
                            movieStyle.getButtonSizeOffset(NextGenStyle.NextGenAppearanceType.OutOfMovie),
                            videoSize);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ViewGroup.LayoutParams mainMoiveBtnLayoutParams = playMovieButton.getLayoutParams();
                            if (mainMoiveBtnLayoutParams instanceof  LinearLayout.LayoutParams) {
                                ((LinearLayout.LayoutParams) mainMoiveBtnLayoutParams).setMargins(mainMoiveParams.x, mainMoiveParams.y, 0, 0);
                            }else if (mainMoiveBtnLayoutParams instanceof  RelativeLayout.LayoutParams) {
                                ((RelativeLayout.LayoutParams) mainMoiveBtnLayoutParams).setMargins(mainMoiveParams.x, mainMoiveParams.y, 0, 0);
                            }

                            mainMoiveBtnLayoutParams.height = mainMoiveParams.height;
                            mainMoiveBtnLayoutParams.width = mainMoiveParams.width;

                            playMovieButton.setLayoutParams(mainMoiveBtnLayoutParams);

                            ViewGroup.LayoutParams extraBtnLayoutParams = extraButton.getLayoutParams();
                            if (mainMoiveBtnLayoutParams instanceof  LinearLayout.LayoutParams) {
                                ((LinearLayout.LayoutParams) extraBtnLayoutParams).setMargins(extraParams.x, extraParams.y, 0, 0);
                            }else if (mainMoiveBtnLayoutParams instanceof  RelativeLayout.LayoutParams) {
                                ((RelativeLayout.LayoutParams) extraBtnLayoutParams).setMargins(extraParams.x, extraParams.y, 0, 0);
                            }

                            extraBtnLayoutParams.height = extraParams.height;
                            extraBtnLayoutParams.width = extraParams.width;

                            extraButton.setLayoutParams(extraBtnLayoutParams);

                        }
                    });


                    /**************/


                    startupVideoView.start();
                    if (startUpTimer == null){
                        startUpTimer = new Timer();
                    }
                    if (startUpTimerTask == null){
                        startUpTimerTask = new TimerTask() {
                            @Override
                            public void run() {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (buttonsLayout != null){
                                                buttonsLayout.setVisibility(View.VISIBLE);
                                                buttonsLayout.setAlpha(0.0f);

                                                // Start the animation
                                                buttonsLayout.animate().setDuration(1000).alpha(1.0f);

                                            }

                                        }
                                    });


                            }
                        };
                        startUpTimer.schedule(startUpTimerTask, buttonAnimationStartTime);
                    }
                }
            });
            startupVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startupVideoView.seekTo(videoLoopPoint);
                    startupVideoView.start();
                    //startUpVideoViewFrame.setVisibility(View.GONE);
                }
            });
            startupVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startUpVideoViewFrame.setVisibility(View.GONE);
                    startupVideoView.stopPlayback();
                }
            });
            startupVideoView.requestFocus();
            startupVideoView.setVideoURI(Uri.parse(NextGenApplication.getMovieMetaData().getStyle().getBackgroundVideoURL()));
        }
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.next_gen_startup_play_button) {

            Intent intent = new Intent(this, NextGenPlayer.class);
            intent.setDataAndType(Uri.parse(NextGenApplication.getMovieMetaData().getMainMovieUrl()), "video/*");
            startActivity(intent);

        } else if (v.getId() == R.id.next_gen_startup_extra_button) {
            Intent extraIntent = new Intent(this, NextGenExtraActivity.class);
            startActivity(extraIntent);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (NextGenApplication.isDebugBuild())
            CrashManager.register(this);
    }

    class ButtonParams {
        int x, y, height, width;
    }

    private ButtonParams computeButtonParams(NextGenStyle.NGScreenOffSetRatio centerRatio, NextGenStyle.NGScreenOffSetRatio sizeRatio, Size videoSize){
        ButtonParams resultParams = new ButtonParams();

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics screenMetrics = new DisplayMetrics();
        display.getRealMetrics(screenMetrics);

        Size screenSize = new Size(screenMetrics.widthPixels, screenMetrics.heightPixels);


        double videoAspecRatio = (double)videoSize.getWidth() / (double)videoSize.getHeight();
        double screenAspecRatio = (double)screenSize.getWidth() / (double)screenSize.getHeight();

        int effectiveVideoWidth, effectiveVideoHeight;

        if (videoAspecRatio > screenAspecRatio){        // video is wider
            effectiveVideoWidth = screenSize.getWidth();
            effectiveVideoHeight = (int)((double)screenSize.getWidth() / (double)videoSize.getWidth() * (double)videoSize.getHeight());

        }else{              // screen is wider
            effectiveVideoHeight = screenSize.getHeight();
            effectiveVideoWidth = (int)((double)screenSize.getHeight() / (double)videoSize.getHeight() * (double)videoSize.getWidth());
        }


        resultParams.width = (int)(effectiveVideoWidth * sizeRatio.horizontalRatio);
        resultParams.height = (int)(effectiveVideoHeight * sizeRatio.verticalRatio);

        resultParams.x = (screenSize.getWidth() - effectiveVideoWidth) / 2  // the side pillow width
                + (int)(centerRatio.horizontalRatio * effectiveVideoWidth )  // ratio of the center
                - resultParams.width / 2;                // half the width

        resultParams.y = (screenSize.getHeight() - effectiveVideoHeight) / 2  // the side pillow width
                + (int)(centerRatio.verticalRatio * effectiveVideoHeight )  // ratio of the center
                - resultParams.height / 2;                // half the width

        return resultParams;
    }

}
