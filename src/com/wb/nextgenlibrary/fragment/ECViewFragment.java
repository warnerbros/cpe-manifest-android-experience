package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.view.View;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NextGenAnalyticData;
import com.wb.nextgenlibrary.util.utils.StringHelper;

/**
 * Created by gzcheng on 7/26/16.
 */
public abstract class ECViewFragment extends AbstractNextGenFragment{

    private boolean shouldHideMetaData = false;
    protected View contentMetaFrame;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentMetaFrame = view.findViewById(R.id.ec_content_meta_frame);

        if (contentMetaFrame != null){
            contentMetaFrame.setVisibility(shouldHideMetaData ? View.GONE : View.VISIBLE);
        }
    }

    public void setShouldHideMetaData(boolean bHide){
        shouldHideMetaData = bHide;
    }

    public void onFullScreenChange(boolean bFullscreen){
        if (contentMetaFrame != null)
            contentMetaFrame.setVisibility(bFullscreen || shouldHideMetaData? View.GONE : View.VISIBLE);

    }

}
