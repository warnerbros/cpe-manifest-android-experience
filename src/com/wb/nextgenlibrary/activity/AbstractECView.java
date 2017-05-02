package com.wb.nextgenlibrary.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.FrameLayout;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.fragment.ECViewLeftListFragment;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.F;

/**
 * Created by gzcheng on 2/25/16.
 */
public abstract class AbstractECView extends AbstractNGEActivity {

    protected MovieMetaData.ExperienceData ecGroupData ;
    protected MovieMetaData.ExperienceData selectedEC = null;
    protected ECViewLeftListFragment listFragment;
    protected FrameLayout leftListFrame;
    protected boolean isContentFullScreen = false;
    private String titleText = null;

    public abstract void onLeftListItemSelected(MovieMetaData.ExperienceData ecContentData);

    public abstract int getContentViewId();

    public MovieMetaData.ExperienceData getECGroupData(){
        return ecGroupData;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String groupId = intent.getStringExtra(F.ID);
        ecGroupData = NextGenExperience.getMovieMetaData().findExperienceDataById(groupId);

        setContentView(getContentViewId());

        leftListFrame = (FrameLayout)findViewById(R.id.ec_list_frame);

        float density = NextGenExperience.getScreenDensity(this);
        int spacing = (int)(10 *density);

        listFragment = (ECViewLeftListFragment) getSupportFragmentManager().findFragmentById(R.id.ec_fragment_list);

    }

    public void onStart() {
        super.onStart();
        if (TabletUtils.isTablet())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        if (listFragment != null && listFragment.getSelectedIndex() <= 0 && ecGroupData.getChildrenContents().size() > 0){
            listFragment.onListItemClick(0, ecGroupData.getChildrenContents().get(0));
            listFragment.scrollToTop();
        }
    }

    public void onStop(){
        super.onStop();

        if (TabletUtils.isTablet())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    public abstract int getListItemViewLayoutId();

    @Override
    public int getLeftButtonLogoId(){
        return R.drawable.back_logo;
    }

    @Override
    public String getBackgroundImgUri(){
        if (NextGenExperience.getMovieMetaData().getExtraExperience().style != null &&
                NextGenExperience.getMovieMetaData().getExtraExperience().style.getBackground() != null &&
                NextGenExperience.getMovieMetaData().getExtraExperience().style.getBackground().getImage() != null)
            return NextGenExperience.getMovieMetaData().getExtraExperience().style.getBackground().getImage().url;
        else
            return null;
    }

    @Override
    public String getLeftButtonText(){
        return getResources().getString(R.string.back_button_text);
    }

    @Override
    public String getRightTitleImageUri(){
        return null;//DemoData.getExtraRightTitleImageUrl();
    }


    @Override
    public void onRequestToggleFullscreen(){
        switchFullScreen(!isContentFullScreen);
        isContentFullScreen = !isContentFullScreen;

    }

    @Override
    public void switchFullScreen(boolean bFullScreen){
        super.switchFullScreen(bFullScreen);
        if (getFullScreenDisappearView() == null /*|| listFragment == null*/)
            return;
        ActionBar bar = getSupportActionBar();
        if (bFullScreen){    // make it full screen
            getFullScreenDisappearView().setVisibility(View.GONE);
            onFullScreenChange(true);
            loadBGImage();
            if (bar != null)
                bar.hide();


        } else {                     // shrink it
            getFullScreenDisappearView().setVisibility(View.VISIBLE);
            onFullScreenChange(false);
            loadBGImage();
            if (bar != null)
                bar.show();

        }

    }

    protected View getFullScreenDisappearView(){
        return leftListFrame;
    }

    abstract void onFullScreenChange(boolean bFullscreen);

    @Override
    public void onBackPressed() {
        if (isContentFullScreen)
            onRequestToggleFullscreen();
        else
            super.onBackPressed();
    }

    public String getRightTitleText(){
        if (titleText == null && getIntent() != null) {
            if (getIntent() != null)
                titleText = getIntent().getStringExtra("title");
            else
                titleText = "";
        }
        return titleText;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!TabletUtils.isTablet()) {
            switch(newConfig.orientation){
                case Configuration.ORIENTATION_LANDSCAPE:
                    switchFullScreen(true);
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    switchFullScreen(false);
                    break;
            }
        }
    }
}
