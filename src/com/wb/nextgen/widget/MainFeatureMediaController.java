package com.wb.nextgen.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gzcheng on 3/28/16.
 */
public class MainFeatureMediaController extends  NextGenMediaController {
    public MainFeatureMediaController(Context context) {
        super(context);
    }

    public void hideShowControls(boolean bShow){
        super.hideShowControls(bShow);
        if (mContext instanceof Activity) {
            if (bShow) {
                ((Activity)mContext).getActionBar().show();
            } else {
                ((Activity)mContext).getActionBar().hide();
            }
        }
    }
}
