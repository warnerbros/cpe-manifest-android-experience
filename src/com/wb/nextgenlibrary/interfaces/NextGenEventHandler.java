package com.wb.nextgenlibrary.interfaces;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by stomata on 8/22/16.
 */
public interface NextGenEventHandler {
    void handleMovieTitleSelection(Activity activity, String movieId);
    boolean isDebugBuild();
    void userEventLog(String screen, String subScreen, String button, String action, String value);
    void handleShareLink(Activity activity, Fragment fragment, String shareUrl);
}
