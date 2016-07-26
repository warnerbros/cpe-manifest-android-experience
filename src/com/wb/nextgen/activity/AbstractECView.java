package com.wb.nextgen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.FrameLayout;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.NextGenStyle;
import com.wb.nextgen.fragment.ECViewLeftListFragment;
import com.wb.nextgen.util.utils.F;

/**
 * Created by gzcheng on 2/25/16.
 */
public abstract class AbstractECView extends AbstractNextGenActivity {

    protected MovieMetaData.ExperienceData ecGroupData ;
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
        ecGroupData = NextGenApplication.getMovieMetaData().findExperienceDataById(groupId);

        setContentView(getContentViewId());

        leftListFrame = (FrameLayout)findViewById(R.id.ec_list_frame);

        float density = NextGenApplication.getScreenDensity(this);
        int spacing = (int)(10 *density);

        listFragment = (ECViewLeftListFragment) getSupportFragmentManager().findFragmentById(R.id.ec_fragment_list);

    }

    public void onStart() {
        super.onStart();
        if (listFragment != null && listFragment.getSelectedIndex() <= 0){
            listFragment.onListItemClick(0, ecGroupData.getChildrenContents().get(0));
            listFragment.scrollToTop();
        }
    }

    public abstract int getListItemViewLayoutId();

    @Override
    public int getLeftButtonLogoId(){
        return R.drawable.back_logo;
    }

    @Override
    public String getBackgroundImgUri(){
        return NextGenApplication.getMovieMetaData().getStyle().getBackgroundImageURL(NextGenStyle.NextGenAppearanceType.OutOfMovie);
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
        super.onRequestToggleFullscreen();
        if (getFullScreenDisappearView() == null /*|| listFragment == null*/)
            return;
        ActionBar bar = getSupportActionBar();
        if (!isContentFullScreen){    // make it full screen
            getFullScreenDisappearView().setVisibility(View.GONE);
            onFullScreenChange(true);

            if (bar != null)
                bar.hide();


        } else {                     // shrink it
            getFullScreenDisappearView().setVisibility(View.VISIBLE);
            onFullScreenChange(false);
            if (bar != null)
                bar.show();

        }
        isContentFullScreen = !isContentFullScreen;
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
}
