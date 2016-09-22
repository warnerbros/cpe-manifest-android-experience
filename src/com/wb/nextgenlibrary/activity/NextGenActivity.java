package com.wb.nextgenlibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.StyleData;
import com.wb.nextgenlibrary.parser.cpestyle.BackgroundOverlayAreaType;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gzcheng on 1/7/16.
 */
public class NextGenActivity extends NextGenHideStatusBarActivity implements View.OnClickListener {
    // wrapper of ProfileViewFragment

    VideoView startupVideoView;
    ImageView startupImageView;

    ImageButton playMovieButton;
    ImageButton extraButton;
    View buttonsLayout;
    Size startupVideoSize = null;
    Size bgImageSize = null;

    FixedAspectRatioFrameLayout videoParentFrame;
    FixedAspectRatioFrameLayout imageParentFrame;
    FixedAspectRatioFrameLayout buttonParentFrame;

    private int videoLoopPoint = 0;
    private int buttonAnimationStartTime = 0;

    private TimerTask startUpTimerTask;
    private Timer startUpTimer;

    private boolean isStartUp = true;

    StyleData.ExperienceStyle mainStyle = NextGenExperience.getMovieMetaData().getRootExperienceStyle();
    StyleData.ExperienceStyle imeStyle = NextGenExperience.getMovieMetaData().getIMEExperienceStyle();
    StyleData.ExperienceStyle extraStyle = NextGenExperience.getMovieMetaData().getExtraExperienceStyle();
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.next_gen_startup_view);

        videoParentFrame = (FixedAspectRatioFrameLayout)findViewById(R.id.video_parent_aspect_ratio_frame);
        buttonParentFrame = (FixedAspectRatioFrameLayout)findViewById(R.id.button_parent_aspect_ratio_frame);
        imageParentFrame = (FixedAspectRatioFrameLayout)findViewById(R.id.image_background_aspect_ratio_frame);

        startupVideoView = (VideoView)findViewById(R.id.startup_video_view);
        startupImageView = (ImageView) findViewById(R.id.startup_image_view);

        buttonsLayout = findViewById(R.id.startup_buttons_layout);
        if (buttonsLayout != null){
            buttonsLayout.setVisibility(View.GONE);
        }

        playMovieButton = (ImageButton) findViewById(R.id.next_gen_startup_play_button);
        if (playMovieButton != null){
            playMovieButton.setOnClickListener(this);
        }
        extraButton = (ImageButton) findViewById(R.id.next_gen_startup_extra_button);
        if (extraButton != null){
            extraButton.setOnClickListener(this);
        }

        StyleData.NodeBackground nodeBackground = mainStyle.getBackground();

        if (!StringHelper.isEmpty(nodeBackground.getVideoUrl())) {
            videoLoopPoint = nodeBackground.getVideoLoopingPoint();
            buttonAnimationStartTime = nodeBackground.getVideoLoopingPoint() - 500;
        }
        MovieMetaData.PictureImageData bgImageData = mainStyle.getBackgroundImage();
        if (bgImageData != null && !StringHelper.isEmpty(bgImageData.url)){
            //buttonsLayout.setVisibility(View.VISIBLE);
            String bgImageUrl = bgImageData.url;
            imageParentFrame.setAspectRatio(bgImageData.width, bgImageData.height);
            bgImageSize = new Size(bgImageData.width, bgImageData.height);
            if (startupImageView != null && !StringHelper.isEmpty(bgImageUrl)){
                startupImageView.setVisibility(View.VISIBLE);
                Glide.with(this).load(bgImageUrl).asBitmap().fitCenter().into(startupImageView);
            }
        }
        adjustButtonSizesAndPosition();
    }

    @Override
    public void onStart(){
        super.onStart();
        if (TabletUtils.isTablet())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        if (StringHelper.isEmpty(mainStyle.getBackgroundVideoUrl())) {
            if (startupVideoView != null ){
                startupVideoView.setVisibility(View.GONE);
            }
        } else if (startupVideoView != null ){
            if (!isStartUp){
                startupVideoView.seekTo(videoLoopPoint);
                startupVideoView.start();
                return;
            }
            isStartUp = false;
            startupVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startupVideoSize = new Size(mp.getVideoWidth(), mp.getVideoHeight());
                            videoParentFrame.setAspectRatio(startupVideoSize.getWidth(), startupVideoSize.getHeight());
                            adjustButtonSizesAndPosition();

                        }
                    });
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

                    //added: tr 9/19
                    mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            startupVideoView.start();
                        }
                    });
                }
            });
            startupVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startupVideoView.seekTo(videoLoopPoint);
                    //need to wait until the seekTo is complete before restarting.
                    //startupVideoView.start();
                }


            });
            startupVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startupVideoView.stopPlayback();
                }
            });

            startupVideoView.requestFocus();
            startupVideoView.setVideoURI(Uri.parse(mainStyle.getBackgroundVideoUrl()) );


        }
    }

    public void onStop(){
        super.onStop();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustButtonSizesAndPosition();

    }

    private void adjustButtonSizesAndPosition(){
        int orientation = NextGenHideStatusBarActivity.getCurrentScreenOrientation();       // adjust video frame aspect ration priority according to orientation
        FixedAspectRatioFrameLayout.Priority newPriority = FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY;
        switch(orientation){
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                newPriority = FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY;
                buttonParentFrame.copyFrameParams(videoParentFrame);                        // copy the video frame's layout params to Button frame
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                newPriority = FixedAspectRatioFrameLayout.Priority.WIDTH_PRIORITY;
                buttonParentFrame.copyFrameParams(imageParentFrame);                        // copy the background image frame's layout params to Button frame
                break;

        }
        if (newPriority == videoParentFrame.getAspectRatioPriority()){                      // just adjust the botton locations if there's no layout change
            adjustButtonSizesAndPosition_priv();

        }else {
            videoParentFrame.setAspectRatioPriority(newPriority);
            startupVideoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                int counter = 0;
                @Override
                public void onGlobalLayout() {
                    adjustButtonSizesAndPosition_priv();
                    counter = counter +1;
                    if (counter > 1) {
                        startupVideoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    private void adjustButtonSizesAndPosition_priv(){

        int orientation = NextGenHideStatusBarActivity.getCurrentScreenOrientation();
        Size referenceFrameSize = new Size(buttonParentFrame.getWidth(), buttonParentFrame.getHeight());

        Size targetSize = startupVideoSize;
        switch(orientation){
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                targetSize = startupVideoSize;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                targetSize = bgImageSize;
                break;

        }
        StyleData.NodeStyleData nodeStyleData = mainStyle.getNodeStyleData(orientation);
        StyleData.ThemeData buttonTheme = nodeStyleData.theme;
        BackgroundOverlayAreaType buttonLayoutArea = nodeStyleData.getBGOverlay();

        if (startupVideoSize == null)
            return;


        double shrinkRatio = ((double)referenceFrameSize.getWidth()) / ((double)targetSize.getWidth());


        int width = (int)(((double)buttonLayoutArea.getWidthPixels().intValue()) * shrinkRatio);
        int height = (int)(((double)buttonLayoutArea.getHeightPixels().intValue()) * shrinkRatio);
        int y = (int)(((double)targetSize.getHeight() - buttonLayoutArea.getPixelsFromBottom().intValue()) * shrinkRatio) - height;
        int x = (int)(((double)buttonLayoutArea.getPixelsFromLeft().intValue()) * shrinkRatio);

        ViewGroup.LayoutParams buttonsLayoutParams = buttonsLayout.getLayoutParams();
        if (buttonsLayoutParams instanceof  LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) buttonsLayoutParams).setMargins(x, y, 0, 0);
        }else if (buttonsLayoutParams instanceof  RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) buttonsLayoutParams).setMargins(x, y, 0, 0);
        }else if (buttonsLayoutParams instanceof  FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) buttonsLayoutParams).setMargins(x, y, 0, 0);
        }

        buttonsLayoutParams.height = height;
        buttonsLayoutParams.width = width;

        buttonsLayout.setLayoutParams(buttonsLayoutParams);


        MovieMetaData.PictureImageData extraBtnImageData = buttonTheme.getImageData(StyleData.ThemeData.EXTRA_BUTTON);
        MovieMetaData.PictureImageData playBtnImageData = buttonTheme.getImageData(StyleData.ThemeData.PLAY_BUTTON);

        double buttonRatio = (double)width / (double)playBtnImageData.width;
        if (playMovieButton != null) {
            ViewGroup.LayoutParams buttonsParams = playMovieButton.getLayoutParams();

            int newHeight = (int)((double)playBtnImageData.height * buttonRatio);
            buttonsParams.height = newHeight;
            playMovieButton.setLayoutParams(buttonsParams);

            if (playBtnImageData != null)
                Glide.with(this).load(playBtnImageData.url).into(playMovieButton);
        }

        if (extraButton != null) {
            ViewGroup.LayoutParams buttonsParams = extraButton.getLayoutParams();

            int newWidth = (int)((double)extraBtnImageData.width * buttonRatio);
            int newHeight = (int)((double)extraBtnImageData.height * buttonRatio);
            buttonsParams.height = newHeight;
            buttonsParams.width = newWidth;
            extraButton.setLayoutParams(buttonsParams);


            if (extraBtnImageData != null)
                Glide.with(this).load(extraBtnImageData.url).into(extraButton);
        }
        buttonsLayout.invalidate();

    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.next_gen_startup_play_button) {

            Intent intent = new Intent(this, NextGenPlayer.class);
            intent.setDataAndType(Uri.parse(NextGenExperience.getMovieMetaData().getMainMovieUrl()), "video/*");
            startActivity(intent);

        } else if (v.getId() == R.id.next_gen_startup_extra_button) {
            Intent extraIntent = new Intent(this, NextGenExtraActivity.class);
            startActivity(extraIntent);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

}

