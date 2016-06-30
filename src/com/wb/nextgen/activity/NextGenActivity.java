package com.wb.nextgen.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.NextGenLogger;

import net.hockeyapp.android.CrashManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gzcheng on 1/7/16.
 */
public class NextGenActivity extends FragmentActivity implements View.OnClickListener {
    // wrapper of ProfileViewFragment

    VideoView startupVideoView;

    ImageButton playMovieButton;
    ImageButton extraButton;
    LinearLayout buttonsLayout;

    final static int LOOPING_POINT = 14 *1000;
    final static int BUTTON_ANIM_START = 8500;
    final static int TOP_RATIO = 52;
    final static int LEFT_RATIO = 31;
    LinearLayout leftPadding, topPadding, rightPortion;
    //RelativeLayout startUpVideoViewFrame;

    private TimerTask startUpTimerTask;
    private Timer startUpTimer;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.next_gen_startup_view);

        startupVideoView = (VideoView)findViewById(R.id.startup_video_view);
        //startUpVideoViewFrame = (RelativeLayout)findViewById(R.id.startuo_video_view_frame);


        leftPadding = (LinearLayout)findViewById(R.id.startup_left_padding);
        topPadding = (LinearLayout)findViewById(R.id.startup_top_padding);
        rightPortion = (LinearLayout)findViewById(R.id.startup_right_portion);

        buttonsLayout = (LinearLayout) findViewById(R.id.startup_buttons_layout);
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
            playMovieButton.setImageResource(R.drawable.front_page_paly_button);
            playMovieButton.setOnClickListener(this);
        }
        extraButton = (ImageButton) findViewById(R.id.next_gen_startup_extra_button);
        if (extraButton != null){
            extraButton.setImageResource(R.drawable.front_page_extra_button);
            extraButton.setOnClickListener(this);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (startupVideoView != null ){
            startupVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    /**************/
                    int width = mp.getVideoWidth();
                    int height = mp.getVideoHeight();
                    int screenHeight = NextGenApplication.getScreenHeight(NextGenActivity.this);
                    int screenWidth = NextGenApplication.getScreenWidth(NextGenActivity.this);
                    int videoEffectiveWidth = screenHeight * width / height;

                    int pillowWidth = screenWidth - videoEffectiveWidth;
                    int totalLeft = pillowWidth/2 + videoEffectiveWidth * LEFT_RATIO / 100 - buttonsLayout.getLayoutParams().width/2;
                    final int realLeftRatio = totalLeft * 100 / screenWidth;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ViewGroup.LayoutParams params = leftPadding.getLayoutParams();
                            ((LinearLayout.LayoutParams)params).weight = realLeftRatio;
                            leftPadding.setLayoutParams(params);

                            params = rightPortion.getLayoutParams();
                            ((LinearLayout.LayoutParams)params).weight = 100 - realLeftRatio;
                            rightPortion.setLayoutParams(params);

                            params = topPadding.getLayoutParams();
                            ((LinearLayout.LayoutParams)params).weight = TOP_RATIO;
                            topPadding.setLayoutParams(params);


                            //rightPortion.setLayoutParams(new TableLayout.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT, 100 - realLeftRatio));
                            //topPadding.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, TOP_RATIO));
                            //buttonsLayout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 100 - TOP_RATIO));
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
                                            NextGenLogger.d("GrantTest", "show buttons");
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
                        startUpTimer.schedule(startUpTimerTask, BUTTON_ANIM_START);
                    }
                }
            });
            startupVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startupVideoView.seekTo(LOOPING_POINT);
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
            startupVideoView.setVideoURI(Uri.parse("android.resource://com.wb.nextgen/" + R.raw.mos_nextgen_background));
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
}
