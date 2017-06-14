package com.wb.nextgenlibrary.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.interfaces.ContentViewFullscreenRequestInterface;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.StringHelper;

/**
 * Created by gzcheng on 3/9/16.
 */
public abstract class AbstractNGEActivity extends NGEHideStatusBarActivity implements ContentViewFullscreenRequestInterface, CastStateListener {

	public abstract String getBackgroundImgUri();
    public abstract String getLeftButtonText();
    public abstract String getRightTitleImageUri();
    public abstract String getRightTitleText();
    public int getLeftButtonLogoId(){
        return 0;
    }

    private Button actionBarLeftButton;
    protected ImageView backgroundImageView;
    protected LinearLayout topUnderlayActionbarSpacer;
    protected TextView actionBarRightTextView;
    private int actionBarHeight=0;
    ImageView centerBanner, rightLogo;



    protected RemoteMediaClient remoteMediaClient;
    protected RemoteMediaClient.Listener castListener = null;
    protected CastSession mCastSession;
    protected SessionManager mSessionManager;
    protected final SessionManagerListener mSessionManagerListener = new SessionManagerListenerImpl();

    public void onCastStateChanged(int i){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);

		// actionbar height issue: http://stackoverflow.com/questions/26449195/new-theme-appcompat-actionbar-height
        final ActionBar actionBar = getSupportActionBar();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarCustomView = inflator.inflate(R.layout.action_bar_custom_view, null);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(null);
        actionBar.setTitle("");

        actionBarLeftButton = (Button) actionBarCustomView.findViewById(R.id.action_bar_left_button);
        actionBarRightTextView = (TextView) actionBarCustomView.findViewById(R.id.action_bar_right_text);
        centerBanner = (ImageView) actionBarCustomView.findViewById(R.id.action_bar_center_banner);
        rightLogo = (ImageView) actionBarCustomView.findViewById(R.id.action_bar_right_logo);
        actionBar.setCustomView(actionBarCustomView);

		// Remove margins from Action Bar -- Check with Grant to see if this is necessary
		// http://stackoverflow.com/questions/27354812/android-remove-left-margin-from-actionbars-custom-layout
		//Toolbar toolbarParent = (Toolbar) actionBarCustomView.getParent();
		//toolbarParent.setPadding(0, 0, 0, 0);
		//toolbarParent.setContentInsetsAbsolute(0, 0);

