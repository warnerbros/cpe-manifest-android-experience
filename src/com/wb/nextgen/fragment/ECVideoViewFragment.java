package com.wb.nextgen.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.widget.ECMediaController;

import net.flixster.android.drm.ObservableVideoView;

/**
 * Created by gzcheng on 3/31/16.
 */
public class ECVideoViewFragment extends Fragment {
    protected ObservableVideoView videoView;

    ECMediaController mediaController;
    protected TextView selectedECNameTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ec_video_frame_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView = (ObservableVideoView) view.findViewById(R.id.surface_view);
        mediaController = new ECMediaController(getActivity(), (RelativeLayout) view.findViewById(R.id.video_view_container));
        selectedECNameTextView = (TextView)view.findViewById(R.id.ec_content_name);

        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(new PreparedListener());
        videoView.requestFocus();
    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*Intent intent = getIntent();
        Uri uri = intent.getData();*/
        videoView.setVisibility(View.VISIBLE);
    }

    public void setAudioVisualItem(MovieMetaData.ECAudioVisualItem avItem){
        if (avItem != null) {
            selectedECNameTextView.setText(avItem.title);
            videoView.setVideoURI(Uri.parse(avItem.videoUrl));
        }
    }


    public void onFullScreenChange(boolean bFullscreen){
        selectedECNameTextView.setVisibility(bFullscreen ? View.GONE : View.VISIBLE);
    }

    public void onRequestToggleFullscreen(boolean bFullscreen) {
        mediaController.onToggledFullScreen(bFullscreen);
    }
}
