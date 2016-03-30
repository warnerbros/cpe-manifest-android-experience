package com.wb.nextgen.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.fragment.ECViewLeftListFragment;
import com.wb.nextgen.util.utils.F;

/**
 * Created by gzcheng on 2/25/16.
 */
public abstract class AbstractECView extends AbstractNextGenActivity {

    protected MovieMetaData.ExperienceData ecGroupData ;
    protected ECViewLeftListFragment listFragment;
    protected TextView selectedECNameTextView;
    protected FrameLayout leftListFrame;
    protected boolean isContentFullScreen = false;

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
        selectedECNameTextView = (TextView)findViewById(R.id.ec_content_name);
    }

    public void onStart() {
        super.onStart();
        if (listFragment != null){
            listFragment.onListItemClick(ecGroupData.getChildrenContents().get(0));
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
        return DemoData.getExtraBackgroundUrl();
    }

    @Override
    public String getLeftButtonText(){
        return getResources().getString(R.string.back_button_text);
    }

    @Override
    public String getRightTitleImageUri(){
        return DemoData.getExtraRightTitleImageUrl();
    }


    @Override
    public void onRequestToggleFullscreen(){
        super.onRequestToggleFullscreen();
        ActionBar bar = getActionBar();
        if (!isContentFullScreen){    // make it full screen
            leftListFrame.setVisibility(View.GONE);
            selectedECNameTextView.setVisibility(View.GONE);
/*
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);*/
            if (bar != null)
                bar.hide();


        } else {                     // shrink it
            leftListFrame.setVisibility(View.VISIBLE);
            selectedECNameTextView.setVisibility(View.VISIBLE);
            if (bar != null)
                bar.show();
/*
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_VISIBLE);*/

        }
        isContentFullScreen = !isContentFullScreen;
    }

    @Override
    public void onBackPressed() {
        if (isContentFullScreen)
            onRequestToggleFullscreen();
        else
            super.onBackPressed();
    }
}
