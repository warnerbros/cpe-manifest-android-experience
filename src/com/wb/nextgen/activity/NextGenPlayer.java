package com.wb.nextgen.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.wb.nextgen.NextGenExperience;
import com.wb.nextgen.R;
import com.wb.nextgen.data.NextGenStyle;
import com.wb.nextgen.fragment.AbstractNextGenMainMovieFragment;
import com.wb.nextgen.fragment.NextGenPlayerBottomFragment;
import com.wb.nextgen.interfaces.NextGenFragmentTransactionInterface;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;
import com.wb.nextgen.videoview.IVideoViewActionListener;
import com.wb.nextgen.videoview.ObservableVideoView;
import com.wb.nextgen.widget.CustomMediaController;
import com.wb.nextgen.widget.MainFeatureMediaController;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by gzcheng on 1/5/16.
 */
public class NextGenPlayer extends AbstractNextGenActivity implements NextGenFragmentTransactionInterface, DialogInterface.OnCancelListener {

    private final static String IS_PLAYER_PLAYING = "IS PLAYING";
    private final static String RESUME_PLAYBACK_TIME = "RESUME_PLAYBACK_TIME";

    protected ObservableVideoView interstitialVideoView;
    protected RelativeLayout containerView;
    protected View skipThisView;
    protected ProgressBar skipThisCounter;

    private TimerTask imeUpdateTask;

    private Timer imeUpdateTimer;

    private MainFeatureMediaController mediaController;

    private ProgressDialog mDialog;

    NextGenFragmentTransactionEngine nextGenFragmentTransactionEngine;

    NextGenPlayerBottomFragment imeBottomFragment;

    public static final Uri INTERSTITIAL_VIDEO_URI = Uri.parse(NextGenExperience.getMovieMetaData().getStyle().getInterstitialVideoURL());//Uri.parse("android.resource://com.wb.nextgen/" + R.raw.mos_nextgen_interstitial);

    private Uri currentUri = null;

    private DRMStatus drmStatus = DRMStatus.NOT_INITIATED;
    private boolean bInterstitialVideoComplete = false;
    //TextView imeText;
    //IMEElementsGridFragment imeGridFragment;
    private long lastTimeCode = -1;

    AbstractNextGenMainMovieFragment mainMovieFragment;

    static enum DRMStatus{
        SUCCESS, FAILED, IN_PROGRESS, NOT_INITIATED
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(this);
        mDialog.setCanceledOnTouchOutside(true);

        nextGenFragmentTransactionEngine = new NextGenFragmentTransactionEngine(this);

        setContentView(R.layout.next_gen_videoview);

        backgroundImageView = (ImageView)findViewById(R.id.ime_background_image_view);
        if (backgroundImageView != null){
            Glide.with(this).load(NextGenExperience.getMovieMetaData().getStyle().getBackgroundImageURL(NextGenStyle.NextGenAppearanceType.InMovie)).into(backgroundImageView);
            //PicassoTrustAll.loadImageIntoView(this, NextGenExperience.getMovieMetaData().getStyle().getBackgroundImageURL(NextGenStyle.NextGenAppearanceType.InMovie), backgroundImageView);
        }

        containerView = (RelativeLayout)findViewById(R.id.interstitial_container);

        skipThisView = findViewById(R.id.skip_this_layout);
        skipThisCounter = (ProgressBar) findViewById(R.id.skip_this_countdown);

        interstitialVideoView = (ObservableVideoView) findViewById(R.id.interstitial_video_view);

