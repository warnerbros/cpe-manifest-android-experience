package com.wb.nextgen.util.utils;


import android.view.View;
import android.view.ViewGroup;

/**
 * Logger helper
 */
public class L {
    public static void view(View v) {
        FlixsterLogger.i(F.TAG, v + " isClickable: " + v.isClickable());
        FlixsterLogger.i(F.TAG, v + " isFocusable: " + v.isFocusable());
        FlixsterLogger.i(F.TAG, v + " isFocused: " + v.isFocused());
    }

    public static void viewGroup(ViewGroup vg) {
        view(vg);
        FlixsterLogger.i(F.TAG, vg + " hasFocusable: " + vg.hasFocusable());
        FlixsterLogger.i(F.TAG, vg + " hasFocus: " + vg.hasFocus());
    }
}
