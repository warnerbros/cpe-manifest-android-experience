package com.wb.nextgenlibrary.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastState;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.fragment.AbstractCastMainMovieFragment;
import com.wb.nextgenlibrary.fragment.AbstractNGEMainMovieFragment;
import com.wb.nextgenlibrary.fragment.IMEBottomFragment;
import com.wb.nextgenlibrary.interfaces.NGEFragmentTransactionInterface;
import com.wb.nextgenlibrary.interfaces.NGEPlaybackStatusListener.NextGenPlaybackStatus;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NGEFragmentTransactionEngine;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.videoview.IVideoViewActionListener;
import com.wb.nextgenlibrary.videoview.ObservableVideoView;
import com.wb.nextgenlibrary.widget.CustomMediaController;
import com.wb.nextgenlibrary.widget.FontFitTextView;
import com.wb.nextgenlibrary.widget.MainFeatureMediaController;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by gzcheng on 1/5/16.
 */
public class InMovieExperience extends AbstractNGEActivity implements NGEFragmentTransactionInterface, DialogInterface.OnCancelListener {

    private final static String IS_PLAYER_PLAYING = "IS PLAYING";
    private final static String RESUME_PLAYBACK_TIME = "RESUME_PLAYBACK_TIME";

    protected ObservableVideoView interstitialVideoView;
    protected RelativeLayout containerView;
    protected View skipThisView;
    protected ProgressBar skipThisCounter;
    private View actionbarPlaceHolder;

    private TimerTask imeUpdateTask;

    private Timer imeUpdateTimer;

    private MainFeatureMediaController mediaController;

    //private ProgressDialog mDialog;

    private ProgressBar loadingView;

    NGEFragmentTransactionEngine fragmentTransactionEngine;

    IMEBottomFragment imeBottomFragment;

    private Uri INTERSTITIAL_VIDEO_URI = null;

    private Uri currentUri = null;

    private DRMStatus drmStatus = DRMStatus.NOT_INITIATED;
    private boolean bInterstitialVideoComplete = false;
    //TextView imeText;
    //IMEElementsGridFragment imeGridFragment;
    private long lastTimeCode = -1;
    private NextGenPlaybackStatus lastPlaybackStatus = null;

    AbstractNGEMainMovieFragment mainMovieFragment;

    int ecFragmentsCounter = 0;

    MediaPlayer commentaryAudioPlayer;
    CommentaryOnOffAdapter commentaryOnOffAdapter;
    ListPopupWindow commentaryPopupWindow;

    Menu mOptionsMenu;

    static enum DRMStatus{
        SUCCESS, FAILED, IN_PROGRESS, NOT_INITIATED
    }

