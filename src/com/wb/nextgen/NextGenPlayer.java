package com.wb.nextgen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.MediaController;

import com.flixster.android.captioning.CaptionedPlayer;
import com.wb.nextgen.R;

import com.wb.nextgen.fragment.NextGenPlayerBottomFragment;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.util.TabletUtils;

import net.flixster.android.drm.IVideoViewActionListener;
import net.flixster.android.drm.ObservableVideoView;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by gzcheng on 1/5/16.
 */
public class NextGenPlayer extends CaptionedPlayer {


    protected ObservableVideoView videoView;

    protected NextGenPlayerBottomFragment imeFragment;

    private TimerTask imeUpdateTask;

    private Timer imeUpdateTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.next_gen_videoview);
        videoView = (ObservableVideoView) findViewById(R.id.surface_view);
        videoView.setMediaController(new MediaController(this));
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
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    nextGenView.setVisibility(View.GONE);
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
            FlixsterLogger.d(F.TAG_DRM, "WidevinePlayer.onCompletion: rightId=" + rightId + ", resetting seek time");

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
}
