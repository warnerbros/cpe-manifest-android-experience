package com.wb.nextgenlibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.NextGenStyle;
import com.wb.nextgenlibrary.util.utils.StringHelper;

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

    private int videoLoopPoint = 0;
    private int buttonAnimationStartTime = 0;

    private TimerTask startUpTimerTask;
    private Timer startUpTimer;

    private boolean isStartUp = true;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.next_gen_startup_view);

        startupVideoView = (VideoView)findViewById(R.id.startup_video_view);
        startupImageView = (ImageView) findViewById(R.id.startup_image_view);

        buttonsLayout = findViewById(R.id.startup_buttons_layout);
        if (buttonsLayout != null){
            buttonsLayout.setVisibility(View.GONE);
        }

        playMovieButton = (ImageButton) findViewById(R.id.next_gen_startup_play_button);
        if (playMovieButton != null){
            Glide.with(this).load(NextGenExperience.getMovieMetaData().getStyle().getButtonImageURL(NextGenStyle.NextGenAppearanceType.InMovie)).into(playMovieButton);
            playMovieButton.setOnClickListener(this);
        }
        extraButton = (ImageButton) findViewById(R.id.next_gen_startup_extra_button);
        if (extraButton != null){
            Glide.with(this).load(NextGenExperience.getMovieMetaData().getStyle().getButtonImageURL(NextGenStyle.NextGenAppearanceType.OutOfMovie)).into(extraButton);
            extraButton.setOnClickListener(this);
        }
        if (!StringHelper.isEmpty(NextGenExperience.getMovieMetaData().getStyle().getBackgroundVideoURL())) {
            if (startupImageView != null)
                startupImageView.setVisibility(View.GONE);
            videoLoopPoint = (int) (NextGenExperience.getMovieMetaData().getStyle().getBackgroundVideoLoopTime() * 1000);
            buttonAnimationStartTime = (int) (NextGenExperience.getMovieMetaData().getStyle().getBackgroundVideoFadeTime() * 1000);
        } else{
            buttonsLayout.setVisibility(View.VISIBLE);
            startupVideoView.setVisibility(View.GONE);
            String bgImageUrl = NextGenExperience.getMovieMetaData().getStyle().getStartupImageURL();
            if (startupImageView != null && !StringHelper.isEmpty(bgImageUrl)){
                startupImageView.setVisibility(View.VISIBLE);
                Glide.with(this).load(bgImageUrl).fitCenter().into(startupImageView);
            }
        }
        adjustButtonSizesAndPosition();
    }



    @Override
    public void onStart(){
        super.onStart();
        if (StringHelper.isEmpty(NextGenExperience.getMovieMetaData().getStyle().getBackgroundVideoURL())) {
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
                }
            });
            startupVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startupVideoView.stopPlayback();
                }
            });
            startupVideoView.requestFocus();
            startupVideoView.setVideoURI(Uri.parse(NextGenExperience.getMovieMetaData().getStyle().getBackgroundVideoURL()));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustButtonSizesAndPosition();
    }

    private void adjustButtonSizesAndPosition(){

        int orientation = NextGenHideStatusBarActivity.getCurrentScreenOrientation();


        Size screenSize = NextGenExperience.getScreenSize(this);
        Size buttonFrameSize;
        double aspectRatio =  (double)screenSize.getHeight() / (double)screenSize.getWidth();
        if (aspectRatio < 0.75){    // use the height
            buttonFrameSize = new Size ( (int)((double)screenSize.getHeight() * 4.0 / 3.0), screenSize.getHeight());
        }else if (aspectRatio > 0.75){  // user the width
            buttonFrameSize = new Size ( screenSize.getWidth(), (int)((double)screenSize.getWidth() * 3.0 / 4.0));
        }else {
            buttonFrameSize = screenSize;
        }


        NextGenStyle movieStyle = NextGenExperience.getMovieMetaData().getStyle();
        final ButtonParams mainMoiveParams = computeButtonParams(movieStyle.getButtonCenterOffset(NextGenStyle.NextGenAppearanceType.InMovie, orientation),
                movieStyle.getButtonSizeOffset(NextGenStyle.NextGenAppearanceType.InMovie, orientation),
                buttonFrameSize);

        final ButtonParams extraParams = computeButtonParams(movieStyle.getButtonCenterOffset(NextGenStyle.NextGenAppearanceType.OutOfMovie, orientation),
                movieStyle.getButtonSizeOffset(NextGenStyle.NextGenAppearanceType.OutOfMovie, orientation),
                buttonFrameSize);
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
