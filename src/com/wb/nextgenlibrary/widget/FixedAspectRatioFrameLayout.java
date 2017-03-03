package com.wb.nextgenlibrary.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.NGEHideStatusBarActivity;

/**
 * Created by gzcheng on 3/10/16.
 */
public class FixedAspectRatioFrameLayout extends FrameLayout
{
    private int mAspectRatioWidth;
    private int mAspectRatioHeight;
    private Priority priority;
    private int mOrientationFlag = 3;
    private ViewGroup.LayoutParams mDefaultLatouParams;
    private boolean bAllowLargerThanParent = false;

    private final static int PORTRAIT_ONLY = 2;
    private final static int LANDSCAPE_ONLY = 1;

    public static enum Priority{
        WIDTH_PRIORITY(1), HEIGHT_PRIORITY(2);
        final int intValue;
        Priority(int intValue){
            this.intValue = intValue;
        }

        static Priority valueFromInt(int value){
            for(Priority p : Priority.values()){
                if (p.intValue == value)
                    return p;
            }
            return WIDTH_PRIORITY;
        }
    }

    public FixedAspectRatioFrameLayout(Context context)
    {
        super(context);
    }

    public FixedAspectRatioFrameLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init(context, attrs);
    }

    public FixedAspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedAspectRatioFrameLayout);

        mAspectRatioWidth = a.getInt(R.styleable.FixedAspectRatioFrameLayout_aspectRatioWidth, NextGenExperience.getScreenWidth(context));
        mAspectRatioHeight = a.getInt(R.styleable.FixedAspectRatioFrameLayout_aspectRatioHeight, NextGenExperience.getScreenHeight(context));
        priority = Priority.valueFromInt(a.getInt(R.styleable.FixedAspectRatioFrameLayout_priority, Priority.WIDTH_PRIORITY.intValue));
        mOrientationFlag = a.getInt(R.styleable.FixedAspectRatioFrameLayout_whenOrientation, 3);
        bAllowLargerThanParent = a.getBoolean(R.styleable.FixedAspectRatioFrameLayout_allowLargerThanParent, false);

        mDefaultLatouParams = getLayoutParams();
        a.recycle();
    }
    // **overrides**

    @Override protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int finalWidth, finalHeight;

        int orientation = NGEHideStatusBarActivity.getCurrentScreenOrientation();
        if ((mOrientationFlag == PORTRAIT_ONLY && orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ||
            (mOrientationFlag == LANDSCAPE_ONLY && orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (priority == Priority.WIDTH_PRIORITY && originalWidth == NextGenExperience.getScreenWidth(NextGenExperience.getApplicationContext()) && getContext() != null && getContext().getResources() != null &&
                getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)  {       // this is full screen when width priority

            /*WindowManager wm = (WindowManager) NextGenExperience.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);*/
            int visible = getSystemUiVisibility();
            if ( (visible & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) { // this is full screen
                WindowManager wm = (WindowManager) NextGenExperience.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getRealMetrics(metrics);
                //if (originalHeight == metrics.heightPixels){
                    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(originalHeight, MeasureSpec.EXACTLY));

                //}else
                  //  super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            }else{

                super.onMeasure(
                        widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(originalHeight, MeasureSpec.EXACTLY));
            }
        }else {
            DisplayMetrics metrics = null;
            try {
                WindowManager wm = (WindowManager) NextGenExperience.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                metrics = new DisplayMetrics();
                display.getMetrics(metrics);
            }catch (Exception ex){

            }

            if (priority == Priority.WIDTH_PRIORITY) {
                finalHeight = (int)Math.ceil( (double)(originalWidth * mAspectRatioHeight) / (double)mAspectRatioWidth) - 1;
                finalWidth = originalWidth;
                if (!bAllowLargerThanParent && originalHeight != 0 && finalHeight > originalHeight){
                    finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight;
                    finalHeight = originalHeight;

                }

            } else {
                finalWidth = (int)Math.ceil( (double)(originalHeight * mAspectRatioWidth) / (double)mAspectRatioHeight);
                finalHeight = originalHeight;
                if (!bAllowLargerThanParent && originalWidth != 0 && finalWidth > originalWidth){
                    finalHeight = (int)Math.ceil( (double)(originalWidth * mAspectRatioHeight) / (double)mAspectRatioWidth);
                    finalWidth = originalWidth;

                }
            }

            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
       }
    }

    public void setAspectRatioPriority(Priority priority){
        this.priority = priority;
        invalidate();

    }

    public void setAspectRatio(int widthRatio, int heightRatio){
        mAspectRatioWidth = widthRatio;
        mAspectRatioHeight = heightRatio;
        invalidate();
    }

    public Priority getAspectRatioPriority(){
        return priority;
    }

    public int getAspectRatioWidth(){
        return mAspectRatioWidth;
    }

    public int getAspectRatioHeight(){
        return mAspectRatioHeight;
    }

    public void copyFrameParams(FixedAspectRatioFrameLayout otherFixedFrame){
        if (otherFixedFrame != null){
            mAspectRatioHeight = otherFixedFrame.mAspectRatioHeight;
            mAspectRatioWidth = otherFixedFrame.mAspectRatioWidth;
            setLayoutParams(otherFixedFrame.getLayoutParams());
            //setScaleX(otherFixedFrame.getScaleX());
            //setScaleY(otherFixedFrame.getScaleY());
            //invalidate();
        }
    }
}