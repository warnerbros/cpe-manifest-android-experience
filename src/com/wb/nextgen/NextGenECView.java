package com.wb.nextgen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import com.flixster.android.captioning.CaptionedPlayer;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;

import net.flixster.android.drm.ObservableVideoView;

/**
 * Created by gzcheng on 2/25/16.
 */
public class NextGenECView extends CaptionedPlayer {
    protected ObservableVideoView videoView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.next_gen_ecs_view);
        videoView = (ObservableVideoView) findViewById(R.id.surface_view);
        videoView.setMediaController(new MediaController(this));
        //videoView.setOnErrorListener(getOnErrorListener());
        videoView.setOnPreparedListener(new PreparedListener());
        //videoView.setOnCompletionListener(getOnCompletionListener());
        videoView.requestFocus();
    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {

            videoView.start();
        }
    }

    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);

        //hideShowNextGenView();
    }
}
