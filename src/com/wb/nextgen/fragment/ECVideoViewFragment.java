package com.wb.nextgen.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;
import com.wb.nextgen.util.utils.StringHelper;
import com.wb.nextgen.videoview.ObservableVideoView;
import com.wb.nextgen.widget.CustomMediaController;
import com.wb.nextgen.widget.ECMediaController;
import com.wb.nextgen.widget.FixedAspectRatioFrameLayout;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECVideoViewFragment extends Fragment{
    protected ObservableVideoView videoView;

    ECMediaController mediaController;
    protected TextView selectedECNameTextView;
    protected TextView ecDurationTextView;
    protected TextView countDownTextView;
    protected View countDownCountainer;
    protected View contentMetaFrame;
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
    boolean shouldHideMetaData = false;

    FixedAspectRatioFrameLayout.Priority aspectFramePriority = FixedAspectRatioFrameLayout.Priority.WIDTH_PRIORITY;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ec_video_frame_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView = (ObservableVideoView) view.findViewById(R.id.ec_video_view);
        mediaController = new ECMediaController(getActivity(), videoView);
        selectedECNameTextView = (TextView)view.findViewById(R.id.ec_content_name);
        ecDurationTextView = (TextView)view.findViewById(R.id.ec_content_runtime);
        previewImageView = (ImageView)view.findViewById(R.id.ec_video_preview_image);
        previewFrame = (RelativeLayout)view.findViewById(R.id.ec_video_preview_image_frame);
        previewPlayBtn = (ImageButton)view.findViewById(R.id.ec_video_preview_playButton);
        aspectRatioFrame = (FixedAspectRatioFrameLayout) view.findViewById(R.id.ec_video_aspect_ratio_frame);
        countDownCountainer = view.findViewById(R.id.count_down_container);
        countDownTextView = (TextView) view.findViewById(R.id.count_down_text_view);
        countDownProgressBar = (ProgressBar) view.findViewById(R.id.count_down_progressBar);
        contentMetaFrame = view.findViewById(R.id.ec_content_meta_frame);

        if (contentMetaFrame != null){
            contentMetaFrame.setVisibility(shouldHideMetaData ? View.GONE : View.VISIBLE);
        }

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
        videoView.setCustomMediaController(mediaController);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            private Handler mHandler = new Handler();
            int counter = COUNT_DOWN_SECONDS;
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (ecsAdaptor != null) {
                    if (ecsAdaptor.shouldStartCountDownForNext()){
                        //new
                        startRepeatingTask();
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
            PicassoTrustAll.loadImageIntoView(getActivity(), bgImageUrl, bgImageView);
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

    public void setShouldHideMetaData(boolean bHide){
        shouldHideMetaData = bHide;
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
                if (ecDurationTextView != null) {
                    ecDurationTextView.setText(avItem.getFullDurationString());
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


    public void onFullScreenChange(boolean bFullscreen){
        contentMetaFrame.setVisibility(bFullscreen && !shouldHideMetaData? View.GONE : View.VISIBLE);

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
