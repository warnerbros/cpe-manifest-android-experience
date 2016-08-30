package com.wb.nextgenlibrary.fragment.phone;

import android.content.pm.ActivityInfo;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.NextGenHideStatusBarActivity;
import com.wb.nextgenlibrary.fragment.NextGenActorListFragment;

/**
 * Created by stomata on 8/25/16.
 */
public class NextGenActorListFragment_Phone extends NextGenActorListFragment {


    protected int getListItemViewId() {
        if (NextGenHideStatusBarActivity.getCurrentScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            return R.layout.next_gen_actors_row;
        else
            return R.layout.next_gen_actors_row_portrait;
    }


    protected int getStartupSelectedIndex(){
        return 0;
    }
}
