package com.wb.nextgen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.wb.nextgen.R;

import com.wb.nextgen.util.TabletUtils;

/**
 * Created by gzcheng on 1/7/16.
 */
public class NextGenActivity extends FragmentActivity {
    // wrapper of ProfileViewFragment


    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.next_gen_view);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideShowNextGenView();
    }

    private void hideShowNextGenView(){
        if (TabletUtils.isTablet()) {
            View nextGenView = findViewById(R.id.nextgen_portrait_bottom);
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
}
