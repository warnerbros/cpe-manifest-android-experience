package com.wb.nextgenlibrary.activity.phone;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.AbstractNGEActivity;
import com.wb.nextgenlibrary.fragment.ActorDetailFragment;
import com.wb.nextgenlibrary.fragment.ActorListFragment;
import com.wb.nextgenlibrary.interfaces.NGEFragmentTransactionInterface;
import com.wb.nextgenlibrary.util.utils.StringHelper;

/**
 * Created by stomata on 8/25/16.
 */
public class NGEActorsActivity_Phone extends AbstractNGEActivity implements NGEFragmentTransactionInterface {

    public static String TALENT_LIST_MODE = "TALENT_LIST_MODE";
    ActorDetailFragment actorDetailFragment;
    ActorListFragment actorListFragment;
    public static String activeTitle = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actor_activity_phone);
        String talentListMode = getIntent().getStringExtra(TALENT_LIST_MODE);
        actorListFragment = (ActorListFragment)getSupportFragmentManager().findFragmentById(R.id.actor_list_fragment);
        activeTitle = getResources().getString(R.string.actors);
        if (!StringHelper.isEmpty(talentListMode)){
            activeTitle = talentListMode;
            actorListFragment.setForcedMode(activeTitle);
        }
        actorDetailFragment = (ActorDetailFragment)getSupportFragmentManager().findFragmentById(R.id.actor_detail_fragment);
        if (actorListFragment != null && actorDetailFragment != null) {
            actorDetailFragment.reloadDetail(actorListFragment.getActorInfos().get(0));
        }
    }

    @Override
    public int getLeftButtonLogoId(){
        return R.drawable.back_logo;
    }

    @Override
    public String getBackgroundImgUri(){
        if (NextGenExperience.getCPEData().getExtraExperience().style != null)
            return NextGenExperience.getCPEData().getExtraExperience().style.getBackground().getImage().url;
        else
            return null;
    }

    @Override
    public String getLeftButtonText(){
        return getResources().getString(R.string.back_button_text);
    }
    public String getRightTitleText(){
        return activeTitle;
    }

    @Override
    public String getRightTitleImageUri(){
        return null;
    }

    @Override
    public void transitRightFragment(Fragment nextFragment){
        if (nextFragment instanceof ActorDetailFragment){
            actorDetailFragment.reloadDetail(((ActorDetailFragment)nextFragment).getDetailObject());
        }
    }

    @Override
    public void transitLeftFragment(Fragment nextFragment){}

    @Override
    public void transitMainFragment(Fragment nextFragment){}

    @Override
    public void resetUI(boolean bIsRoot){}

}
