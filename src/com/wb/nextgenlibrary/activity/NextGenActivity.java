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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NextGenAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.StyleData;
import com.wb.nextgenlibrary.parser.cpestyle.BackgroundOverlayAreaType;
import com.wb.nextgenlibrary.testassets.TestItemsActivity;
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
    Button playMovieTextButton;
    Button extraTextButton;

    View buttonsLayout;
    Size startupVideoSize = null;
    Size bgImageSize = null;

    FixedAspectRatioFrameLayout videoParentFrame;
    FixedAspectRatioFrameLayout imageParentFrame;
    FixedAspectRatioFrameLayout buttonParentFrame;
    View textButtonParentFrame;

    private int videoLoopPoint = 0;
    private int buttonAnimationStartTime = 0;

    private TimerTask startUpTimerTask;
    private Timer startUpTimer;

    private boolean isStartUp = true;

    StyleData.ExperienceStyle mainStyle = NextGenExperience.getMovieMetaData().getRootExperienceStyle();
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.next_gen_startup_view);

        videoParentFrame = (FixedAspectRatioFrameLayout)findViewById(R.id.video_parent_aspect_ratio_frame);
        buttonParentFrame = (FixedAspectRatioFrameLayout)findViewById(R.id.button_parent_aspect_ratio_frame);
        imageParentFrame = (FixedAspectRatioFrameLayout)findViewById(R.id.image_background_aspect_ratio_frame);
        textButtonParentFrame = findViewById(R.id.startup_text_buttons_layout);

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
            extraButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(NextGenActivity.this, TestItemsActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        }

        playMovieTextButton = (Button)findViewById(R.id.next_gen_startup_play_text_button);
        if (playMovieTextButton != null){
            playMovieTextButton.setOnClickListener(this);
        }
        extraTextButton = (Button)findViewById(R.id.next_gen_startup_extra_text_button);
        if (extraTextButton != null){
            extraTextButton.setOnClickListener(this);
            extraTextButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(NextGenActivity.this, TestItemsActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        }

        StyleData.NodeBackground nodeBackground = (mainStyle != null ) ? mainStyle.getBackground() : null;

        if (nodeBackground != null && !StringHelper.isEmpty(nodeBackground.getVideoUrl())) {
            videoLoopPoint = nodeBackground.getVideoLoopingPoint();
            if (videoLoopPoint > 500)
                buttonAnimationStartTime = nodeBackground.getVideoLoopingPoint() - 500;
        }
        MovieMetaData.PictureImageData bgImageData = (mainStyle != null ) ? mainStyle.getBackgroundImage() : null;
        if (bgImageData != null && !StringHelper.isEmpty(bgImageData.url)){
            //buttonsLayout.setVisibility(View.VISIBLE);
            String bgImageUrl = bgImageData.url;
            imageParentFrame.setAspectRatio(bgImageData.width, bgImageData.height);
            bgImageSize = new Size(bgImageData.width, bgImageData.height);
            if (startupImageView != null && !StringHelper.isEmpty(bgImageUrl)){
                startupImageView.setVisibility(View.VISIBLE);
                Glide.with(this).load(bgImageUrl).asBitmap().fitCenter().into(startupImageView);
            }
        }else{      // load default image
            String imageurl = NextGenExperience.getMovieMetaData().getExtraExperience().getPosterImgUrl();
            videoParentFrame.setVisibility(View.GONE);
            Glide.with(this).load(imageurl).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(final GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageParentFrame.setAspectRatio(resource.getIntrinsicWidth(), resource.getIntrinsicHeight());
                        }
                    });

                    return false;
                }
            }).fitCenter().into(startupImageView);

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
        if (mainStyle != null) {
            if (StringHelper.isEmpty(mainStyle.getBackgroundVideoUrl())) {
                if (startupVideoView != null) {
                    startupVideoView.setVisibility(View.GONE);
                }
            } else if (startupVideoView != null) {
                if (!isStartUp) {
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
								if (startupVideoSize == null)
	                                startupVideoSize = new Size(mp.getVideoWidth(), mp.getVideoHeight());
                                videoParentFrame.setAspectRatio(startupVideoSize.getWidth(), startupVideoSize.getHeight());
                                adjustButtonSizesAndPosition();

                            }
                        });
                        startupVideoView.start();
                        if (startUpTimer == null) {
                            startUpTimer = new Timer();
                        }
                        if (startUpTimerTask == null) {
                            startUpTimerTask = new TimerTask() {
                                @Override
                                public void run() {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (buttonsLayout != null) {
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

                startupVideoView.requestFocus();
                startupVideoView.setVideoURI(Uri.parse(mainStyle.getBackgroundVideoUrl()));
				startupVideoSize = mainStyle.getBackgroundVideoSize();

            }
        }else {
            buttonParentFrame.setVisibility(View.VISIBLE);
            buttonsLayout.setVisibility(View.GONE);
            textButtonParentFrame.setVisibility(View.VISIBLE);
        }
    }

    public void onStop(){
        super.onStop();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    public void onPause(){
        super.onPause();
        if (startupVideoView.isPlaying()){
            startupVideoView.pause();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustButtonSizesAndPosition();

    }

    private void adjustButtonSizesAndPosition(){
        if (mainStyle != null) {
            int orientation = NextGenHideStatusBarActivity.getCurrentScreenOrientation();       // adjust video frame aspect ration priority according to orientation
            FixedAspectRatioFrameLayout.Priority newPriority = FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY;
            switch (orientation) {
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
            if (newPriority == videoParentFrame.getAspectRatioPriority()) {                      // just adjust the botton locations if there's no layout change
                adjustButtonSizesAndPosition_priv();

            } else {
                videoParentFrame.setAspectRatioPriority(newPriority);
                startupVideoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    int counter = 0;

                    @Override
                    public void onGlobalLayout() {
                        adjustButtonSizesAndPosition_priv();
                        counter = counter + 1;
                        if (counter > 3) {
                            startupVideoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }else{              // for experience without CPE style xml
            buttonParentFrame.copyFrameParams(imageParentFrame);
            adjustButtonSizesAndPosition_priv();
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

        if (mainStyle != null) {
            StyleData.NodeStyleData nodeStyleData = mainStyle.getNodeStyleData(orientation);
            if (nodeStyleData != null) {
                textButtonParentFrame.setVisibility(View.GONE);
                StyleData.ThemeData buttonTheme = nodeStyleData.theme;
                BackgroundOverlayAreaType buttonLayoutArea = nodeStyleData.getBGOverlay();

                if (startupVideoSize == null)
                    return;

                double shrinkRatio = ((double) referenceFrameSize.getWidth()) / ((double) targetSize.getWidth());
                double buttonShrinkRation = shrinkRatio;

                int width = (int) (((double) buttonLayoutArea.getWidthPixels().intValue()) * shrinkRatio);
                int screenWidth = NextGenExperience.getScreenWidth(this);
                if (width > screenWidth){
                    width = screenWidth;
                    buttonShrinkRation = ((double) width / ((double) buttonLayoutArea.getWidthPixels().intValue()));
                }

                int height = (int) (((double) buttonLayoutArea.getHeightPixels().intValue()) * buttonShrinkRation);
                int y = (int) (((double) targetSize.getHeight() - buttonLayoutArea.getPixelsFromBottom().intValue()) * shrinkRatio) - height;
                int x = (int) (((double) buttonLayoutArea.getPixelsFromLeft().intValue()) * shrinkRatio);

                ViewGroup.LayoutParams buttonsLayoutParams = buttonsLayout.getLayoutParams();
                if (buttonsLayoutParams instanceof LinearLayout.LayoutParams) {
                    ((LinearLayout.LayoutParams) buttonsLayoutParams).setMargins(x, y, 0, 0);
                } else if (buttonsLayoutParams instanceof RelativeLayout.LayoutParams) {
                    ((RelativeLayout.LayoutParams) buttonsLayoutParams).setMargins(x, y, 0, 0);
                } else if (buttonsLayoutParams instanceof FrameLayout.LayoutParams) {
                    ((FrameLayout.LayoutParams) buttonsLayoutParams).setMargins(x, y, 0, 0);
                }

                buttonsLayoutParams.height = height;
                buttonsLayoutParams.width = width;

                buttonsLayout.setLayoutParams(buttonsLayoutParams);

                MovieMetaData.PictureImageData extraBtnImageData = buttonTheme.getImageData(StyleData.ThemeData.EXTRA_BUTTON);
                MovieMetaData.PictureImageData playBtnImageData = buttonTheme.getImageData(StyleData.ThemeData.PLAY_BUTTON);

                double buttonRatio = (double) width / (double) playBtnImageData.width;
                if (playMovieButton != null) {
                    ViewGroup.LayoutParams buttonsParams = playMovieButton.getLayoutParams();

                    int newHeight = (int) ((double) playBtnImageData.height * buttonRatio);
                    buttonsParams.height = newHeight;
                    playMovieButton.setLayoutParams(buttonsParams);

                    if (playBtnImageData != null)
                        Glide.with(this).load(playBtnImageData.url).into(playMovieButton);
                }

                if (extraButton != null) {
                    ViewGroup.LayoutParams buttonsParams = extraButton.getLayoutParams();

                    int newWidth = (int) ((double) extraBtnImageData.width * buttonRatio);
                    int newHeight = (int) ((double) extraBtnImageData.height * buttonRatio);
                    buttonsParams.height = newHeight;
                    buttonsParams.width = newWidth;
                    extraButton.setLayoutParams(buttonsParams);


                    if (extraBtnImageData != null)
                        Glide.with(this).load(extraBtnImageData.url).into(extraButton);
                }
                buttonsLayout.invalidate();
                return;
            }
        }

        buttonParentFrame.setVisibility(View.VISIBLE);
        buttonsLayout.setVisibility(View.GONE);
        textButtonParentFrame.setVisibility(View.VISIBLE);


    }

    @Override
    public void onClick(View v){

        if (v.getId() == R.id.next_gen_startup_play_button || v.getId() == R.id.next_gen_startup_play_text_button) {

            Intent intent = new Intent(this, NextGenPlayer.class);
            intent.setDataAndType(Uri.parse(NextGenExperience.getMovieMetaData().getMainMovieUrl()), "video/*");
            startActivity(intent);
            NextGenAnalyticData.reportEvent(this, null, "Play Movie", NextGenAnalyticData.AnalyticAction.ACTION_CLICK, null);

        } else if (v.getId() == R.id.next_gen_startup_extra_button || v.getId() == R.id.next_gen_startup_extra_text_button) {
            Intent extraIntent = new Intent(this, NextGenExtraActivity.class);
            startActivity(extraIntent);
            NextGenAnalyticData.reportEvent(this, null, "Extras", NextGenAnalyticData.AnalyticAction.ACTION_CLICK, null);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!startupVideoView.isPlaying() && startupVideoView.getVisibility() == View.VISIBLE){
            startupVideoView.start();
        }
    }

}

