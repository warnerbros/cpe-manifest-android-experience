package com.wb.nextgenlibrary.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.videoview.ObservableVideoView;
import com.wb.nextgenlibrary.widget.ECMediaController;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECVideoViewFragment extends ECViewFragment{
    protected ObservableVideoView videoView;

    ECMediaController mediaController;
    protected TextView selectedECNameTextView;
    protected TextView descriptionTextView;
    protected TextView countDownTextView;
    protected View countDownCountainer;
    protected ProgressBar countDownProgressBar;

    ImageView previewImageView = null;
    RelativeLayout previewFrame = null;
    ImageButton previewPlayBtn = null;
    FixedAspectRatioFrameLayout aspectRatioFrame = null;

    MovieMetaData.AudioVisualItem selectedAVItem = null;
    boolean bSetOnResume= false;
    ImageView bgImageView;

    String bgImageUrl = null;
    boolean bCountDown = false;

    boolean shouldAutoPlay = true;

    FixedAspectRatioFrameLayout.Priority aspectFramePriority = null;

    private static int COUNT_DOWN_SECONDS = 5;

    ECVideoListAdaptor ecsAdaptor = null;

    public static interface ECVideoListAdaptor{
        void playbackFinished();
        boolean shouldStartCountDownForNext();
    }

    public void setBGImageUrl(String url){
        bgImageUrl = url;
    }

    @Override
    public int getContentViewId(){
        return R.layout.ec_video_frame_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView = (ObservableVideoView) view.findViewById(R.id.ec_video_view);
        mediaController = new ECMediaController(getActivity(), videoView);
        selectedECNameTextView = (TextView)view.findViewById(R.id.ec_content_name);
        descriptionTextView = (TextView)view.findViewById(R.id.ec_content_runtime);
        previewImageView = (ImageView)view.findViewById(R.id.ec_video_preview_image);
        previewFrame = (RelativeLayout)view.findViewById(R.id.ec_video_preview_image_frame);
        previewPlayBtn = (ImageButton)view.findViewById(R.id.ec_video_preview_playButton);
        aspectRatioFrame = (FixedAspectRatioFrameLayout) view.findViewById(R.id.ec_video_aspect_ratio_frame);
        countDownCountainer = view.findViewById(R.id.count_down_container);
        countDownTextView = (TextView) view.findViewById(R.id.count_down_text_view);
        countDownProgressBar = (ProgressBar) view.findViewById(R.id.count_down_progressBar);

        if (countDownCountainer != null)
            countDownCountainer.setVisibility(View.INVISIBLE);
        if (countDownProgressBar != null){
            countDownProgressBar.setMax(COUNT_DOWN_SECONDS);
        }

        if (aspectRatioFrame != null){
            aspectRatioFrame.setAspectRatioPriority(aspectFramePriority);
        }

        if (previewPlayBtn != null) {
            previewPlayBtn.setVisibility(View.GONE);
            previewPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (previewFrame != null)
                        previewFrame.setVisibility(View.GONE);
                    videoView.start();
                }
            });
        }
        //videoView.setMediaController(mediaController);
        videoView.setMediaController(mediaController);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            private Handler mHandler = new Handler();
            int counter = COUNT_DOWN_SECONDS;
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (ecsAdaptor != null) {
                    if (ecsAdaptor.shouldStartCountDownForNext()){
                        //new
                        startRepeatingTask();
                    }else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    videoView.seekTo(0);
                                    if (previewFrame != null) {
                                        if (!StringHelper.isEmpty(selectedAVItem.getPosterImgUrl())) {
                                            Picasso.with(getActivity()).load(selectedAVItem.getPreviewImageUrl()).fit().into(previewImageView);
                                        }
                                        previewFrame.setVisibility(View.VISIBLE);
                                    }
                                    if (previewPlayBtn != null)
                                        previewPlayBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                }
            }
            Runnable mStatusChecker = new Runnable() {
                @Override
                public void run() {
                    boolean shouldFinish = false;

                    try {

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (countDownCountainer != null) {
                                        if (counter >= 0) {
                                            countDownCountainer.setVisibility(View.VISIBLE);
                                            countDownTextView.setText(String.format(getResources().getString(R.string.count_down_text), counter) );
                                            countDownProgressBar.setProgress(counter);
                                        }else {
                                            countDownTextView.setText("");
                                            countDownCountainer.setVisibility(View.GONE);
                                        }
                                    }

                                }
                            });
                        }

                        if (counter < 0){
                            ecsAdaptor.playbackFinished();
                            stopRepeatingTask();
                            shouldFinish = true;
                        }
                        counter = counter - 1;
                    } finally {
                        // 100% guarantee that this always happens, even if
                        // your update method throws an exception
                        if (!shouldFinish)
                            mHandler.postDelayed(mStatusChecker, 1000);
                    }
                }
            };

            void startRepeatingTask() {
                counter = 5;
                mStatusChecker.run();
            }

            void stopRepeatingTask() {
                mHandler.removeCallbacks(mStatusChecker);
            }
        });

        videoView.setOnPreparedListener(new PreparedListener());
        videoView.requestFocus();

        bgImageView = (ImageView)view.findViewById(R.id.ec_video_frame_bg);

        if (bgImageView != null && !StringHelper.isEmpty(bgImageUrl)){
            Glide.with(getActivity()).load(bgImageUrl).fitCenter().into(bgImageView);
            //PicassoTrustAll.loadImageIntoView(getActivity(), bgImageUrl, bgImageView);
        }
    }


    public void setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority priority){
        if (aspectRatioFrame != null)
            aspectRatioFrame.setAspectRatioPriority(priority);
        aspectFramePriority = priority;
    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (shouldAutoPlay) {
                videoView.start();
            }else {
                if (previewPlayBtn != null){
                    previewPlayBtn.setVisibility(View.VISIBLE);
                }
                shouldAutoPlay = true;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*Intent intent = getIntent();
        Uri uri = intent.getData();*/
        videoView.setVisibility(View.VISIBLE);

        if (bSetOnResume){
            bSetOnResume = false;
            setAudioVisualItem(selectedAVItem);
        }
    }

    @Override
    public void onDestroyView(){
        videoView.setMediaController(null);
        videoView.stopPlayback();
        mediaController.onPlayerDestroy();
        mediaController = null;
        super.onDestroyView();
    }

    public void setEnableCountDown(boolean bCountDown){
        this.bCountDown = bCountDown;
    }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    videoView.setBackground(new BitmapDrawable(bitmap));

                }
            });
        }
        @Override

        public void onPrepareLoad(Drawable var1){
            NextGenLogger.d(F.TAG, "haha");
        }

        @Override
        public void onBitmapFailed(Drawable var1) {
            NextGenLogger.d(F.TAG, "haha");
        }
    };

    public void setAudioVisualItem(MovieMetaData.AudioVisualItem avItem){
        if (avItem != null) {
            selectedAVItem = avItem;
            if (countDownCountainer != null)
                countDownCountainer.setVisibility(View.INVISIBLE);

            if (selectedECNameTextView != null && videoView != null) {
                selectedECNameTextView.setText(avItem.getTitle());
                if (descriptionTextView != null) {
                    descriptionTextView.setText(avItem.getSummary());
                }
                if (!shouldAutoPlay) {
                    if(videoView.isPlaying())
                        videoView.stopPlayback();
                    if (previewFrame != null) {
                        previewFrame.setVisibility(View.VISIBLE);
                        previewPlayBtn.setVisibility(View.GONE);
                    }
                    if (!StringHelper.isEmpty(selectedAVItem.getPosterImgUrl())) {

                        Picasso.with(getActivity()).load(selectedAVItem.getPreviewImageUrl()).fit().into(previewImageView);

                    }
                }else{
                    if (previewFrame != null) {
                        previewFrame.setVisibility(View.GONE);
                        previewPlayBtn.setVisibility(View.GONE);
                    }
                }
                videoView.setVideoURI(Uri.parse(avItem.videoUrl));
                if (mediaController != null) {
                    mediaController.reset();
                }
            }else{
                bSetOnResume = true;
            }
        }
    }

    public void onRequestToggleFullscreen(boolean bFullscreen) {
        mediaController.onToggledFullScreen(bFullscreen);
    }

    public void setEcsAdaptor(ECVideoListAdaptor adaptor){
        ecsAdaptor = adaptor;
    }

    public void setShouldAutoPlay(boolean shouldAutoPlay){
        this.shouldAutoPlay = shouldAutoPlay;
    }
}
