package com.wb.nextgenlibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.wb.nextgenlibrary.interfaces.ContentViewFullscreenRequestInterface;
import com.wb.nextgenlibrary.interfaces.NGEPlayerInterface;
import com.wb.nextgenlibrary.util.TabletUtils;

/**
 * Created by gzcheng on 3/9/16.
 */
public class ECMediaController extends NextGenMediaController {
    //private VideoView videoView;

    int statupSystemUIView = 0;

    ImageButton maxminButton;
    public ECMediaController(Context context, NGEPlayerInterface player) {
        super(context, player, TabletUtils.isTablet());
        statupSystemUIView = mBaseSystemUIView.getSystemUiVisibility();
    }


    @Override
    public void requestToggleFullScreen(){
        if (mContext instanceof ContentViewFullscreenRequestInterface) {
            ((ContentViewFullscreenRequestInterface) mContext).onRequestToggleFullscreen();
        }
    }

    public void onToggledFullScreen(boolean isFullScreen){
        if (!isFullScreen){
            mBaseSystemUIView.setSystemUiVisibility(statupSystemUIView );
        }else{
            mBaseSystemUIView.setSystemUiVisibility(mBaseSystemUIView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && mContext instanceof  Activity ){
            ((Activity)mContext).onBackPressed();
            return false;
        }else
            return super.dispatchKeyEvent(event);
    }
}
