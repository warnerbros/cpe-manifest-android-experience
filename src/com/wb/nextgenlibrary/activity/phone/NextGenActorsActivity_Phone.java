package com.wb.nextgenlibrary.activity.phone;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.AbstractNextGenActivity;
import com.wb.nextgenlibrary.fragment.NextGenActorDetailFragment;
import com.wb.nextgenlibrary.fragment.NextGenActorListFragment;
import com.wb.nextgenlibrary.interfaces.NextGenFragmentTransactionInterface;

/**
 * Created by stomata on 8/25/16.
 */
public class NextGenActorsActivity_Phone extends AbstractNextGenActivity implements NextGenFragmentTransactionInterface{

    NextGenActorDetailFragment actorDetailFragment;
    NextGenActorListFragment actorListFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actor_activity_phone);
        actorListFragment = (NextGenActorListFragment)getSupportFragmentManager().findFragmentById(R.id.actor_list_fragment);
        actorDetailFragment = (NextGenActorDetailFragment)getSupportFragmentManager().findFragmentById(R.id.actor_detail_fragment);
        if (actorListFragment != null && actorDetailFragment != null) {
            actorDetailFragment.setbEnableActorGallery(true);
            actorDetailFragment.reloadDetail(actorListFragment.getActorInfos().get(0));
        }
    }

    @Override
    public int getLeftButtonLogoId(){
        return R.drawable.back_logo;
    }

    @Override
    public String getBackgroundImgUri(){
        return NextGenExperience.getMovieMetaData().getExtraExperience().style.getBackground().getImage().url;
    }

    @Override
    public String getLeftButtonText(){
        return getResources().getString(R.string.back_button_text);
    }
    public String getRightTitleText(){
        return getResources().getString(R.string.actors);
    }

    @Override
    public String getRightTitleImageUri(){
        return null;
    }

    @Override
    public void transitRightFragment(Fragment nextFragment){
        if (nextFragment instanceof NextGenActorDetailFragment){
            actorDetailFragment.reloadDetail(((NextGenActorDetailFragment)nextFragment).getDetailObject());
        }
    }

    @Override
    public void transitLeftFragment(Fragment nextFragment){}

    @Override
    public void transitMainFragment(Fragment nextFragment){}

    @Override
    public void resetUI(boolean bIsRoot){}

}
