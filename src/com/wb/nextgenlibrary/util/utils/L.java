package com.wb.nextgenlibrary.util.utils;


import android.view.View;
import android.view.ViewGroup;

/**
 * Logger helper
 */
public class L {
    public static void view(View v) {
        NextGenLogger.i(F.TAG, v + " isClickable: " + v.isClickable());
        NextGenLogger.i(F.TAG, v + " isFocusable: " + v.isFocusable());
        NextGenLogger.i(F.TAG, v + " isFocused: " + v.isFocused());
    }

    public static void viewGroup(ViewGroup vg) {
        view(vg);
        NextGenLogger.i(F.TAG, vg + " hasFocusable: " + vg.hasFocusable());
        NextGenLogger.i(F.TAG, vg + " hasFocus: " + vg.hasFocus());
    }
}
