package com.wb.nextgenlibrary.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.interfaces.ECVideoPlayerInterface;
import com.wb.nextgenlibrary.interfaces.IMEVideoStatusListener;
import com.wb.nextgenlibrary.interfaces.NGEPlayerInterface;
import com.wb.nextgenlibrary.util.Size;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.videoview.IVideoViewActionListener;
import com.wb.nextgenlibrary.widget.ECMediaController;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECVideoViewFragment extends ECViewFragment implements ECVideoPlayerInterface, IVideoViewActionListener{


    ECMediaController mediaController;
    //protected TextView selectedECNameTextView;
    //protected TextView descriptionTextView;
    protected TextView countDownTextView;
    protected View countDownCountainer;
    protected ProgressBar countDownProgressBar, loadingView;

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

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DataSource.Factory mediaDataSourceFactory;
    protected SimpleExoPlayerView videoView;
    private SimpleExoPlayer player;

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
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        videoView = (SimpleExoPlayerView) view.findViewById(R.id.ec_video_view);
        mediaDataSourceFactory = buildDataSourceFactory(true);
        if (videoView != null){
            @SimpleExoPlayer.ExtensionRendererMode int extensionRendererMode = SimpleExoPlayer.EXTENSION_RENDERER_MODE_ON;//SimpleExoPlayer.EXTENSION_RENDERER_MODE_OFF;
            TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            //trackSelectionHelper = new TrackSelectionHelper(trackSelector, videoTrackSelectionFactory);
            player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, new DefaultLoadControl(),
                    null, extensionRendererMode);
            player.setPlayWhenReady(false);
            videoView.setPlayer(player);
            //videoView.setOnClickListener(showButtonOnClickLister);
        }
        mediaController = new ECMediaController(getActivity(), new NGEPlayerInterface() {
            @Override
            public boolean isPlaying() {
                if (player != null) {
                    return player.getPlaybackState() == ExoPlayer.STATE_READY && player.getPlayWhenReady();
                }
                return false;
            }
        });
        mediaController.setMediaPlayer(playerControl);
        previewImageView = (ImageView)view.findViewById(R.id.ec_video_preview_image);
        previewFrame = (RelativeLayout)view.findViewById(R.id.ec_video_preview_image_frame);
        previewPlayBtn = (ImageButton)view.findViewById(R.id.ec_video_preview_playButton);
        aspectRatioFrame = (FixedAspectRatioFrameLayout) view.findViewById(R.id.ec_video_aspect_ratio_frame);
        countDownCountainer = view.findViewById(R.id.count_down_container);
        countDownTextView = (TextView) view.findViewById(R.id.count_down_text_view);
        countDownProgressBar = (ProgressBar) view.findViewById(R.id.count_down_progressBar);

        loadingView = (ProgressBar)view.findViewById(R.id.next_gen_loading_progress_bar);
        if (loadingView != null){
            loadingView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

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
                    playerControl.start();
                    NGEAnalyticData.reportEvent(getActivity(), ECVideoViewFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_VIDEO, selectedAVItem.videoId, null);
                }
            });
        }

        player.addListener(ecVideoViewListener);

        videoView.requestFocus();

        bgImageView = (ImageView) view.findViewById(R.id.ec_video_frame_bg);

        if (bgImageView != null && !StringHelper.isEmpty(bgImageUrl)){
            NextGenGlide.load(getActivity(), bgImageUrl).fitCenter().into(bgImageView);
        }
        if (selectedAVItem != null){
            setAudioVisualItem(selectedAVItem);
        }
    }
    private ECVideoEventListner ecVideoViewListener = new ECVideoEventListner();
    class ECVideoEventListner implements ExoPlayer.EventListener{
            boolean isFreshloaded = true;
            private Handler mHandler = new Handler();
            int counter = COUNT_DOWN_SECONDS;
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch(playbackState) {
                    case ExoPlayer.STATE_ENDED:
                        onVideoComplete();
                        if (shouldExitWhenComplete){                                // exit this
                            closeBtn.callOnClick();
                        }else if (ecsAdaptor != null) {
                            if (ecsAdaptor.shouldStartCountDownForNext()){          // try to play the next clip
                                //new
                                startRepeatingTask();
                            }else {                                                 // show the preview thumbnail
                                if (getActivity() != null) {                        // only do this if the activity is still alive
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            player.seekTo(0);
                                            player.setPlayWhenReady(false);
                                            shouldAutoPlay = false;
                                            resumeTime = 0;
                                            if (previewFrame != null) {
                                                if (!StringHelper.isEmpty(selectedAVItem.getPosterImgUrl())) {
                                                    NextGenGlide.load(getActivity(), selectedAVItem.getPreviewImageUrl()).fitCenter().into(previewImageView);
                                                    //Picasso.with(getActivity()).load(selectedAVItem.getPreviewImageUrl()).fit().into(previewImageView);
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
                        break;
                    case ExoPlayer.STATE_READY:
                        if (loadingView != null)
                            loadingView.setVisibility(View.GONE);
                        int startTime = previousPlaybackTime;

                        if (resumeTime > 0)
                            startTime = resumeTime;

                        if (shouldAutoPlay || startTime > 0) {

                            if (player.getPlayWhenReady()) {
                                previewFrame.setVisibility(View.GONE);
                                previewPlayBtn.setVisibility(View.GONE);
                                if (startTime > 0) {
                                    player.seekTo(startTime);
                                    resumeTime = -1;
                                    previousPlaybackTime = -1;
                                }
                                if (!playerControl.isPlaying())
                                    playerControl.start();
                            }
                        }else {
                            if (previewPlayBtn != null){
                                previewPlayBtn.setVisibility(View.VISIBLE);
                            }
                            shouldAutoPlay = true;
                        }
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

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
        public void startRepeatingTask() {
            counter = 5;
            mStatusChecker.run();
        }

        public void stopRepeatingTask() {
            mHandler.removeCallbacks(mStatusChecker);
            if (countDownTextView != null)
                countDownTextView.setText("");
            if (countDownCountainer != null)
                countDownCountainer.setVisibility(View.GONE);

        }
    };

    boolean isClipPlaying;
    //******* Observe videoView Playback
    protected IVideoViewActionListener getVideoViewActionListerner(){
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
            player.setPlayWhenReady(true);
    }

    @Override
    public void onVideoStart() {
        if (isClipPlaying)
            player.setPlayWhenReady(false);
    }

    @Override
    public void onVideoResume() {
        if (isClipPlaying)
            player.setPlayWhenReady(false);

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
        if (player != null)
            return (int)player.getCurrentPosition();
        else
            return 0;
    }

    public void setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority priority){
        if (aspectRatioFrame != null)
            aspectRatioFrame.setAspectRatioPriority(priority);
        aspectFramePriority = priority;
    }
    boolean shouldResumePlayback = false;

    @Override
    public void onPause(){
        super.onPause();
        shouldResumePlayback = playerControl.isPlaying();
        player.setPlayWhenReady(false);
        previousPlaybackTime = (int)player.getCurrentPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoView != null && mediaController != null) {
            videoView.setVisibility(View.VISIBLE);
            mediaController.setAnchorView(videoView);
            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (mediaController.isShowing()) {
                            mediaController.hide();
                            //debugRootView.setVisibility(View.GONE);
                        } else {
                            mediaController.show();
                        }
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        view.performClick();
                    }
                    return true;
                }
            });
        }
        if (bSetOnResume){
            bSetOnResume = false;
            setAudioVisualItem(selectedAVItem);
        }
        if (playerControl != null) {
            if (shouldResumePlayback) {
                playerControl.start();
            } else {
                playerControl.pause();
            }
            shouldResumePlayback = false;
        }
    }

    @Override
    public void onDestroyView(){
        if (videoView != null){
            player.stop();
            videoView.setPlayer(null);
        }
        videoView = null;

        /*videoView.stopPlayback();
        videoView.setVideoURI(null);
        videoView.setMediaController(null);*/
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
        if (ecVideoViewListener != null)
            ecVideoViewListener.stopRepeatingTask();
        if (avItem != null) {
            selectedAVItem = avItem;
            if (countDownCountainer != null)
                countDownCountainer.setVisibility(View.INVISIBLE);

            if (/*selectedECNameTextView != null && */videoView != null) {
                //selectedECNameTextView.setText(avItem.getTitle());
                //if (descriptionTextView != null) {
                //    descriptionTextView.setText(avItem.getSummary());
                //}
                MediaSource mediaSource = buildMediaSource(Uri.parse(avItem.getVideoUrl()));
                //new HlsMediaSource(Uri.parse(mainStyle.getBackgroundVideoUrl()), mediaDataSourceFactory, new Handler(),null);
                //buildMediaSource(nonDRMPlaybackContent.contentUri, nonDRMPlaybackContent.contentType, EXTENSION_EXTRA);
                player.prepare(mediaSource, true, true);
                if (loadingView != null)
                    loadingView.setVisibility(View.VISIBLE);
                if (!shouldAutoPlay) {
                    if (previewFrame != null) {
                        previewFrame.setVisibility(View.VISIBLE);
                        previewPlayBtn.setVisibility(View.GONE);
                    }
                    if(playerControl.isPlaying())
                        player.setPlayWhenReady(false);
                    if (!StringHelper.isEmpty(selectedAVItem.getPosterImgUrl()) && getActivity() != null) {

                        NextGenGlide.load(getActivity(), selectedAVItem.getPreviewImageUrl()).fitCenter().into(previewImageView);
                        //Picasso.with(getActivity()).load(selectedAVItem.getPreviewImageUrl()).fit().into(previewImageView);

                    }
                }else{
                    if (previewFrame != null) {
                        previewFrame.setVisibility(View.GONE);
                        previewPlayBtn.setVisibility(View.GONE);
                    }
                    player.setPlayWhenReady(true);
                }
                //videoView.setVideoURI(Uri.parse(avItem.getVideoUrl()));
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
        shouldResumePlayback = shouldAutoPlay;
    }

    public void stopPlayback(){
        playerControl.pause();
    }

    public MediaController.MediaPlayerControl getPlayerControl(){
        return playerControl;
    }

    final MediaController.MediaPlayerControl playerControl = new MediaController.MediaPlayerControl() {
        @Override
        public void start() {
            if (player != null)
                player.setPlayWhenReady(true);
            getVideoViewActionListerner().onVideoStart();

        }

        @Override
        public void pause() {
            if (player != null)
                player.setPlayWhenReady(false);
            getVideoViewActionListerner().onVideoPause();

        }

        @Override
        public int getDuration() {
            if (player != null)
                return (int)player.getDuration();
            return 0;
        }

        @Override
        public int getCurrentPosition() {
            if (player != null)
                return (int)player.getCurrentPosition();
            return 0;
        }

        @Override
        public void seekTo(int pos) {
            if (player != null)
                player.seekTo(pos);
        }

        @Override
        public boolean isPlaying() {
            if (player != null)
                return player.getPlaybackState() == ExoPlayer.STATE_READY && player.getPlayWhenReady();
            return false;
        }

        @Override
        public int getBufferPercentage() {
            if (player != null)
                return player.getBufferedPercentage();
            return 0;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return false;
        }

        @Override
        public boolean canSeekForward() {
            return false;
        }

        @Override
        public int getAudioSessionId() {
            return 0;
        }
    };

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        DefaultBandwidthMeter bandwidthMeter = useBandwidthMeter ? BANDWIDTH_METER : null;
        return new DefaultDataSourceFactory(getActivity(), bandwidthMeter,
                buildHttpDataSourceFactory(useBandwidthMeter));

    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        String userAgent = Util.getUserAgent(getActivity(), "NextGenExoPlayer");
        DefaultBandwidthMeter bandwidthMeter = useBandwidthMeter ? BANDWIDTH_METER : null;
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    private MediaSource buildMediaSource(Uri uri) {
        int type = Util.inferContentType(uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), new Handler(), null);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), new Handler(), null);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, new Handler(), null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        new Handler(), null);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    public void onFullScreenChange(boolean bFullscreen){
        super.onFullScreenChange(shouldShowFullScreenBtn);
        if (bFullscreen)
            getView().setBackgroundColor(getActivity().getResources().getColor(android.R.color.black));
        else
            getView().setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
    }

}
