package com.wb.nextgen.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.flixster.android.captioning.CaptionedPlayer;
import com.wb.nextgen.R;
import com.wb.nextgen.fragment.NextGenPlayerBottomFragment;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;

import net.flixster.android.drm.IVideoViewActionListener;

/**
 * Created by gzcheng on 3/11/16.
 */
public class InterStitualVideoPlayerActivity extends Activity{
    VideoView videoView;
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.interstitual_video_view);
        videoView = (VideoView)findViewById(R.id.video_view);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                InterStitualVideoPlayerActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(InterStitualVideoPlayerActivity.this, NextGenPlayer.class);
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/feature/ManOfSteel_Clean.mp4"), "video/*");
                        startActivity(intent);
                       finish();
                    }
                });
            }
        });
        videoView.requestFocus();

    }

    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);


    }

}
