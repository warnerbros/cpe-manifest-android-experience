package com.wb.nextgen.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;

import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.widget.NextGenVideoView;

/**
 * Created by gzcheng on 1/7/16.
 */
public class NextGenActivity extends FragmentActivity implements View.OnClickListener {
    // wrapper of ProfileViewFragment

    VideoView startupVideoView;
    //RelativeLayout startUpVideoViewFrame;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.next_gen_startup_view);

        startupVideoView = (VideoView)findViewById(R.id.startup_video_view);
        //startUpVideoViewFrame = (RelativeLayout)findViewById(R.id.startuo_video_view_frame);


        ImageButton playMovieButton;
        ImageButton extraButton;
        ImageView movieLogo;


        ImageView bg = (ImageView)findViewById(R.id.next_gen_startup_layout);
        if (bg != null){
            String bgImageUri = "android.resource://com.wb.nextgen/" + R.drawable.front_page_bg;
            PicassoTrustAll.loadImageIntoView(NextGenApplication.getContext(), bgImageUri, bg);
        }
        movieLogo = (ImageView) findViewById(R.id.next_gen_startup_movie_logo);
        if (movieLogo != null){
            movieLogo.setImageResource(R.drawable.man_of_sett_top_logo);
        }
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
                public void onPrepared(MediaPlayer mp) {
                    startupVideoView.start();
                }
            });
            startupVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
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
}
