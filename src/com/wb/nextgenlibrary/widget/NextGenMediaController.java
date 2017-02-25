package com.wb.nextgenlibrary.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.interfaces.NGEPlayerInterface;

/**
 * Created by gzcheng on 3/16/16.
 */
public class NextGenMediaController extends CustomMediaController {

    protected Context mContext;
    protected View mBaseSystemUIView;
    NGEPlayerInterface player;

    public NextGenMediaController(Context context, NGEPlayerInterface nextGenPlayer, boolean useFullScreenToggle) {
        super(new ContextThemeWrapper(context, R.style.NextGenMediaControllerStyle), false, useFullScreenToggle);
        player = nextGenPlayer;

        init(context);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onPlayerDestroy(){
        mBaseSystemUIView.setOnSystemUiVisibilityChangeListener(null);
        mContext = null;
        mBaseSystemUIView = null;
    }

    public boolean isFullScreen(){
        return this.getWidth() == NextGenExperience.getScreenWidth(NextGenExperience.getApplicationContext());
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void init(Context context) {
        mContext = context;



        mBaseSystemUIView = ((Activity)context).getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && mBaseSystemUIView != null) {
            mBaseSystemUIView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int currentVis = mBaseSystemUIView.getSystemUiVisibility();

                    if ((currentVis & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
                        if (!isShown()) {
                            show();
                            //hideShowControls(true);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void hide() {
        //if (player.isPlaying()) {
            super.hide();
        //}
    }

    Handler dismissNavHandler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            dismissSoftButtonsIfPossible();
        }
    };

    public void dismissSoftButtonsIfPossible() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
            new Runnable() {

                @Override
                public void run() {
                    if (mBaseSystemUIView == null)
                        return;
                    if (isFullScreen()) {
                        hideShowControls(false);


                    }

                }
            }.run();
        }
    }

    protected void hideShowControls(boolean bShow){
        if (!bShow  ){
          //  mBaseSystemUIView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN); // hide nav bar
        }else{

        }

    }




}
