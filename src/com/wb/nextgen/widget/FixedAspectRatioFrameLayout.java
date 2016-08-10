package com.wb.nextgen.widget;

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

import com.wb.nextgen.NextGenExperience;
import com.wb.nextgen.R;
import com.wb.nextgen.activity.NextGenHideStatusBarActivity;

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

        mDefaultLatouParams = getLayoutParams();
        a.recycle();
    }
    // **overrides**

    @Override protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int finalWidth, finalHeight;

        int orientation = NextGenHideStatusBarActivity.getCurrentScreenOrientation();
        if ((mOrientationFlag == PORTRAIT_ONLY && orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ||
            (mOrientationFlag == LANDSCAPE_ONLY && orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (priority == Priority.WIDTH_PRIORITY && originalWidth == NextGenExperience.getScreenWidth(NextGenExperience.getApplicationContext()) &&
                NextGenExperience.getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)  {       // this is full screen when width priority

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
                finalHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth;
                finalWidth = originalWidth;
                if (originalHeight != 0 && finalHeight > originalHeight){
                    finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight;
                    finalHeight = originalHeight;

                }

            } else {
                finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight;
                finalHeight = originalHeight;
                if (originalWidth != 0 && finalWidth > originalWidth){
                    finalHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth;
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
    }
}