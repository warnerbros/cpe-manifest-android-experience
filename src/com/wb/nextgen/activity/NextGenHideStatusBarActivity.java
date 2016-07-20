package com.wb.nextgen.activity;

import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by gzcheng on 7/20/16.
 */
public class NextGenHideStatusBarActivity extends AppCompatActivity {

    static int DESIRE_VISIBILITY = //View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    static long RESETUI_TIMER = 3000L;

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | DESIRE_VISIBILITY);
        /*getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(final int visibility) {
                if ( (visibility & DESIRE_VISIBILITY) != DESIRE_VISIBILITY){

                    hideBarsHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getWindow().getDecorView().setSystemUiVisibility(visibility | DESIRE_VISIBILITY); // hide nav bar

                                }
                            });
                        }
                    }, RESETUI_TIMER);

                }
            }
        });*/

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | DESIRE_VISIBILITY);
    }

    final Handler hideBarsHandler = new Handler();


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