        if (actionBarLeftButton != null) {
            setBackButtonText(getLeftButtonText());
            setBackButtonLogo(getLeftButtonLogoId());

            actionBarLeftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   onLeftTopActionBarButtonPressed();
                }
            });
        }

        super.onCreate(savedInstanceState);
        try {
            CastContext castContext = CastContext.getSharedInstance(this);
            mSessionManager = castContext.getSessionManager();
            mSessionManager.addCastStateListener(this);
        }catch (Exception ex){}
    }

    @Override
    protected void onPostCreate( Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (centerBanner != null && !StringHelper.isEmpty(getTitleImageUrl())) {
            NextGenGlide.load(this, getTitleImageUrl()).fitCenter().into(centerBanner);
            //PicassoTrustAll.loadImageIntoView(this, getTitleImageUrl(), centerBanner);
        }

        if (rightLogo != null && !StringHelper.isEmpty(getRightTitleImageUri())) {
            NextGenGlide.load(this, getRightTitleImageUri()).fitCenter().into(rightLogo);
            //PicassoTrustAll.loadImageIntoView(this, getRightTitleImageUri(), rightLogo);
            actionBarRightTextView.setVisibility(View.INVISIBLE);
        }else if (!StringHelper.isEmpty(getRightTitleText())){
            actionBarRightTextView.setVisibility(View.VISIBLE);
            actionBarRightTextView.setText(getRightTitleText().toUpperCase());
        }

    }

    @Override
    public void setContentView(int layoutResID) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setTag("wrapper_layout");
        wrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        wrapper.setOrientation(LinearLayout.VERTICAL);
        View v = getLayoutInflater().inflate(layoutResID, wrapper);

        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT){
            layoutParams.height = ViewGroup.LayoutParams.FILL_PARENT;
            v.setLayoutParams(layoutParams);
        }
        //wrapper.addView(v, 0);
        super.setContentView(wrapper);
    }

    @Override
    protected void onStart(){
        super.onStart();

        //int spacerPost = 0;

        if (backgroundImageView == null){
            backgroundImageView = new ImageView(this);
            backgroundImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            backgroundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewGroup contentView = (ViewGroup)this.getWindow().getDecorView().findViewById(android.R.id.content);
            if (contentView != null){
                contentView.addView(backgroundImageView, 0);
            }

            if (!StringHelper.isEmpty(getBackgroundImgUri()))
                loadBGImage();
        }


        if (topUnderlayActionbarSpacer == null && shouldUseActionBarSpacer()){
            topUnderlayActionbarSpacer = new LinearLayout(this);
            //int actionBarHeight = 0;

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, actionBarHeight);
            //layoutParams.height = getActionBar().getHeight();
            topUnderlayActionbarSpacer.setLayoutParams(layoutParams);
            LinearLayout layout = (LinearLayout)getWindow().getDecorView().findViewWithTag("wrapper_layout");
            if (layout != null){
                layout.addView(topUnderlayActionbarSpacer, 0);
            }
            //topUnderlayActionbarSpacer.setVisibility(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (mSessionManager != null) {
            mCastSession = mSessionManager.getCurrentCastSession();
            mSessionManager.addSessionManagerListener(mSessionManagerListener);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mSessionManager != null) {
            mSessionManager.removeSessionManagerListener(mSessionManagerListener);
            mCastSession = null;
        }
    }

    @Override
    public void onDestroy() {
        if (castListener != null)
            remoteMediaClient.removeListener(castListener);

        if (mSessionManager != null) {
            mSessionManager.removeCastStateListener(this);
        }
        super.onDestroy();
    }

    protected void loadBGImage(){
        backgroundImageView.setImageDrawable(null);
        if (!StringHelper.isEmpty(getBackgroundImgUri())){
            NextGenGlide.load(this, getBackgroundImgUri()).centerCrop().into(backgroundImageView);
        }
    }

    protected void setBackButtonText(String backText){
        if (actionBarLeftButton != null)
            actionBarLeftButton.setText(backText);
    }

    private int currentBackButtonLogoId = 0;
    protected void setBackButtonLogo(int logoId){
        if (currentBackButtonLogoId == logoId)
            return;

        if (actionBarLeftButton != null && logoId != 0){
            Drawable icon = getResources().getDrawable(logoId);
            if (icon instanceof BitmapDrawable) {
                Bitmap source = ((BitmapDrawable) icon).getBitmap();
                int targetHeight = actionBarHeight / 5;                                     // resize the logo to 1/5 of the height of aciton bar
                int targetWidth = source.getWidth() * targetHeight /source.getHeight();     // calculate the width according to its aspect ratio

                Bitmap resized = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                actionBarLeftButton.setCompoundDrawablesWithIntrinsicBounds( new BitmapDrawable(getResources(), resized), null, null, null );

            }else
                actionBarLeftButton.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
        }else
            actionBarLeftButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        currentBackButtonLogoId = logoId;
    }

    public void onRequestToggleFullscreen(){
        if (topUnderlayActionbarSpacer != null) {
            switchFullScreen(topUnderlayActionbarSpacer.getVisibility() != View.GONE);
        }
    }

    public void switchFullScreen(boolean bFullScreen){
        if (topUnderlayActionbarSpacer != null) {
            if (bFullScreen) {
                topUnderlayActionbarSpacer.setVisibility(View.GONE);
            } else
                topUnderlayActionbarSpacer.setVisibility(View.VISIBLE);
        }
    }

    protected void onLeftTopActionBarButtonPressed(){
        //NGEAnalyticData.reportEvent(this, null, "Back Button", NGEAnalyticData.AnalyticAction.ACTION_CLICK, null);
        onBackPressed();
    }

    protected boolean shouldUseActionBarSpacer(){
        return true;
    }

    String getTitleImageUrl(){
        return NextGenExperience.getMovieMetaData().getTitletreatmentImageUrl();
        //return NextGenExperience.getMovieMetaData().getStyle().getTitleImageURL(NextGenStyle.NextGenAppearanceType.InMovie);
    }

    public boolean isCasting(){
        if (mSessionManager != null)
            mCastSession = mSessionManager.getCurrentCastSession();
        return mSessionManager != null && mCastSession != null && mCastSession.isConnected();
    }

    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarted(Session session, String sessionId) {

        }
        @Override
        public void onSessionResumed(Session session, boolean wasSuspended) {

        }
        @Override
        public void onSessionEnded(Session session, int error) {

        }

        public void onSessionResuming(Session var1, String str){}
        public void onSessionStarting(Session var1){}

        public void onSessionStartFailed(Session var1, int var2){}

        public void onSessionEnding(Session var1){}

        public void onSessionResumeFailed(Session var1, int var2){}

        public void onSessionSuspended(Session var1, int var2){}
    }
}
