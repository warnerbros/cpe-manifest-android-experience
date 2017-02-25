package com.wb.nextgenlibrary.interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by gzcheng on 2/24/16.
 */
public interface NGEFragmentTransactionInterface {
    void transitRightFragment(Fragment nextFragment);

    void transitLeftFragment(Fragment nextFragment);

    void transitMainFragment(Fragment nextFragment);

    void resetUI(boolean bIsRoot);
}