        interstitialVideoView.setOnErrorListener(getOnErrorListener());
        interstitialVideoView.setOnPreparedListener(getOnPreparedListener());
        interstitialVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.STOP, -1L);
                bInterstitialVideoComplete = true;
                playMainMovie();
            }
        });
        interstitialVideoView.requestFocus();

        imeBottomFragment = new NextGenPlayerBottomFragment();


        //transitLeftFragment(new NextGenIMEActorFragment());
        transitMainFragment(imeBottomFragment);
        try {
            mainMovieFragment = NextGenExperience.getMainMovieFragmentClass().newInstance();
            mainMovieFragment.setProgressDialog(mDialog);
            mainMovieFragment.setOnCompletionLister(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //launch extra
                    Intent intent = new Intent(NextGenPlayer.this, NextGenExtraActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            mediaController = new MainFeatureMediaController(this, mainMovieFragment);
            mediaController.setVisibilityChangeListener(new CustomMediaController.MediaControllerVisibilityChangeListener(){
                public void onVisibilityChange(boolean bShow){
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        if (bShow)
                            getSupportActionBar().show();
                        else
                            getSupportActionBar().hide();
                    }
                }
            });
            mainMovieFragment.setCustomMediaController(mediaController);
            mainMovieFragment.setPlaybackObject(NextGenExperience.getNextgenPlaybackObject());
            //mainMovieFragment.setOnCompletionListener
            mainMovieFragment.setNextGenVideoViewListener(new IVideoViewActionListener() {

                @Override
                public void onTimeBarSeekChanged(int currentTime) {
                    updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.SEEK, currentTime);
                }

                @Override
                public void onResume() {
                    updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.PAUSE, mainMovieFragment.getCurrentPosition());
                }

                @Override
                public void onPause() {
                    updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.RESUME, mainMovieFragment.getCurrentPosition());
                }
            });
        }catch (InstantiationException ex){

        }catch (IllegalAccessException iex){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideShowNextGenView();
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0 )
            finish();
        else if (getSupportFragmentManager().getBackStackEntryCount() == 1 && isPausedByIME){
            isPausedByIME = false;
            interstitialVideoView.start();
        }
    }


    @Override
    protected boolean shouldUseActionBarSpacer(){
        return false;
    }

    @Override
    String getTitleImageUrl(){
        return NextGenExperience.getMovieMetaData().getStyle().getTitleImageURL(NextGenStyle.NextGenAppearanceType.InMovie);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dialog.cancel();
        //this.finish();
    }

    protected void updateImeFragment(final NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, final long timecode){
        if (INTERSTITIAL_VIDEO_URI.equals(currentUri))
            return;

        if (lastTimeCode == timecode)
            return;

        lastTimeCode = timecode;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imeBottomFragment != null)
                    imeBottomFragment.playbackStatusUpdate(playbackStatus, timecode);

            }
        });

        switch (playbackStatus){
            case PREPARED:
                break;
            case STARTED:
                break;
            case STOP:
                break;
            case TIMESTAMP_UPDATE:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideShowNextGenView();
    }

    public int getLayoutViewId(){
        return R.layout.next_gen_videoview;
    }

    private void hideShowNextGenView(){
        //if (TabletUtils.isTablet()) {
            View nextGenView = findViewById(R.id.next_gen_ime_bottom_view);
            if (nextGenView == null)
                return;
            switch (this.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:

                    nextGenView.setVisibility(View.VISIBLE);
                    if (mediaController != null)
                        mediaController.hideShowControls(true);
                    break;
                 case Configuration.ORIENTATION_LANDSCAPE:
                    nextGenView.setVisibility(View.GONE);
                     getSupportActionBar().hide();
                    if (mediaController != null)
                        mediaController.hideShowControls(false);
            }
    }

    @Override
    boolean shouldHideActionBar(){
        return (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private class ErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {


            return true;
        }
    }


    protected MediaPlayer.OnErrorListener getOnErrorListener(){
        return new ErrorListener();
    }

    private class ProgressBarAnimation extends Animation{
        private ProgressBar progressBar;

        public ProgressBarAnimation(ProgressBar progressBar) {
            super();
            this.progressBar = progressBar;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = 0 + (100) * interpolatedTime;
            progressBar.setProgress((int) value);
        }

    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            interstitialVideoView.start();

            skipThisView.setVisibility(View.VISIBLE);
            skipThisCounter.setProgress(0);
            skipThisCounter.setProgress(100);

            ProgressBarAnimation skipProgressBarAnim = new ProgressBarAnimation(skipThisCounter);
            skipProgressBarAnim.setDuration(mp.getDuration());
            skipThisCounter.startAnimation(skipProgressBarAnim);

            return;




        }
    }


    protected MediaPlayer.OnPreparedListener getOnPreparedListener(){
        return new PreparedListener();
    }

    private void playMainMovie(){
        if (skipThisView != null)
            skipThisView.setVisibility(View.GONE);
        currentUri = Uri.parse("");

        if (drmStatus == DRMStatus.IN_PROGRESS){    // show loading
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setMessage(getResources().getString(R.string.loading));
            mDialog.show();
            return;
        }else if (drmStatus == DRMStatus.FAILED){
            //Show error Message and exit

            finish();
            return;
        } else if (!bInterstitialVideoComplete){
            // wait for interstitial video to be finished
            return;
        }
        interstitialVideoView.stopPlayback();
        interstitialVideoView.setVisibility(View.GONE);
        mDialog.hide();

        nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.video_view_frame, mainMovieFragment);
        if (imeUpdateTimer == null){
            imeUpdateTimer = new Timer();
        }
        if (imeUpdateTask == null){
            imeUpdateTask = new TimerTask() {
                @Override
                public void run() {
                    updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.TIMESTAMP_UPDATE, mainMovieFragment.getCurrentPosition());
                }
            };
            imeUpdateTimer.scheduleAtFixedRate(imeUpdateTask, 0, 1000);
        }


        updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.PREPARED, -1L);
        //mainMovieFragment
        /*videoView.setCustomMediaController(mediaController);
        if (INTERSTITIAL_VIDEO_URI.equals(currentUri)) {
            videoView.setOnTouchListener(null);
            Intent intent = getIntent();
            Uri uri = intent.getData();
            currentUri = uri;
            videoView.setVideoURI(uri);
            if (mediaController == null) {
                mediaController = new MainFeatureMediaController(this, videoView);

            }
        }*/
    }

    int resumePlayTime = -1;
    boolean shouldStartAfterResume = true;

    @Override
    public void onPause() {
        shouldStartAfterResume = mainMovieFragment.isPlaying();
        resumePlayTime = mainMovieFragment.getCurrentPosition();
        super.onPause();
    }

    public void onResume() {
        if (resumePlayTime != -1){
            mainMovieFragment.setResumeTime(resumePlayTime);
        }
        super.onResume();

        if (currentUri == null) {
            currentUri = INTERSTITIAL_VIDEO_URI;
            interstitialVideoView.setVisibility(View.VISIBLE);
            interstitialVideoView.setVideoURI(currentUri);
            interstitialVideoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    bInterstitialVideoComplete = true;
                    playMainMovie();
                    return true;
                }
            });
            drmStatus = DRMStatus.IN_PROGRESS;
            mainMovieFragment.streamStartPreparations(new ResultListener<Boolean>() {
                @Override
                public void onResult(Boolean result) {

                    drmStatus = DRMStatus.SUCCESS;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playMainMovie();
                        }
                    });

                }

                @Override
                public <E extends Exception> void onException(E e) {
                    drmStatus = DRMStatus.FAILED;
                }
            });

        } else{
            skipThisView.setVisibility(View.GONE);
            currentUri = Uri.parse("");
        }
        hideShowNextGenView();
    }



    @Override
    public void onDestroy() {
        if (mediaController != null){
            mediaController.onPlayerDestroy();
        }
        if (imeUpdateTask != null) {
            imeUpdateTask.cancel();
            imeUpdateTask = null;
        }
        if (imeUpdateTimer != null){
            imeUpdateTimer.cancel();
            imeUpdateTimer = null;
        }
        super.onDestroy();

    }

    @Override
    public void resetUI(boolean isRoot){

    }

    public int getMainFrameId(){
        return R.id.next_gen_ime_bottom_view;
    }

    public int getLeftFrameId(){
        return R.id.next_gen_ime_bottom_view;
    }

    public int getRightFrameId(){
        return R.id.next_gen_ime_bottom_view;

    }

    boolean isPausedByIME = false;
    public void pausMovieForImeECPiece(){
        mainMovieFragment.pause();
        isPausedByIME = true;
    }

    @Override
    public void transitLeftFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), getLeftFrameId(), nextFragment);
    }

    @Override
    public void transitRightFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), getRightFrameId(), nextFragment);
    }

    @Override
    public void transitMainFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), getMainFrameId(), nextFragment);
    }

    @Override
    public int getLeftButtonLogoId(){
        return R.drawable.home_logo;
    }

    @Override
    public String getBackgroundImgUri(){
        return "";//NextGenExperience.getMovieMetaData().getStyle().getBackgroundImageURL(NextGenStyle.NextGenAppearanceType.InMovie);
    }

    @Override
    public String getLeftButtonText(){
        return getResources().getString(R.string.home_button_text);
    }

    public String getRightTitleImageUri(){
        return "";

    }

    @Override
    protected void onLeftTopActionBarButtonPressed(){
        finish();
    }

    @Override
    public String getRightTitleText(){
        return "";
    }
}
