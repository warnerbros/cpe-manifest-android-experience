package com.wb.nextgen.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.fragment.ECVideoViewFragment;
import com.wb.nextgen.widget.ECMediaController;


/**
 * Created by gzcheng on 3/7/16.
 */
public class ECVideoActivity extends AbstractECView {

    ECVideoViewFragment rightVideoFrame;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rightVideoFrame = (ECVideoViewFragment) getSupportFragmentManager().findFragmentById(R.id.ec_video_view_fragment);


    }


    @Override
    public int getListItemViewLayoutId(){
        return R.layout.next_gen_ec_list_item;
    }


    @Override
    public int getContentViewId(){
        return R.layout.next_gen_ecs_view;
    }

    @Override
    public void onLeftListItemSelected(MovieMetaData.ExperienceData ec){
        if (ec != null && ec.audioVisualItems.size() > 0) {
            rightVideoFrame.setAudioVisualItem(ec.audioVisualItems.get(0));
            //selectedECNameTextView.setText(ec.audioVisualItems.get(0).title);
            //videoView.setVideoURI(Uri.parse(ec.audioVisualItems.get(0).videoUrl));
        }
    }

    @Override
    public void onRequestToggleFullscreen() {
        super.onRequestToggleFullscreen();
        rightVideoFrame.onRequestToggleFullscreen(isContentFullScreen);
    }

    @Override
    public void onFullScreenChange(boolean bFullscreen){
        rightVideoFrame.onFullScreenChange(bFullscreen);
    }


}