    @Override
    public void onCastStateChanged(int i) {
        switch (i){
            case CastState.CONNECTED:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                resetMainMovieFragment();
                startStreamPreparations();
                if (isCommentaryAvailable())
                    actionBarRightTextView.setVisibility(View.GONE);
                break;
            case CastState.CONNECTING:
                break;
            case CastState.NO_DEVICES_AVAILABLE:
                break;
            case CastState.NOT_CONNECTED:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                resetMainMovieFragment();
                startStreamPreparations();
                if (isCommentaryAvailable())
                    actionBarRightTextView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        detectScreenShotService();
        if (NextGenExperience.getMovieMetaData() == null)
            finish();

        INTERSTITIAL_VIDEO_URI = Uri.parse(NextGenExperience.getMovieMetaData().getInterstitialVideoURL());

        fragmentTransactionEngine = new NGEFragmentTransactionEngine(this);
        setContentView(R.layout.next_gen_videoview);
        loadingView = (ProgressBar)findViewById(R.id.next_gen_loading_progress_bar);
        if (loadingView != null){
            loadingView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        }

        actionbarPlaceHolder = findViewById(R.id.next_gen_ime_actionbar_placeholder);

        backgroundImageView = (ImageView)findViewById(R.id.ime_background_image_view);
        if (backgroundImageView != null){
            if (NextGenExperience.getMovieMetaData().getExtraExperience().style != null) {
                String bgImgUrl = NextGenExperience.getMovieMetaData().getExtraExperience().style.getBackground().getImage().url;
                NextGenGlide.load(this, bgImgUrl).into(backgroundImageView);
            }
        }

        containerView = (RelativeLayout)findViewById(R.id.interstitial_container);

        skipThisView = findViewById(R.id.skip_this_layout);
        skipThisCounter = (ProgressBar) findViewById(R.id.skip_this_countdown);

        interstitialVideoView = (ObservableVideoView) findViewById(R.id.interstitial_video_view);

        interstitialVideoView.setOnErrorListener(getOnErrorListener());
        interstitialVideoView.setOnPreparedListener(getOnPreparedListener());
        interstitialVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                updateImeFragment(NextGenPlaybackStatus.STOP, -1L);

                NextGenExperience.getNextGenEventHandler().setInterstitialWatchedForContent(NextGenExperience.getNextgenPlaybackObject());
                bInterstitialVideoComplete = true;
                playMainMovie();
            }
        });
        interstitialVideoView.requestFocus();

        imeBottomFragment = new IMEBottomFragment();


        transitMainFragment(imeBottomFragment);

        resetMainMovieFragment();

        if (isCommentaryAvailable()) {
            actionBarRightTextView.setText(getResources().getString(R.string.nge_commentary));
            actionBarRightTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.nge_commentary), null);
            actionBarRightTextView.setTextColor( getResources().getColor(R.color.gray));

            commentaryOnOffAdapter = new CommentaryOnOffAdapter();

            commentaryPopupWindow = new ListPopupWindow(this);
            commentaryPopupWindow.setModal(false);
            commentaryPopupWindow.setAdapter(commentaryOnOffAdapter);

            commentaryPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    commentaryOnOffAdapter.setSelection(position);
                    if (position == 0){ // OFF
                        switchCommentary(false);
                    } else {
                        switchCommentary(true);
                    }
                    commentaryPopupWindow.dismiss();
                }
            });

            actionBarRightTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //toggleCommentary();
                    if (commentaryPopupWindow.isShowing())
                        commentaryPopupWindow.dismiss();
                    else {
                        commentaryPopupWindow.setAnchorView(actionBarRightTextView);
                        commentaryPopupWindow.setContentWidth(measureContentWidth(commentaryOnOffAdapter));
                        commentaryPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
                        commentaryPopupWindow.show();
                    }

                }
            });

        }

        //resetActivePlaybackFragment();
    }

    private void resetMainMovieFragment(){
        try {
            int resumeTime = -1;
            if (mainMovieFragment != null)
                resumeTime = mainMovieFragment.getCurrentPosition();

            if (isCasting()){

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                mainMovieFragment = NextGenExperience.getCastMovieFragmentClass().newInstance();
                ((AbstractCastMainMovieFragment)mainMovieFragment).setCastControllers(mCastSession, remoteMediaClient, mSessionManager);
            }else
                mainMovieFragment = NextGenExperience.getMainMovieFragmentClass().newInstance();

            mainMovieFragment.setResumeTime(resumeTime);
            mainMovieFragment.setLoadingView(loadingView);
            mainMovieFragment.setOnCompletionLister(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //launch extra
                    Intent intent = new Intent(InMovieExperience.this, OutOfMovieActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            mediaController = new MainFeatureMediaController(this, mainMovieFragment);
            mediaController.setVisibilityChangeListener(new CustomMediaController.MediaControllerVisibilityChangeListener(){
                public void onVisibilityChange(boolean bShow){
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        if (bShow)
                            getSupportActionBar().show();
                        else
                            getSupportActionBar().hide();
                    }
                }
            });
            mainMovieFragment.setCustomMediaController(mediaController);
            mainMovieFragment.setPlaybackObject(NextGenExperience.getNextgenPlaybackObject());
            //mainMovieFragment.setOnCompletionListener
            mainMovieFragment.setNextGenVideoViewListener(new IVideoViewActionListener() {

                @Override
                public void onTimeBarSeekChanged(int currentTime) {
                    updateImeFragment(NextGenPlaybackStatus.SEEK, currentTime);
                }

                @Override
                public void onVideoResume() {
                    updateImeFragment(NextGenPlaybackStatus.RESUME, mainMovieFragment.getCurrentPosition());
                }

                @Override
                public void onVideoStart() {
                    updateImeFragment(NextGenPlaybackStatus.RESUME, mainMovieFragment.getCurrentPosition());
                }

                @Override
                public void onVideoPause() {
                    updateImeFragment(NextGenPlaybackStatus.PAUSE, mainMovieFragment.getCurrentPosition());
                }

                @Override
                public void onVideoComplete(){}
            });
        }catch (InstantiationException ex){

        }catch (IllegalAccessException iex){

        }

        prepareCommentaryTrack();
        playMainMovie();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        if (mOptionsMenu == null) {
            getMenuInflater().inflate(R.menu.next_gen_cast_option_menu, menu);
            mOptionsMenu = menu;
            return super.onCreateOptionsMenu(menu);
        }
        //resetMenuItems();
        return true;
    }

    /**
     * Set up action bar items
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.next_gen_cast_option_menu, menu);

        mOptionsMenu = menu;


        try {
            CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                    menu,
                    R.id.menuChromecast);

        }catch (Exception ex){}
        if (actionBarRightTextView != null && actionBarRightTextView instanceof FontFitTextView)
            ((FontFitTextView)actionBarRightTextView).setNumberOfLinesAllowed(1);

        return true;
    }

    private int measureContentWidth(ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(this);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }

    class CommentaryOnOffAdapter extends BaseAdapter {
        private final String[] items = new String[]{getResources().getString(R.string.off_text), getResources().getString(R.string.director_commentary)};
        int selectedIndex = 0;
        CommentaryOnOffAdapter() {

        }

        public void setSelection(int index){
            selectedIndex = index;
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public String getItem(int index) {
            return items[index];
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int index, View view, ViewGroup arg2) {
            View target = null;

            if (view != null){
                target = view;
            }else{
                LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                target = inflate.inflate(R.layout.nge_commentary_onoff, arg2, false);

            }


            TextView tView = (TextView)target.findViewById(R.id.commentary_text_button);
            tView.setText(getItem(index));

            TextView description = (TextView) target.findViewById(R.id.commentary_description);

            ImageView iView = (ImageView)target.findViewById(R.id.commentary_radio_image);
            /*
            if (index > 0){
                description.setText("Hear from the director in his own words about the decision he made");
                description.setVisibility(View.VISIBLE);
            }else {
                description.setVisibility(View.GONE);
            }*/

            if (index == selectedIndex){
                tView.setTextColor(getResources().getColor(R.color.drawer_yellow));
                iView.setImageDrawable(getResources().getDrawable(R.drawable.commentary_radio_button_selected));
            }else {
                tView.setTextColor(getResources().getColor(R.color.white));
                iView.setImageDrawable(getResources().getDrawable(R.drawable.commentary_radio_button));
            }

            return target;
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        hideShowNextGenView();
    }


    @Override
    public void onBackPressed(){
        if (ecFragmentsCounter == 1)
            finish();
        else {
            getSupportFragmentManager().popBackStackImmediate();
            ecFragmentsCounter = ecFragmentsCounter - 1;
            if (isPausedByIME){
                isPausedByIME = false;
                if (mediaController.isShowing()){
                    mediaController.hide();
                } //tr 9/28
            }
        }
    }


    @Override
    protected boolean shouldUseActionBarSpacer(){
        return false;
    }

    @Override
    String getTitleImageUrl(){
        return NextGenExperience.getMovieMetaData().getTitletreatmentImageUrl();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dialog.cancel();
        //this.finish();
    }

    protected void updateImeFragment(final NextGenPlaybackStatus playbackStatus, final long timecode){
        if (INTERSTITIAL_VIDEO_URI.equals(currentUri))
            return;

        if (lastTimeCode == timecode - mainMovieFragment.getMovieOffsetMilliSecond() && lastPlaybackStatus == playbackStatus)
            return;

        lastPlaybackStatus = playbackStatus;
        lastTimeCode = timecode - mainMovieFragment.getMovieOffsetMilliSecond();

        if (lastTimeCode < 0)
            lastTimeCode = 0;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (InMovieExperience.this == null || InMovieExperience.this.isDestroyed() || InMovieExperience.this.isFinishing() )
                    return;
                if (imeBottomFragment != null)
                    imeBottomFragment.playbackStatusUpdate(playbackStatus, lastTimeCode);

            }
        });

        switch (playbackStatus){
            case PREPARED:
                break;
            case STARTED:
                break;
            case STOP:
                break;
            case TIMESTAMP_UPDATE:
                break;
        }
        if (isCommentaryOn && isCommentaryAvailable()){
            resyncCommentary();
            /* TODO: should handle commentary here.
                resync if the timediference is more than 1/2 second
                and keep track of the player state with it's paused or playing

             */
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
            View nextGenView = findViewById(R.id.next_gen_ime_bottom_view);
            if (nextGenView == null)
                return;

            String label = "interstitial";
            if (bInterstitialVideoComplete){
                double percentageDbl = (double)mainMovieFragment.getCurrentPosition()/ (double)mainMovieFragment.getDuration() * 100.0;
                int percentage = ((int)((percentageDbl + 2.5) / 5) * 5);
                label = Integer.toString(percentage);
            }
            switch (this.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:

                    nextGenView.setVisibility(View.VISIBLE);
                    actionbarPlaceHolder.setVisibility(View.VISIBLE);
                    if (mediaController != null)
                        mediaController.hideShowControls(true);



                    NGEAnalyticData.reportEvent(InMovieExperience.this, null, NGEAnalyticData.AnalyticAction.ACTION_ROTATE_SCREEN_SHOW_EXTRAS, label, null);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    try {
                        Fragment fragment = getSupportFragmentManager().findFragmentById(getMainFrameId());
                        if (!(fragment instanceof IMEBottomFragment) ){
                            onBackPressed();
                        }
                    }catch (Exception ex){}
                    nextGenView.setVisibility(View.GONE);
                    actionbarPlaceHolder.setVisibility(View.GONE);
                    getSupportActionBar().hide();
                    if (mediaController != null)
                        mediaController.hideShowControls(false);


                    NGEAnalyticData.reportEvent(InMovieExperience.this, null, NGEAnalyticData.AnalyticAction.ACTION_ROTATE_SCREEN_HIDE_EXTRAS, label, null);
                    imeBottomFragment.onOrientationChange(this.getResources().getConfiguration().orientation);
            }
    }

    @Override
    boolean shouldHideActionBar(){
        return (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
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

    private class ProgressBarAnimation extends Animation{
        private ProgressBar progressBar;

        public ProgressBarAnimation(ProgressBar progressBar) {
            super();
            this.progressBar = progressBar;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = 0 + (100) * interpolatedTime;
            progressBar.setProgress((int) value);
        }

    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            interstitialVideoView.start();

            skipThisView.setVisibility(View.VISIBLE);
            skipThisView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bInterstitialVideoComplete = true;
                            NextGenExperience.getNextGenEventHandler().setInterstitialSkippedForContent(NextGenExperience.getNextgenPlaybackObject());
                            playMainMovie();
                            NGEAnalyticData.reportEvent(InMovieExperience.this, null, NGEAnalyticData.AnalyticAction.ACTION_SKIP_INTERSTITIAL, null, null);
                        }
                    });
                }
            });
            skipThisCounter.setProgress(0);
            skipThisCounter.setProgress(100);

            ProgressBarAnimation skipProgressBarAnim = new ProgressBarAnimation(skipThisCounter);
            skipProgressBarAnim.setDuration(mp.getDuration());
            skipThisCounter.startAnimation(skipProgressBarAnim);

            return;




        }
    }


    protected MediaPlayer.OnPreparedListener getOnPreparedListener(){
        return new PreparedListener();
    }

    private synchronized void playMainMovie(){
        currentUri = Uri.parse("");

        if (drmStatus == DRMStatus.IN_PROGRESS){    // show loading
            mainMovieFragment.showLoadingView();
            /*mDialog.setCanceledOnTouchOutside(false);
            mDialog.setMessage(getResources().getString(R.string.loading));
            mDialog.show();*/
            return;
        }else if (drmStatus == DRMStatus.FAILED){
            //Show error Message and exit

            finish();
            return;
        } else if (!bInterstitialVideoComplete){
            // wait for interstitial video to be finished
            return;
        }
        if (skipThisView != null) {
            skipThisView.setVisibility(View.GONE);
            skipThisView.setOnClickListener(null);
        }
        interstitialVideoView.stopPlayback();
        interstitialVideoView.setVisibility(View.GONE);
        mainMovieFragment.hideLoadingView();
        //mDialog.hide();

        fragmentTransactionEngine.replaceFragment(getSupportFragmentManager(), R.id.video_view_frame, mainMovieFragment);
        if (imeUpdateTimer == null){
            imeUpdateTimer = new Timer();
        }
        if (imeUpdateTask == null){
            imeUpdateTask = new TimerTask() {
                @Override
                public void run() {
                    if (InMovieExperience.this != null && !InMovieExperience.this.isFinishing() && !InMovieExperience.this.isDestroyed())
                        updateImeFragment(NextGenPlaybackStatus.TIMESTAMP_UPDATE, mainMovieFragment.getCurrentPosition());
                }
            };
            imeUpdateTimer.scheduleAtFixedRate(imeUpdateTask, 0, 1000);
        }


        updateImeFragment(NextGenPlaybackStatus.PREPARED, -1L);
    }

    int resumePlayTime = -1;
    boolean shouldStartAfterResume = true;

    @Override
    public void onPause() {
        shouldStartAfterResume = mainMovieFragment.isPlaying();
        resumePlayTime = mainMovieFragment.getCurrentPosition();
        if (isCommentaryOn && commentaryAudioPlayer != null){
                commentaryAudioPlayer.pause();
        }
        if (!bInterstitialVideoComplete && interstitialVideoView.isPlaying()){
            interstitialVideoView.pause();
        }
        super.onPause();

    }

    public void onResume() {
        if (resumePlayTime != -1){
            mainMovieFragment.setResumeTime(resumePlayTime);
            if (!shouldStartAfterResume && !isCasting())
                mainMovieFragment.pause();
        }
        super.onResume();

        if (currentUri == null || StringHelper.isEmpty(currentUri.toString())) {
            currentUri = INTERSTITIAL_VIDEO_URI;

            drmStatus = DRMStatus.IN_PROGRESS;
            if (NextGenExperience.getNextGenEventHandler().shouldShowInterstitialForContent(NextGenExperience.getNextgenPlaybackObject())) {
                interstitialVideoView.setVisibility(View.VISIBLE);
                interstitialVideoView.setVideoURI(currentUri);
            } else {

                bInterstitialVideoComplete = true;
                playMainMovie();
            }
            startStreamPreparations();


        } else{
            skipThisView.setVisibility(View.GONE);
            skipThisView.setOnClickListener(null);
            currentUri = Uri.parse("");
        }
        if (!bInterstitialVideoComplete && interstitialVideoView.getVisibility() == View.VISIBLE){
            interstitialVideoView.resume();
        }
        hideShowNextGenView();

    }

    private void startStreamPreparations(){
        mainMovieFragment.streamStartPreparations(new ResultListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {

                drmStatus = DRMStatus.SUCCESS;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!InMovieExperience.this.isDestroyed() && !InMovieExperience.this.isFinishing())
                            playMainMovie();
                    }
                });

            }

            @Override
            public <E extends Exception> void onException(E e) {
                drmStatus = DRMStatus.FAILED;
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaController != null){
            mediaController.onPlayerDestroy();
        }
        if (imeUpdateTask != null) {
            imeUpdateTask.cancel();
            imeUpdateTask = null;
        }
        if (imeUpdateTimer != null){
            imeUpdateTimer.cancel();
            imeUpdateTimer = null;
        }
        if (fragmentTransactionEngine != null)
            fragmentTransactionEngine.onDestroy();
        if (commentaryAudioPlayer != null){
            if (isCommentaryOn)
                commentaryAudioPlayer.stop();
            commentaryAudioPlayer.release();
        }


    }

    @Override
    public void resetUI(boolean isRoot){

    }

    public int getMainFrameId(){
        return R.id.next_gen_ime_bottom_view;
    }

    public int getLeftFrameId(){
        return R.id.next_gen_ime_bottom_view;
    }

    public int getRightFrameId(){
        return R.id.next_gen_ime_bottom_view;

    }

    boolean isPausedByIME = false;
    public void pauseMovieForImeECPiece(){
        if (mediaController.isShowing()){
            mediaController.hide();
        }
        mainMovieFragment.pauseForIME();
        isPausedByIME = true;
    } //tr 9/28

    public void resumeMovideForImeECPiece(){
        if (mediaController.isShowing()){
            mediaController.hide();
        }
        mainMovieFragment.resumePlaybackFromIME();
        isPausedByIME = true;
    }


    @Override
    public void transitLeftFragment(Fragment nextFragment){
        fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), getLeftFrameId(), nextFragment);
    }

    @Override
    public void transitRightFragment(Fragment nextFragment){
        fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), getRightFrameId(), nextFragment);
    }

    @Override
    public void transitMainFragment(Fragment nextFragment){
        ecFragmentsCounter = ecFragmentsCounter + 1;
        fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), getMainFrameId(), nextFragment);
    }

    @Override
    public int getLeftButtonLogoId(){
        return R.drawable.home_logo;
    }

    @Override
    public String getBackgroundImgUri(){
        return "";
    }

    @Override
    public String getLeftButtonText(){
        return getResources().getString(R.string.home_button_text);
    }

    public String getRightTitleImageUri(){
        return "";

    }

    @Override
    protected void onLeftTopActionBarButtonPressed(){
        finish();
    }

    @Override
    public String getRightTitleText(){
        return "";
    }

    private boolean isCommentaryAvailable(){
        return !StringHelper.isEmpty(NextGenExperience.getMovieMetaData().getCommentaryTrackURL());
    }

    private boolean isCommentaryOn = false;

    private NGECommentaryPlayersStatusListener commentaryPlayersStatusListener = null;

    private void prepareCommentaryTrack(){
        if (isCommentaryAvailable()) {
            if (mainMovieFragment.canHandleCommentaryAudioTrackSwitching()){
                List<String> commentaries = new ArrayList<>();
                commentaries.add(NextGenExperience.getMovieMetaData().getCommentaryTrackURL());
                mainMovieFragment.setCommentaryTrackUrls(commentaries);
            }else {

                try {
                    commentaryAudioPlayer = MediaPlayer.create(this, Uri.parse(NextGenExperience.getMovieMetaData().getCommentaryTrackURL()));
                    commentaryAudioPlayer.setLooping(false);
                    commentaryPlayersStatusListener = new NGECommentaryPlayersStatusListener();
                    commentaryAudioPlayer.setOnPreparedListener(commentaryPlayersStatusListener);
                    commentaryAudioPlayer.setOnInfoListener(commentaryPlayersStatusListener);
                } catch (Exception ex) {
                    NextGenLogger.e(F.TAG, ex.getMessage());
                }
            }
        }
    }

    private class NGECommentaryPlayersStatusListener implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {
        private NextGenPlaybackStatus playerStatus = NextGenPlaybackStatus.BUFFERING;

        public NextGenPlaybackStatus getPlayerStatus(){
            return playerStatus;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            playerStatus = NextGenPlaybackStatus.READY;
            resyncCommentary();
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            playerStatus = NextGenPlaybackStatus.COMPLETED;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            NextGenLogger.d(F.TAG_COMMENTARY,  "CommentaryPlayer " + InMovieExperience.this.getClass().getSimpleName() + ".onInfo: " + what);

            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START: // @since API Level 9
                    NextGenLogger.d(F.TAG_COMMENTARY, "CommentaryPlayer.onInfo: MEDIA_INFO_BUFFERING_START");
                    playerStatus = NextGenPlaybackStatus.BUFFERING;
                    resyncCommentary();

                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END: // @since API Level 9
                    NextGenLogger.d(F.TAG_COMMENTARY, "CommentaryPlayer.onInfo: MEDIA_INFO_BUFFERING_END");
                    playerStatus = NextGenPlaybackStatus.READY;
                    resyncCommentary();

                    //mDialog.hide();

                    break;

                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    playerStatus = NextGenPlaybackStatus.READY;
                    resyncCommentary();
                    //mDialog.hide();

                    break;

                default:
                    //mDialog.hide();
                    break;

            }
            return true;
        }
    }

    /*
    private void toggleCommentary(){
        if (isCommentaryAvailable() && commentaryAudioPlayer != null){
           if (isCommentaryOn) {            // turning commentary off
               isCommentaryOn = false;
               resyncCommentary();
           }else {                          // turning commentary on
               isCommentaryOn = true;
               resyncCommentary();
               commentaryAudioPlayer.start();
               commentaryAudioPlayer.seekTo(mainMovieFragment.getCurrentPosition() - mainMovieFragment.getMovieOffsetMilliSecond());

           }
        }

        actionBarRightTextView.setTextColor(isCommentaryOn? getResources().getColor(R.color.drawer_yellow) : getResources().getColor(R.color.gray));
    }*/

    private void switchCommentary(boolean bOnOff){
        isCommentaryOn = bOnOff;
        if (isCommentaryAvailable()){
            if (mainMovieFragment.canHandleCommentaryAudioTrackSwitching()){
                mainMovieFragment.setActiveCommentaryTrack(bOnOff ? 0 : -1);
            }else if (commentaryAudioPlayer != null) {
                if (!bOnOff) {            // turning commentary off
                    resyncCommentary();
                } else {                          // turning commentary on
                    resyncCommentary();
                    commentaryAudioPlayer.start();
                    commentaryAudioPlayer.seekTo(mainMovieFragment.getCurrentPosition() - mainMovieFragment.getMovieOffsetMilliSecond());

                }
            }
        }
        if (isCommentaryOn) {
            actionBarRightTextView.setTextColor(getResources().getColor(R.color.drawer_yellow));
            actionBarRightTextView.setText(getResources().getString(R.string.nge_commentary_on));
            actionBarRightTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.nge_commentary_on), null);

        } else {
            actionBarRightTextView.setTextColor(getResources().getColor(R.color.white));
            actionBarRightTextView.setText(getResources().getString(R.string.nge_commentary));
            actionBarRightTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.nge_commentary), null);
        }


    }

    private boolean bPausedForCommentaryBuffering = false;

    private void resyncCommentary(){
         if (isCommentaryAvailable() && !mainMovieFragment.canHandleCommentaryAudioTrackSwitching()){
            if (isCommentaryOn){
                if (commentaryPlayersStatusListener.getPlayerStatus() == NextGenPlaybackStatus.READY && mainMovieFragment.getPlaybackStatus() == NextGenPlaybackStatus.READY){        // both ready
                    int mainMovieTime = mainMovieFragment.getCurrentPosition() - mainMovieFragment.getMovieOffsetMilliSecond();
                    if (mainMovieTime < 0)
                        mainMovieTime = 0;
                    int commentaryTime = commentaryAudioPlayer.getCurrentPosition();
                    int timeDifference = Math.abs(mainMovieTime - commentaryTime);
                    if (timeDifference > 150) {     // when they are out of sync i.e. more than 150 mini seconds apart.
                        commentaryAudioPlayer.start();
                        commentaryAudioPlayer.seekTo(mainMovieTime);
                        final boolean bWasPlaying ;
                        if (mainMovieFragment.isPlaying()) {
                            mainMovieFragment.pause();
                            bWasPlaying = true;
                        }else
                            bWasPlaying = false;
                        commentaryAudioPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                            @Override
                            public void onSeekComplete(MediaPlayer mp) {
                                if (bWasPlaying){
                                    try{
                                        mainMovieFragment.resumePlayback();
                                    }catch (Exception ex){}
                                }
                            }
                        });
                    }

                    if (mainMovieFragment.isPlaying()) {    // if it's playing, check to see if recy
                        commentaryAudioPlayer.start();
                    }else if (bPausedForCommentaryBuffering){       // if it's paused for commentary buffering
                        bPausedForCommentaryBuffering = false;
                        mainMovieFragment.resumePlayback();
                        commentaryAudioPlayer.start();
                    } else if (commentaryAudioPlayer.isPlaying()){                                        // if main movie is paused.弓
                        commentaryAudioPlayer.pause();
                    }
                } else if (commentaryPlayersStatusListener.getPlayerStatus() == NextGenPlaybackStatus.BUFFERING || mainMovieFragment.getPlaybackStatus() == NextGenPlaybackStatus.BUFFERING){     // show loading progress bar and pause and wait for buffering completion
                    if (commentaryPlayersStatusListener.getPlayerStatus() == NextGenPlaybackStatus.READY && commentaryAudioPlayer.isPlaying())
                        commentaryAudioPlayer.pause();

                    if (mainMovieFragment.getPlaybackStatus() == NextGenPlaybackStatus.READY){
                        bPausedForCommentaryBuffering = true;
                        mainMovieFragment.pause();
                    }

                }

                mainMovieFragment.switchMainFeatureAudio(false);    // turn off movie audio track
            } else{                 // switch off commentary
                mainMovieFragment.switchMainFeatureAudio(true);     // turn on movie audio track
                if (commentaryPlayersStatusListener.getPlayerStatus() == NextGenPlaybackStatus.READY && commentaryAudioPlayer.isPlaying())
                    commentaryAudioPlayer.pause();
            }
        }
    }

    private android.os.Handler mHandler = null;

    private HandlerThread mHandlerThread = null;

    public void startHandlerThread(){
        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public void detectScreenShotService() {
        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        final int delay = 3000; //milliseconds
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        mHandler.postDelayed(new Runnable() {
            public void run() {

                List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(200);

                for (ActivityManager.RunningServiceInfo ar : rs) {
                    if (ar.process.equals("com.android.systemui:screenshot")) {
                        Toast.makeText(InMovieExperience.this, "Screenshot captured!!", Toast.LENGTH_LONG).show();
                    }
                }
                mHandler.postDelayed(this, delay);
            }
        }, delay);
    }

    public boolean isPlaying(){
        if (mainMovieFragment != null)
            return mainMovieFragment.isPlaying();
        else
            return false;
    }
}
