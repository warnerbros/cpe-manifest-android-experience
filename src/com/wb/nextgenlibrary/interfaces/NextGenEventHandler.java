package com.wb.nextgenlibrary.interfaces;

import android.app.Activity;

/**
 * Created by stomata on 8/22/16.
 */
public interface NextGenEventHandler {
    void handleMovieTitleSelection(Activity activity, String movieId);
    boolean isDebugBuild();
    void userEventLog(String screen, String subScreen, String button, String action, String value);
}
