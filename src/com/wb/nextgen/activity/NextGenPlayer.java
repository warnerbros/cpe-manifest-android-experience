package com.wb.nextgen.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;

import com.flixster.android.captioning.CaptionedPlayer;
import com.wb.nextgen.R;

import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.fragment.NextGenPlayerBottomFragment;
import com.wb.nextgen.interfaces.NextGenFragmentTransactionInterface;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.TabletUtils;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;
import com.wb.nextgen.widget.MainFeatureMediaController;
import com.wb.nextgen.widget.NextGenMediaController;

import net.flixster.android.drm.IVideoViewActionListener;
import net.flixster.android.drm.ObservableVideoView;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by gzcheng on 1/5/16.
 */
public class NextGenPlayer extends CaptionedPlayer implements NextGenFragmentTransactionInterface {


    protected ObservableVideoView videoView;

    protected NextGenPlayerBottomFragment imeFragment;

    private TimerTask imeUpdateTask;

    private Timer imeUpdateTimer;

    private Button actionBarLeftButton;

    private MainFeatureMediaController mediaController;

    NextGenFragmentTransactionEngine nextGenFragmentTransactionEngine;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
         //   actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarCustomView = inflator.inflate(R.layout.action_bar_custom_view, null);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(null);
        actionBar.setTitle("");

        //actionBarLeftButton = (Button) actionBarCustomView.findViewById(R.id.action_bar_left_button);
        ImageView centerBanner = (ImageView) actionBarCustomView.findViewById(R.id.action_bar_center_banner);
        ImageView rightLogo = (ImageView) actionBarCustomView.findViewById(R.id.action_bar_right_logo);
        actionBar.setCustomView(actionBarCustomView);

        if (centerBanner != null)
            PicassoTrustAll.loadImageIntoView(this, DemoData.getMovieLogoUrl(), centerBanner);

        actionBarLeftButton = (Button) actionBarCustomView.findViewById(R.id.action_bar_left_button);

        nextGenFragmentTransactionEngine = new NextGenFragmentTransactionEngine(this);
        setContentView(R.layout.next_gen_videoview);
        videoView = (ObservableVideoView) findViewById(R.id.surface_view);
        mediaController = new MainFeatureMediaController(this);
        videoView.setMediaController(mediaController);
        videoView.setOnErrorListener(getOnErrorListener());
        videoView.setOnPreparedListener(getOnPreparedListener());
        videoView.setOnCompletionListener(getOnCompletionListener());
        videoView.requestFocus();
        imeFragment = (NextGenPlayerBottomFragment)getSupportFragmentManager().findFragmentById(R.id.next_gen_ime_frame);
        videoView.setVideoViewListener(new IVideoViewActionListener() {

            @Override
            public void onTimeBarSeekChanged(int currentTime) {
                updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.SEEK, currentTime);
            }

            @Override
            public void onResume() {
                updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.PAUSE, videoView.getCurrentPosition());
            }

            @Override
            public void onPause() {
                updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.RESUME, videoView.getCurrentPosition());
            }
        });


    }

    protected void updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, long timecode){
        if (imeFragment != null){
            imeFragment.playbackStatusUpdate(playbackStatus, timecode);
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
        if (TabletUtils.isTablet()) {
            View nextGenView = findViewById(R.id.nextgenview);
            if (nextGenView == null)
                return;
            switch (this.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    nextGenView.setVisibility(View.VISIBLE);
                    mediaController.hideShowControls(true);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    nextGenView.setVisibility(View.GONE);
                    mediaController.hideShowControls(false);
            }
        }
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

    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {

            videoView.start();
            if (imeUpdateTimer == null){
                imeUpdateTimer = new Timer();
            }
            if (imeUpdateTask == null){
                imeUpdateTask = new TimerTask() {
                    @Override
                    public void run() {
                        updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.TIMESTAMP_UPDATE, videoView.getCurrentPosition());
                    }
                };
                imeUpdateTimer.scheduleAtFixedRate(imeUpdateTask, 0, 1000);
            }


            updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.PREPARED, -1L);

        }
    }


    protected MediaPlayer.OnPreparedListener getOnPreparedListener(){
        return new PreparedListener();
    }

    private class CompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            updateImeFragment(NextGenPlaybackStatusListener.NextGenPlaybackStatus.STOP, -1L);
            /*trackPlaybackEvent("playback_complete", 0);
            NextGenLogger.d(F.TAG_DRM, "WidevinePlayer.onCompletion: rightId=" + rightId + ", resetting seek time");

            mPlaybackCompleted = true;
            savePlayPosition(0);*/
            finish();

        }
    }

    protected MediaPlayer.OnCompletionListener getOnCompletionListener(){
        return new CompletionListener();
    }

    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);

        hideShowNextGenView();
    }

    @Override
    protected void onDestroy() {
        //ContentLocker content = FlixsterApplication.getCurrentPlayableContent();
        super.onDestroy();
        if (imeUpdateTask != null) {
            imeUpdateTask.cancel();
            imeUpdateTask = null;
        }
        if (imeUpdateTimer != null){
            imeUpdateTimer.cancel();
            imeUpdateTimer = null;
        }
        //FlixsterApplication.setCurrentPlayableContent(content);

    }

    @Override
    public void resetUI(boolean isRoot){

    }

    @Override
    public void transitLeftFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(imeFragment.getLeftFrameId(), nextFragment);
    }

    @Override
    public void transitRightFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(imeFragment.getRightFrameId(), nextFragment);
    }

    @Override
    public void transitMainFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(imeFragment.getMainFrameId(), nextFragment);
    }
}
