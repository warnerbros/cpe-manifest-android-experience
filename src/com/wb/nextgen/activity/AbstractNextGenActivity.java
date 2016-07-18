package com.wb.nextgen.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.interfaces.ContentViewFullscreenRequestInterface;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.StringHelper;

/**
 * Created by gzcheng on 3/9/16.
 */
public abstract class AbstractNextGenActivity extends AppCompatActivity implements ContentViewFullscreenRequestInterface {

    public abstract String getBackgroundImgUri();
    public abstract String getLeftButtonText();
    public abstract String getRightTitleImageUri();
    public abstract String getRightTitleText();
    public int getLeftButtonLogoId(){
        return 0;
    }

    private Button actionBarLeftButton;
    protected ImageView backgroundImageView;
    protected LinearLayout topUnderlayActionbarSpacer;
    private TextView actionBarRightTextView;
    private int actionBarHeight=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);

        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        final ActionBar actionBar = getSupportActionBar();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarCustomView = inflator.inflate(R.layout.action_bar_custom_view, null);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(null);
        actionBar.setTitle("");

        actionBarLeftButton = (Button) actionBarCustomView.findViewById(R.id.action_bar_left_button);
        actionBarRightTextView = (TextView) actionBarCustomView.findViewById(R.id.action_bar_right_text);
        ImageView centerBanner = (ImageView) actionBarCustomView.findViewById(R.id.action_bar_center_banner);
        ImageView rightLogo = (ImageView) actionBarCustomView.findViewById(R.id.action_bar_right_logo);
        actionBar.setCustomView(actionBarCustomView);


        if (actionBarLeftButton != null) {
            setBackButtonText(getLeftButtonText());
            setBackButtonLogo(getLeftButtonLogoId());

            actionBarLeftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   onLeftTopActionBarButtonPressed();
                }
            });
        }
        if (centerBanner != null && !StringHelper.isEmpty(DemoData.getMovieLogoUrl()))
            PicassoTrustAll.loadImageIntoView(this, DemoData.getMovieLogoUrl(), centerBanner);

        if (rightLogo != null && !StringHelper.isEmpty(getRightTitleImageUri())) {
            PicassoTrustAll.loadImageIntoView(this, getRightTitleImageUri(), rightLogo);
            actionBarRightTextView.setVisibility(View.INVISIBLE);
        }else if (!StringHelper.isEmpty(getRightTitleText())){
            actionBarRightTextView.setVisibility(View.VISIBLE);
            actionBarRightTextView.setText(getRightTitleText().toUpperCase());
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setTag("wrapper_layout");
        wrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        wrapper.setOrientation(LinearLayout.VERTICAL);
        View v = getLayoutInflater().inflate(layoutResID, wrapper);

        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT){
            layoutParams.height = ViewGroup.LayoutParams.FILL_PARENT;
            v.setLayoutParams(layoutParams);
        }
        //wrapper.addView(v, 0);
        super.setContentView(wrapper);
    }

    @Override
    protected void onStart(){
        super.onStart();

        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_FULLSCREEN);
        //int spacerPost = 0;

        if (backgroundImageView == null){
            backgroundImageView = new ImageView(this);
            backgroundImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ViewGroup contentView = (ViewGroup)this.getWindow().getDecorView().findViewById(android.R.id.content);
            if (contentView != null){
                contentView.addView(backgroundImageView, 0);
            }
        }

        if (!StringHelper.isEmpty(getBackgroundImgUri()))
            loadBGImage();

        if (topUnderlayActionbarSpacer == null && shouldUseActionBarSpacer()){
            topUnderlayActionbarSpacer = new LinearLayout(this);
            //int actionBarHeight = 0;

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, actionBarHeight);
            //layoutParams.height = getActionBar().getHeight();
            topUnderlayActionbarSpacer.setLayoutParams(layoutParams);
            LinearLayout layout = (LinearLayout)getWindow().getDecorView().findViewWithTag("wrapper_layout");
            if (layout != null){
                layout.addView(topUnderlayActionbarSpacer, 0);
            }
            //topUnderlayActionbarSpacer.setVisibility(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? View.GONE : View.VISIBLE);
        }
    }

    protected void loadBGImage(){
        Picasso.with(this).load(getBackgroundImgUri()).fit().into(backgroundImageView);
    }

    protected void setBackButtonText(String backText){
        if (actionBarLeftButton != null)
            actionBarLeftButton.setText(backText);
    }

    private int currentBackButtonLogoId = 0;
    protected void setBackButtonLogo(int logoId){
        if (currentBackButtonLogoId == logoId)
            return;

        if (actionBarLeftButton != null && logoId != 0){
            Drawable icon= getResources().getDrawable( logoId);
            if (icon instanceof BitmapDrawable) {
                Bitmap source = ((BitmapDrawable) icon).getBitmap();
                int targetHeight = actionBarHeight / 3;                                     // resize the logo to 1/3 of the height of aciton bar
                int targetWidth = source.getWidth() * targetHeight /source.getHeight();     // calculate the width according to its aspect ratio

                Bitmap resized = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                actionBarLeftButton.setCompoundDrawablesWithIntrinsicBounds( new BitmapDrawable(getResources(), resized), null, null, null );

            }else
                actionBarLeftButton.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
        }else
            actionBarLeftButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        currentBackButtonLogoId = logoId;
    }

    public void onRequestToggleFullscreen(){
        if (topUnderlayActionbarSpacer != null) {
            if (topUnderlayActionbarSpacer.getVisibility() == View.GONE) {
                topUnderlayActionbarSpacer.setVisibility(View.VISIBLE);
            } else
                topUnderlayActionbarSpacer.setVisibility(View.GONE);
        }
    }

    protected void onLeftTopActionBarButtonPressed(){
        onBackPressed();
    }

    protected boolean shouldUseActionBarSpacer(){
        return true;
    }
}
