package com.wb.nextgen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;


import com.wb.nextgen.fragment.NextGenActorListFragment;
import com.wb.nextgen.fragment.NextGenExtraMainTableFragment;

import com.wb.nextgen.interfaces.NextGenFragmentTransactionInterface;
import com.wb.nextgen.interfaces.SensitiveFragmentInterface;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;
import com.wb.nextgen.util.PicassoTrustAll;

/**
 * Created by gzcheng on 1/7/16.
 */
public class NextGenExtraActivity extends FragmentActivity implements NextGenFragmentTransactionInterface{
    // wrapper of ProfileViewFragment

    //protected StickyGridHeadersGridView nextGenExtraLeftMenu;
    protected StickyGridHeadersGridView nextGenExtraRightList;

    protected TextView extraTitleTextView;
    protected ImageView extraLogoImageView;
    protected Button extraBackButton;

    NextGenFragmentTransactionEngine nextGenFragmentTransactionEngine;

    protected LinearLayout leftPanelFrame;
    protected LinearLayout rightMainFrame;
    protected LinearLayout fullFrame;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.next_gen_extra);
        float density = NextGenApplication.getScreenDensity(this);
        int spacing = (int)(10 *density);

        leftPanelFrame = (LinearLayout)findViewById(R.id.next_gen_extra_left_view);
        rightMainFrame = (LinearLayout)findViewById(R.id.next_gen_extra_right_view);

        nextGenFragmentTransactionEngine = new NextGenFragmentTransactionEngine(this);

        transitLeftFragment(new NextGenActorListFragment());
        transitRightFragment(new NextGenExtraMainTableFragment());

        extraBackButton = (Button) findViewById(R.id.next_gen_extra_header_left_button);
        extraBackButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                NextGenExtraActivity.this.finish();
            }
        });

        extraLogoImageView = (ImageView) findViewById(R.id.next_gen_extra_header_center_logo);

        extraTitleTextView = (TextView) findViewById(R.id.next_gen_extra_header_right_title);
        /*ImageView bg = (ImageView)view.findViewById(R.id.next_gen_startup_layout);
        if (bg != null){
            PicassoTrustAll.loadImageIntoView(FlixsterApplication.getContext(), "http://www.manofsteel.com/img/about/full_bg.jpg", bg);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (extraLogoImageView != null)
            PicassoTrustAll.loadImageIntoView(NextGenApplication.getContext(), "http://img07.deviantart.net/b14a/i/2013/072/2/7/man_of_steel__2013____superman_symbol_logo_by_gbmpersonal-d5xz08y.jpg", extraLogoImageView);
    }

    //*************** NextGenFragmentTransactionInterface ***************
    @Override
    public void transitRightFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(R.id.next_gen_extra_right_view, nextFragment);
    }

    @Override
    public void transitLeftFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(R.id.next_gen_extra_left_view, nextFragment);
    }

    @Override
    public void transitMainFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(R.id.next_gen_extra_main_frame, nextFragment);
    }

/*
    public void transitLeftPanelFragment(Fragment nextFragment){
        Fragment leftFragment = nextFragment;
        FragmentTransaction ftLeft = getSupportFragmentManager().beginTransaction();
        ftLeft.add(leftPanelFrame.getId(), leftFragment).commit();
    }


    public void transitRightMainFragment(Fragment nextFragment){
        Fragment rightFragment = nextFragment;
        FragmentTransaction ftRight = getSupportFragmentManager().beginTransaction();
        //ftRight.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
        ftRight.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            ftRight.replace(rightMainFrame.getId(), rightFragment, rightFragment.getClass().toString());
            ftRight.addToBackStack(rightFragment.getClass().toString());
            ftRight.commit();
            //rightFrameStack.add(rightFragment.getId());

        }else{
            //int currentFragmentId = rightFrameStack.peek();
            //int currentFragmentId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getId();
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(rightFragment.getClass().toString());
            if (currentFragment != null && currentFragment.getClass().equals(nextFragment.getClass())){
                //Fragment fragment = getSupportFragmentManager().findFragmentById(currentFragmentId);
                if (currentFragment instanceof NextGenActorDetailFragment && nextFragment instanceof NextGenActorDetailFragment){
                    ((NextGenActorDetailFragment) currentFragment).reloadDetail(((NextGenActorDetailFragment)nextFragment).getDetailObject());
                }
            }else{
                ftRight.replace(rightMainFrame.getId(), rightFragment, rightFragment.getClass().toString());
                ftRight.addToBackStack(rightFragment.getClass().toString());
                ftRight.commit();
                //rightFrameStack.add(rightFragment.getId());

            }
        }

    }*/

    //final Stack<Integer> rightFrameStack = new Stack<Integer>();

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

}
