package com.wb.nextgen.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.interfaces.ContentViewFullscreenRequestInterface;

/**
 * Created by gzcheng on 3/16/16.
 */
public class NextGenMediaController extends MediaController {

    Context mContext;
    ImageButton maxminButton;
    protected View mBaseSystemUIView;

    public NextGenMediaController(Context context) {
        super(context);
        init(context);;
    }
    @Override
    protected void onDetachedFromWindow() {
        mContext = null;
        mBaseSystemUIView = null;
        super.onDetachedFromWindow();
    }

    public boolean isFullScreen(){
        return this.getWidth() == NextGenApplication.getScreenWidth(NextGenApplication.getContext());
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
                            hideShowControls(true);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (isFullScreen())
            dismissNavHandler.sendEmptyMessage(0);
        //dismissSoftButtonsIfPossible();
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

                       /* mBaseSystemUIView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE); // hide nav bar
                                */
                    }

                }
            }.run();
        }
    }

    protected void hideShowControls(boolean bShow){
        if (!bShow){
            mBaseSystemUIView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN); // hide nav bar
        }else{

        }

    }
}
