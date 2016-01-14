package com.wb.nextgen;

import android.content.res.Configuration;
import android.view.View;

import com.flixster.android.captioning.CaptionedPlayer;
import com.wb.nextgen.R;

import com.wb.nextgen.util.TabletUtils;

/**
 * Created by gzcheng on 1/5/16.
 */
public class NextGenPlayer extends CaptionedPlayer {
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideShowNextGenView();
    }

    public int getLayoutViewId(){
        return R.layout.next_gen_videoview;
    }

    private void hideShowNextGenView(){
        if (TabletUtils.isTablet()) {
            View nextGenView = findViewById(R.id.nextgenview);
            if (nextGenView == null)
                return;
            switch (this.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    nextGenView.setVisibility(View.VISIBLE);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    nextGenView.setVisibility(View.GONE);
            }
        }
    }

    public void onResume() {
        super.onResume();
        hideShowNextGenView();
    }

    @Override
    protected void onDestroy() {
        //ContentLocker content = FlixsterApplication.getCurrentPlayableContent();
        super.onDestroy();
        //FlixsterApplication.setCurrentPlayableContent(content);

    }
}
