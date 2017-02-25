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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.interfaces.ECVideoPlayerInterface;
import com.wb.nextgenlibrary.interfaces.IMEVideoStatusListener;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.videoview.IVideoViewActionListener;
import com.wb.nextgenlibrary.videoview.ObservableVideoView;
import com.wb.nextgenlibrary.widget.ECMediaController;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECVideoViewFragment extends ECViewFragment implements ECVideoPlayerInterface, IVideoViewActionListener {
    protected ObservableVideoView videoView;

    ECMediaController mediaController;
    //protected TextView selectedECNameTextView;
    //protected TextView descriptionTextView;
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
    boolean shouldExitWhenComplete = false;
    IMEVideoStatusListener videoStatusListener;

    FixedAspectRatioFrameLayout.Priority aspectFramePriority = null;

    int resumeTime = -1;

    public final static int COUNT_DOWN_SECONDS = 5;

    ECVideoListAdaptor ecsAdaptor = null;
    private int previousPlaybackTime = 0;

    public static interface ECVideoListAdaptor{
        void playbackFinished();
        boolean shouldStartCountDownForNext();
    }

    String getReportContentName(){
        if (selectedAVItem != null)
            return selectedAVItem.getTitle();
        else
            return null;
    }

    public void setVideoStatusListener(IMEVideoStatusListener listener){
        this.videoStatusListener = listener;
    }

    public void setBGImageUrl(String url){
        bgImageUrl = url;
    }

    @Override
    public int getContentViewId(){
        return R.layout.ec_video_frame_view;
    }

    public void setShouldExitWhenComplete(boolean bTrue){
        shouldExitWhenComplete = bTrue;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView = (ObservableVideoView) view.findViewById(R.id.ec_video_view);
        mediaController = new ECMediaController(getActivity(), videoView);
        //selectedECNameTextView = (TextView)view.findViewById(R.id.ec_content_name);
        //descriptionTextView = (TextView)view.findViewById(R.id.ec_content_runtime);
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
                    NGEAnalyticData.reportEvent(getActivity(), ECVideoViewFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_VIDEO, selectedAVItem.videoId, null);
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
                onVideoComplete();
                if (shouldExitWhenComplete){
                    closeBtn.callOnClick();
                }else if (ecsAdaptor != null) {
                    if (ecsAdaptor.shouldStartCountDownForNext()){
                        //new
                        startRepeatingTask();
                    }else {
                        if (getActivity() != null) {                // only do this if the activity is still alive
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
                                            countDownTextView.setText(String.format(getResources().getString(R.string.count_down_text), Integer.toString(counter)) );
                                            countDownProgressBar.setProgress(counter);
                                        }else {
                                            countDownTextView.setText("");
                                            countDownCountainer.setVisibility(View.GONE);
                                        }
                                    }

                                }
                            });

                            if (counter < 0){
                                ecsAdaptor.playbackFinished();
                                stopRepeatingTask();
                                shouldFinish = true;
                            }
                            counter = counter - 1;
                        }
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

        videoView.setVideoViewListener(getVideoViewActionListern());

        bgImageView = (ImageView) view.findViewById(R.id.ec_video_frame_bg);

        if (bgImageView != null && !StringHelper.isEmpty(bgImageUrl)){
            NextGenGlide.load(getActivity(), bgImageUrl).fitCenter().into(bgImageView);
        }
        if (selectedAVItem != null){
            setAudioVisualItem(selectedAVItem);
        }
    }

    boolean isClipPlaying;
    //******* Observe videoView Playback
    protected IVideoViewActionListener getVideoViewActionListern(){
        return new IVideoViewActionListener() {
            @Override
            public void onVideoPause() {
                if (videoStatusListener != null){
                    videoStatusListener.onVideoPause();
                }
            }

            @Override
            public void onVideoStart() {
                isClipPlaying = true;
                if (videoStatusListener != null)
                    videoStatusListener.onVideoStartPlaying();
            }

            @Override
            public void onVideoResume() {
                if (videoStatusListener != null){
                    videoStatusListener.onVideoResume();
                }
            }

            @Override
            public void onVideoComplete(){
                isClipPlaying = false;
                if (videoStatusListener != null){
                    videoStatusListener.onVideoComplete();
                }
            }

            @Override
            public void onTimeBarSeekChanged(int currentTime) {

            }
        };
    }

    //******* Observe Main Movie Playback
    @Override
    public void onVideoPause() {
        if (isClipPlaying)
            videoView.start();
    }

    @Override
    public void onVideoStart() {
        if (isClipPlaying)
            videoView.pause();
    }

    @Override
    public void onVideoResume() {
        if (isClipPlaying)
            videoView.pause();

    }

    @Override
    public void onVideoComplete(){

    }

    @Override
    public void onTimeBarSeekChanged(int currentTime) {

    }

    @Override
    public void setResumeTimeMillisecond(int resumeTime){
        this.resumeTime = resumeTime;
    }

    @Override
    public int getCurrentPlaybackTimeMillisecond(){
        if (videoView != null)
            return videoView.getCurrentPosition();
        else
            return 0;
    }

    public void setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority priority){
        if (aspectRatioFrame != null)
            aspectRatioFrame.setAspectRatioPriority(priority);
        aspectFramePriority = priority;
    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            int startTime = previousPlaybackTime;
            if (resumeTime > 0)
                startTime = resumeTime;

            if (shouldAutoPlay || resumeTime > 0) {

                previewFrame.setVisibility(View.GONE);
                previewPlayBtn.setVisibility(View.GONE);
                if (startTime != 0){
                    videoView.seekTo(startTime);
                    //added: tr 9/19
                    mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            videoView.start();
                        }
                    });
                }else
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
    public void onPause(){
        super.onPause();
        videoView.pause();
        previousPlaybackTime = videoView.getCurrentPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.setVisibility(View.VISIBLE);

        if (bSetOnResume){
            bSetOnResume = false;
            setAudioVisualItem(selectedAVItem);
        }
    }

    @Override
    public void onDestroyView(){
        videoView.stopPlayback();
        videoView.setVideoURI(null);
        videoView.setMediaController(null);
        mediaController.onPlayerDestroy();
        mediaController = null;

        if (videoStatusListener != null)
            videoStatusListener.onFragmentDestroyed();
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

            if (/*selectedECNameTextView != null && */videoView != null) {
                //selectedECNameTextView.setText(avItem.getTitle());
                //if (descriptionTextView != null) {
                //    descriptionTextView.setText(avItem.getSummary());
                //}
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
                videoView.setVideoURI(Uri.parse(avItem.getVideoUrl()));
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

    public void stopPlayback(){
        videoView.stopPlayback();
    }

}
