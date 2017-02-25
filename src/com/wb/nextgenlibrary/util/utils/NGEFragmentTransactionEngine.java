package com.wb.nextgenlibrary.util.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.fragment.ActorDetailFragment;

/**
 * Created by gzcheng on 2/24/16.
 */
public class NGEFragmentTransactionEngine {

    FragmentActivity activity;

    public NGEFragmentTransactionEngine(FragmentActivity activity){
        this.activity = activity;
    }

    /*
    public void transitLeftPanelFragment(Fragment nextFragment){
        Fragment leftFragment = nextFragment;
        FragmentTransaction ftLeft = activitygetSupportFragmentManager().beginTransaction();
        ftLeft.add(leftPanelFrame.getId(), leftFragment).commit();
    }*/
/*
    public void transitRightFragment(Fragment nextFragment){
        transitFragment(rightFrameId, nextFragment);
    }

    public void transitLeftFragment(Fragment nextFragment){
        transitFragment(leftFrameId, nextFragment);
    }

    public void transitMainFragment(Fragment nextFragment){
        transitFragment(mainFrameId, nextFragment);
    }*/

    public void transitFragment(FragmentManager fragmentManager, int frameId, Fragment nextFragment) {
        transitFragment(fragmentManager,frameId,nextFragment,null);
    }

    public void transitFragment(FragmentManager fragmentManager, int frameId, Fragment nextFragment, int animationArray[]){
        if (fragmentManager == null)
            fragmentManager = activity.getSupportFragmentManager();

        Fragment rightFragment = nextFragment;
        FragmentTransaction ftRight = fragmentManager.beginTransaction();
        //ftRight.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
        if (animationArray == null)
            ftRight.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        else
            ftRight.setCustomAnimations(animationArray[0], animationArray[1], animationArray[2], animationArray[3]);

        if (fragmentManager.getBackStackEntryCount() == 0) {
            ftRight.replace(frameId, rightFragment, rightFragment.getClass().toString());
            ftRight.addToBackStack(rightFragment.getClass().toString());
            ftRight.commit();
            //rightFrameStack.add(rightFragment.getId());

        }else{
            //int currentFragmentId = rightFrameStack.peek();
            //int currentFragmentId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getId();
            Fragment currentFragment = fragmentManager.findFragmentByTag(rightFragment.getClass().toString());
            if (currentFragment != null && currentFragment.getClass().equals(nextFragment.getClass())){
                //Fragment fragment = getSupportFragmentManager().findFragmentById(currentFragmentId);
                if (currentFragment instanceof ActorDetailFragment && nextFragment instanceof ActorDetailFragment){
                    ((ActorDetailFragment) currentFragment).reloadDetail(((ActorDetailFragment)nextFragment).getDetailObject());
                }
            }else{
                ftRight.replace(frameId, rightFragment, rightFragment.getClass().toString());
                ftRight.addToBackStack(rightFragment.getClass().toString());
                ftRight.commit();
                //rightFrameStack.add(rightFragment.getId());

            }
        }

    }



    public void replaceFragment(FragmentManager fragmentManager, int frameId, Fragment nextFragment){
        if (fragmentManager == null)
            fragmentManager = activity.getSupportFragmentManager();

        Fragment rightFragment = nextFragment;
        FragmentTransaction ftRight = fragmentManager.beginTransaction();

        ftRight.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);


        ftRight.replace(frameId, rightFragment, rightFragment.getClass().toString());
        ftRight.commit();



    }


    public void onDestroy(){
        activity = null;        // garbage collection
    }
}
