package com.wb.nextgen.util.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.wb.nextgen.R;
import com.wb.nextgen.fragment.NextGenActorDetailFragment;

/**
 * Created by gzcheng on 2/24/16.
 */
public class NextGenFragmentTransactionEngine {

    FragmentActivity activity;

    public NextGenFragmentTransactionEngine(FragmentActivity activity){
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

    public void transitFragment(int frameId, Fragment nextFragment){
        Fragment rightFragment = nextFragment;
        FragmentTransaction ftRight = activity.getSupportFragmentManager().beginTransaction();
        //ftRight.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
        ftRight.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        if (activity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
            ftRight.replace(frameId, rightFragment, rightFragment.getClass().toString());
            ftRight.addToBackStack(rightFragment.getClass().toString());
            ftRight.commit();
            //rightFrameStack.add(rightFragment.getId());

        }else{
            //int currentFragmentId = rightFrameStack.peek();
            //int currentFragmentId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getId();
            Fragment currentFragment = activity.getSupportFragmentManager().findFragmentByTag(rightFragment.getClass().toString());
            if (currentFragment != null && currentFragment.getClass().equals(nextFragment.getClass())){
                //Fragment fragment = getSupportFragmentManager().findFragmentById(currentFragmentId);
                if (currentFragment instanceof NextGenActorDetailFragment && nextFragment instanceof NextGenActorDetailFragment){
                    ((NextGenActorDetailFragment) currentFragment).reloadDetail(((NextGenActorDetailFragment)nextFragment).getDetailObject());
                }
            }else{
                ftRight.replace(frameId, rightFragment, rightFragment.getClass().toString());
                ftRight.addToBackStack(rightFragment.getClass().toString());
                ftRight.commit();
                //rightFrameStack.add(rightFragment.getId());

            }
        }

    }

    public void onDestroy(){
        activity = null;        // garbage collection
    }
}
