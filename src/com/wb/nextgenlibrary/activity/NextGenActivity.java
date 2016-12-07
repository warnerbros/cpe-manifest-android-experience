package com.wb.nextgenlibrary.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.wb.nextgenlibrary.util.Size;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gzcheng on 1/7/16.
 */
public class NextGenActivity extends NextGenHideStatusBarActivity implements View.OnClickListener {
    VideoView startupVideoView;
    ImageView startupImageView;

    MediaPlayer audioPlayer;

    ImageButton playMovieButton;
    ImageButton extraButton;
    ImageButton purchaseButton;

    Button playMovieTextButton;
    Button extraTextButton;

    View imageButtonsFrame;
    View textButtonsFrame;
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

    StyleData.ExperienceStyle mainStyle = NextGenExperience.getMovieMetaData() != null ? NextGenExperience.getMovieMetaData().getRootExperienceStyle() : null;
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.next_gen_startup_view);
        if (NextGenExperience.getMovieMetaData() == null)
            finish();

        videoParentFrame = (FixedAspectRatioFrameLayout)findViewById(R.id.video_parent_aspect_ratio_frame);
        buttonParentFrame = (FixedAspectRatioFrameLayout)findViewById(R.id.button_parent_aspect_ratio_frame);
        imageParentFrame = (FixedAspectRatioFrameLayout)findViewById(R.id.image_background_aspect_ratio_frame);
        textButtonsFrame = findViewById(R.id.startup_text_buttons_layout);

        startupVideoView = (VideoView)findViewById(R.id.startup_video_view);
        startupImageView = (ImageView) findViewById(R.id.startup_image_view);

        imageButtonsFrame = findViewById(R.id.startup_buttons_layout);

        setPlayExtraButtonsVisibility(ButtonsMode.ALL_INVISIBLE);

        playMovieButton = (ImageButton) findViewById(R.id.next_gen_startup_play_button);
        if (playMovieButton != null){
            playMovieButton.setOnClickListener(this);
        }

        playMovieTextButton = (Button)findViewById(R.id.next_gen_startup_play_text_button);
        if (playMovieTextButton != null){
            playMovieTextButton.setOnClickListener(this);
        }

        View.OnLongClickListener extraLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(NextGenActivity.this, TestItemsActivity.class);
                startActivity(intent);
                return true;
            }
        };
        extraButton = (ImageButton) findViewById(R.id.next_gen_startup_extra_button);
        if (extraButton != null){
            extraButton.setOnClickListener(this);
           // extraButton.setOnLongClickListener(extraLongClickListener);
        }
        extraTextButton = (Button)findViewById(R.id.next_gen_startup_extra_text_button);
        if (extraTextButton != null){
            extraTextButton.setOnClickListener(this);
            //extraTextButton.setOnLongClickListener(extraLongClickListener);
        }

        purchaseButton = (ImageButton) findViewById(R.id.next_gen_startup_purchase_button);
        if (purchaseButton != null){
            purchaseButton.setOnClickListener(this);
        }

        StyleData.NodeBackground nodeBackground = (mainStyle != null ) ? mainStyle.getBackground() : null;

        if (nodeBackground != null && !StringHelper.isEmpty(nodeBackground.getVideoUrl())) {
            videoLoopPoint = nodeBackground.getVideoLoopingPoint();
            if (videoLoopPoint > 500)
                buttonAnimationStartTime = nodeBackground.getVideoLoopingPoint() - 500;
        }

        MovieMetaData.PictureImageData bgImageData = (mainStyle != null ) ? mainStyle.getBackgroundImage() : null;
        if (bgImageData != null && !StringHelper.isEmpty(bgImageData.url)){
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

        View exitIcon = findViewById(R.id.nge_main_exit);
        if (exitIcon != null){
            exitIcon.setOnClickListener(this);
        }


        arrangeLayoutAccordingToScreenOrientation(getResources().getConfiguration().orientation);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (TabletUtils.isTablet())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        if (mainStyle != null) {
            if (StringHelper.isEmpty(mainStyle.getBackgroundVideoUrl())) {		// if there's no background video

                startupVideoView.setVisibility(View.GONE);
                setPlayExtraButtonsVisibility(ButtonsMode.IMAGE_BUTTON_VISIBLE);

            } else {
                if (!isStartUp) {
                    startupVideoView.seekTo(videoLoopPoint);
                    startupVideoView.start();
                    return;
                }
                isStartUp = false;
                startupVideoSize = mainStyle.getBackgroundVideoSize();
                if (startupVideoSize != null)
                    videoParentFrame.setAspectRatio(startupVideoSize.getWidth(), startupVideoSize.getHeight());

                startupVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (startupVideoSize == null) {
                                    startupVideoSize = new Size(mp.getVideoWidth(), mp.getVideoHeight());
                                    videoParentFrame.setAspectRatio(startupVideoSize.getWidth(), startupVideoSize.getHeight());
                                }
                                //adjustButtonSizesAndPosition();

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
                                            if (imageButtonsFrame != null) {
                                                setPlayExtraButtonsVisibility(ButtonsMode.IMAGE_BUTTON_VISIBLE);
                                                imageButtonsFrame.setVisibility(View.VISIBLE);
                                                imageButtonsFrame.setAlpha(0.0f);

                                                // Start the animation
                                                imageButtonsFrame.animate().setDuration(1000).alpha(1.0f);

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
                if (Build.FINGERPRINT.contains("generic")){
                    startupVideoView.setVideoURI(Uri.parse("https://ia800201.us.archive.org/12/items/BigBuckBunny_328/BigBuckBunny_512kb.mp4"));
                }else
                    startupVideoView.setVideoURI(Uri.parse(mainStyle.getBackgroundVideoUrl()));
                if (!StringHelper.isEmpty(mainStyle.getBackgroundAudioUrl())){
                    try {
                        audioPlayer = MediaPlayer.create(this, Uri.parse(mainStyle.getBackgroundAudioUrl()));
                        audioPlayer.setLooping(true);
                        audioPlayer.start();
                    }catch (Exception ex){
                        NextGenLogger.e(F.TAG, ex.getMessage());
                    }
                }


            }
        }else {
            setPlayExtraButtonsVisibility(ButtonsMode.TEXT_BUTTON_VISIBLE);
        }
    }

    public void onPause(){
        super.onPause();
        if (startupVideoView.isPlaying()){
            startupVideoView.pause();
        }
        if (audioPlayer != null){
            audioPlayer.pause();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!startupVideoView.isPlaying() && startupVideoView.getVisibility() == View.VISIBLE){
            startupVideoView.start();
        }
        if (audioPlayer !=  null){
            audioPlayer.start();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        arrangeLayoutAccordingToScreenOrientation(newConfig.orientation);

    }

    private void arrangeLayoutAccordingToScreenOrientation(final int orientation){
        imageParentFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                arrangeLayoutAccordingToScreenOrientation_priv(orientation);
                imageParentFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    private void arrangeLayoutAccordingToScreenOrientation_priv(int orientation){
        ViewGroup.LayoutParams layoutParams = videoParentFrame.getLayoutParams();
        FixedAspectRatioFrameLayout buttonsReferenceFrame;
        Size buttonsReferenceFrameSize;
        Size buttonsReferenceFrameSourceSize;
        StyleData.NodeStyleData nodeStyleData;
        boolean isPortrait = false;
        if (mainStyle == null)		// if there's no main style, no calculations of button positions will be needed
            return;
        if (orientation == Configuration.ORIENTATION_PORTRAIT && !TabletUtils.isTablet()) {	// handle portrait mode only when it's on phone
            isPortrait = true;
            buttonsReferenceFrameSize = new Size(imageParentFrame.getWidth(), imageParentFrame.getHeight());
            buttonsReferenceFrame = imageParentFrame;
            buttonsReferenceFrameSourceSize = bgImageSize;
            nodeStyleData = mainStyle.getNodeStyleData(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (!StringHelper.isEmpty(mainStyle.getBackgroundVideoUrl())) {
                videoParentFrame.setAspectRatioPriority(FixedAspectRatioFrameLayout.Priority.WIDTH_PRIORITY);
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoParentFrame.setLayoutParams(layoutParams);
            } else {

            }

        }else /*if (orientation == Configuration.ORIENTATION_LANDSCAPE)*/{		// for tablets and Landscape mode
            nodeStyleData = mainStyle.getNodeStyleData(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (!StringHelper.isEmpty(mainStyle.getBackgroundVideoUrl())) {
                Size videoSize = mainStyle.getBackgroundVideoSize();
                Size screenSize = NextGenExperience.getScreenSize(NextGenExperience.getApplicationContext());
                float videoAspectRatio = ((float) videoSize.getHeight()) /((float) videoSize.getWidth());
                float screenAspectRatio = ((float) screenSize.getHeight()) /((float) screenSize.getWidth());
                if (screenAspectRatio > 1)
                    screenAspectRatio = 1/ screenAspectRatio;
                if (screenAspectRatio >= videoAspectRatio) {
                    videoParentFrame.setAspectRatioPriority(FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY);
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    videoParentFrame.setAspectRatioPriority(FixedAspectRatioFrameLayout.Priority.WIDTH_PRIORITY);
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                videoParentFrame.setLayoutParams(layoutParams);
                buttonsReferenceFrameSize = new Size(videoParentFrame.getWidth(), videoParentFrame.getHeight());
                buttonsReferenceFrame = videoParentFrame;
                buttonsReferenceFrameSourceSize = startupVideoSize;
            } else {
                buttonsReferenceFrameSize = new Size(imageParentFrame.getWidth(), imageParentFrame.getHeight());
                buttonsReferenceFrame = imageParentFrame;
                buttonsReferenceFrameSourceSize = bgImageSize;
            }
        }

        if (nodeStyleData != null) {
            textButtonsFrame.setVisibility(View.GONE);
            StyleData.ThemeData buttonTheme = nodeStyleData.theme;
            BackgroundOverlayAreaType buttonLayoutArea = nodeStyleData.getBGOverlay();

            if (!StringHelper.isEmpty(mainStyle.getBackgroundVideoUrl()) && startupVideoSize == null)
                return;

            double shrinkRatio = ((double) buttonsReferenceFrameSize.getWidth()) / ((double) buttonsReferenceFrameSourceSize.getWidth());
            double buttonShrinkRation = shrinkRatio;
            buttonParentFrame.copyFrameParams(buttonsReferenceFrame);

            int width = (int) (((double) buttonLayoutArea.getWidthPixels().intValue()) * shrinkRatio);
            int screenWidth = NextGenExperience.getScreenWidth(this);
            if (width > screenWidth){           // if the specific area is wider than the screen, set the button area to be the screen width
                width = screenWidth - 20 ;      // 20 being the padding on the size, 10 pixels each side
                buttonShrinkRation = ((double) width / ((double) buttonLayoutArea.getWidthPixels().intValue()));
            }

            int height = (int) (((double) buttonLayoutArea.getHeightPixels().intValue()) * buttonShrinkRation);
            int y = (int) (((double) buttonsReferenceFrameSourceSize.getHeight() - buttonLayoutArea.getPixelsFromBottom().intValue()) * shrinkRatio) - height;
            int x = (int) (((double) buttonLayoutArea.getPixelsFromLeft().intValue()) * shrinkRatio);

            if (isPortrait){
                x = (buttonsReferenceFrameSize.getWidth() - width) / 2;
            }

            ViewGroup.LayoutParams buttonsLayoutParams = imageButtonsFrame.getLayoutParams();
            if (buttonsLayoutParams instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) buttonsLayoutParams).setMargins(x, y, 0, 0);
            } else if (buttonsLayoutParams instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) buttonsLayoutParams).setMargins(x, y, 0, 0);
            } else if (buttonsLayoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) buttonsLayoutParams).setMargins(x, y, 0, 0);
            }

            buttonsLayoutParams.height = height;
            buttonsLayoutParams.width = width;

            imageButtonsFrame.setLayoutParams(buttonsLayoutParams);

            MovieMetaData.PictureImageData extraBtnImageData = buttonTheme.getImageData(StyleData.ThemeData.EXTRA_BUTTON);
            MovieMetaData.PictureImageData playBtnImageData = buttonTheme.getImageData(StyleData.ThemeData.PLAY_BUTTON);
            MovieMetaData.PictureImageData purchaseBtnImageData = buttonTheme.getImageData(StyleData.ThemeData.PURCHASE_BUTTON);

            double buttonRatio = (double) width / (double) playBtnImageData.width;
            ViewGroup.LayoutParams playBtnParams = null;
            if (playMovieButton != null) {
                playBtnParams = playMovieButton.getLayoutParams();

                int newHeight = (int) ((double) playBtnImageData.height * buttonRatio);
                playBtnParams.height = newHeight;
                playMovieButton.setLayoutParams(playBtnParams);

                if (playBtnImageData != null)
                    Glide.with(this).load(playBtnImageData.url).into(playMovieButton);
            }

            if (purchaseButton != null) {
                if (purchaseBtnImageData != null && playBtnParams != null) {
                    //purchaseButton.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams buttonsParams = purchaseButton.getLayoutParams();

                    buttonsParams.height = playBtnParams.height;
                    purchaseButton.setLayoutParams(buttonsParams);

                    Glide.with(this).load(purchaseBtnImageData.url).into(purchaseButton);
                }else
                    purchaseButton.setVisibility(View.GONE);
            }

            if (extraButton != null) {
                ViewGroup.LayoutParams buttonsParams = extraButton.getLayoutParams();

                int newWidth = (int) ((double) extraBtnImageData.width * buttonRatio);
                int newHeight = (int) ((double) extraBtnImageData.height * buttonRatio);
                buttonsParams.height = newHeight;
                buttonsParams.width = newWidth;


                int extra_x, extra_y;
                extra_x = (buttonsLayoutParams.width - newWidth )/ 2;

                //calculate extra Btn position
                if (purchaseBtnImageData != null){
                    extra_y = (buttonsLayoutParams.height - newHeight) / 2;
                }else{
                    extra_y = buttonsLayoutParams.height - newHeight;
                }

                if (buttonsParams instanceof LinearLayout.LayoutParams) {
                    ((LinearLayout.LayoutParams) buttonsParams).setMargins(extra_x, extra_y, 0, 0);
                } else if (buttonsParams instanceof RelativeLayout.LayoutParams) {
                    ((RelativeLayout.LayoutParams) buttonsParams).setMargins(extra_x, extra_y, 0, 0);
                } else if (buttonsParams instanceof FrameLayout.LayoutParams) {
                    ((FrameLayout.LayoutParams) buttonsParams).setMargins(extra_x, extra_y, 0, 0);
                }


                extraButton.setLayoutParams(buttonsParams);


                if (extraBtnImageData != null)
                    Glide.with(this).load(extraBtnImageData.url).into(extraButton);

            }

            imageButtonsFrame.invalidate();
            if (StringHelper.isEmpty(mainStyle.getBackgroundVideoUrl()))
                imageButtonsFrame.setVisibility(View.VISIBLE);
            return;
        }
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
        } else if (v.getId() == R.id.next_gen_startup_purchase_button) {
            if (NextGenExperience.getNextGenEventHandler() != null)
                NextGenExperience.getNextGenEventHandler().handlePurchaseButtonPressed(this, NextGenExperience.getNextgenPlaybackObject());
        } else if (v.getId() == R.id.nge_main_exit){
            finish();
        }
    }

    static enum ButtonsMode{
        TEXT_BUTTON_VISIBLE, IMAGE_BUTTON_VISIBLE, ALL_INVISIBLE;
    }

    private void setPlayExtraButtonsVisibility(ButtonsMode mode){
        switch (mode){
            case TEXT_BUTTON_VISIBLE:
                textButtonsFrame.setVisibility(View.VISIBLE);
                imageButtonsFrame.setVisibility(View.GONE);
                break;
            case IMAGE_BUTTON_VISIBLE:
                imageButtonsFrame.setVisibility(View.VISIBLE);
                textButtonsFrame.setVisibility(View.GONE);
                break;
            case ALL_INVISIBLE:
            default:
                imageButtonsFrame.setVisibility(View.GONE);
                textButtonsFrame.setVisibility(View.GONE);
        }
    }
}


