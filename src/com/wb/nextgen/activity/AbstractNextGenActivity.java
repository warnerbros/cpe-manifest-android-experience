package com.wb.nextgen.activity;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.StringHelper;

/**
 * Created by gzcheng on 3/9/16.
 */
public abstract class AbstractNextGenActivity extends FragmentActivity {

    public abstract String getBackgroundImgUri();

    protected ImageView backgroundImageView;

    @Override
    protected void onStart(){
        super.onStart();
        if (backgroundImageView == null && !StringHelper.isEmpty(getBackgroundImgUri())){
            backgroundImageView = new ImageView(this);
            backgroundImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            ViewGroup contentView = (ViewGroup)this.getWindow().getDecorView().findViewById(android.R.id.content);
            if (contentView != null){
                contentView.addView(backgroundImageView, 0);
            }
            loadBGImage();
            //PicassoTrustAll.loadImageIntoView(this, getBackgroundImgUri(), backgroundImageView);
        }
    }

    protected void loadBGImage(){
        Picasso.with(this).load(getBackgroundImgUri()).fit().into(backgroundImageView);

    }
}
