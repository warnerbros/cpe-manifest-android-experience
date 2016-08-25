package com.wb.nextgen.activity.phone;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wb.nextgen.NextGenExperience;
import com.wb.nextgen.R;
import com.wb.nextgen.activity.AbstractECView;
import com.wb.nextgen.activity.AbstractNextGenActivity;
import com.wb.nextgen.data.NextGenStyle;
import com.wb.nextgen.fragment.NextGenActorDetailFragment;
import com.wb.nextgen.fragment.NextGenActorListFragment;
import com.wb.nextgen.interfaces.NextGenFragmentTransactionInterface;

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
        return NextGenExperience.getMovieMetaData().getStyle().getBackgroundImageURL(NextGenStyle.NextGenAppearanceType.OutOfMovie);
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
