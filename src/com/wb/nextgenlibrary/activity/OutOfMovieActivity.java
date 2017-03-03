package com.wb.nextgenlibrary.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.StyleData;
import com.wb.nextgenlibrary.fragment.ActorListFragment;
import com.wb.nextgenlibrary.fragment.ExtraMainTableFragment;
import com.wb.nextgenlibrary.interfaces.NGEFragmentTransactionInterface;
import com.wb.nextgenlibrary.interfaces.SensitiveFragmentInterface;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.NGEFragmentTransactionEngine;

/**
 * Created by gzcheng on 1/7/16.
 */
public class OutOfMovieActivity extends AbstractNGEActivity implements NGEFragmentTransactionInterface {

    NGEFragmentTransactionEngine fragmentTransactionEngine;


    StyleData.ExperienceStyle extraStyle = null;
    private int startupStackCount = 2;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (NextGenExperience.getMovieMetaData() == null)
            finish();

        setContentView(R.layout.next_gen_extra);
        float density = NextGenExperience.getScreenDensity(this);
        int spacing = (int)(10 *density);

        //leftPanelFrame = (LinearLayout)findViewById(R.id.next_gen_extra_left_view);
        //rightMainFrame = (LinearLayout)findViewById(R.id.next_gen_extra_right_view);

        extraStyle = NextGenExperience.getMovieMetaData().getExtraExperience().style;

        fragmentTransactionEngine = new NGEFragmentTransactionEngine(this);
        initFragments();
    }

    protected void initFragments(){
        if (TabletUtils.isTablet()) {
            transitLeftFragment(new ActorListFragment());
            transitRightFragment(new ExtraMainTableFragment());
        }
    }

    public void onStart() {
        super.onStart();
        if (TabletUtils.isTablet())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onStop(){
        super.onStop();

        //if (TabletUtils.isTablet())
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    public void onDestroy(){
        super.onDestroy();

        if (fragmentTransactionEngine != null)
            fragmentTransactionEngine.onDestroy();
    }

    //*************** NGEFragmentTransactionInterface ***************
    @Override
    public void transitRightFragment(Fragment nextFragment){
        fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_extra_right_view, nextFragment);

    }

    @Override
    public void transitLeftFragment(Fragment nextFragment){
        try {
            fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_extra_left_view, nextFragment);
        } catch (Exception ex){}
    }

    @Override
    public void transitMainFragment(Fragment nextFragment){
        //NGEFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.next_gen_extra_main_frame, nextFragment);
    }

    @Override
    public void resetUI(boolean bIsRoot){
        if (bIsRoot){
            setBackButtonLogo(R.drawable.home_logo);
            setBackButtonText(getResources().getString(R.string.home_button_text) );
        }else{
            setBackButtonLogo(R.drawable.back_logo);
            setBackButtonText(getResources().getString(R.string.back_button_text) );
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Fragment leftFragment = getSupportFragmentManager().findFragmentById(R.id.next_gen_extra_left_view);
        Fragment rightFragment = getSupportFragmentManager().findFragmentById(R.id.next_gen_extra_right_view);

        if (leftFragment != null && leftFragment instanceof SensitiveFragmentInterface){
            ((SensitiveFragmentInterface)leftFragment).notifyCurrentSensitiveFragment(rightFragment);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1 )
            finish();


    }

    @Override
    public int getLeftButtonLogoId(){
        return R.drawable.home_logo;
    }

    @Override
    public String getBackgroundImgUri(){
        if (extraStyle != null && extraStyle.getBackground() != null && extraStyle.getBackground().getImage() != null)
            return extraStyle.getBackground().getImage().url;
        else
            return "";
        //return NextGenExperience.getMovieMetaData().getStyle().getBackgroundImageURL(NextGenStyle.NextGenAppearanceType.OutOfMovie);
    }

    @Override
    public String getLeftButtonText(){
        return getResources().getString(R.string.home_button_text);
    }

    @Override
    public String getRightTitleImageUri(){
        /*if (extraStyle != null) {
            StyleData.NodeStyleData nodeStyleData = extraStyle.getNodeStyleData(NGEHideStatusBarActivity.getCurrentScreenOrientation());
            MovieMetaData.PictureImageData titleImageData = nodeStyleData.theme.getImageData();
            return ;
        }else
            return "";*/
        return "";
    }

    @Override
    public String getRightTitleText(){
        return getResources().getString(R.string.extras);
    }

}
