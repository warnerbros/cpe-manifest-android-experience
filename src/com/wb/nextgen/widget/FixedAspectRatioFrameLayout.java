package com.wb.nextgen.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wb.nextgen.R;

/**
 * Created by gzcheng on 3/10/16.
 */
public class FixedAspectRatioFrameLayout extends FrameLayout
{
    private int mAspectRatioWidth;
    private int mAspectRatioHeight;
    private Priority priority;

    enum Priority{
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

        mAspectRatioWidth = a.getInt(R.styleable.FixedAspectRatioFrameLayout_aspectRatioWidth, 4);
        mAspectRatioHeight = a.getInt(R.styleable.FixedAspectRatioFrameLayout_aspectRatioHeight, 3);
        priority = Priority.valueFromInt(a.getInt(R.styleable.FixedAspectRatioFrameLayout_priority, Priority.WIDTH_PRIORITY.intValue));
        a.recycle();
    }
    // **overrides**

    @Override protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int finalWidth, finalHeight;

        ViewGroup.LayoutParams layoutParams = getLayoutParams();

        if (priority == Priority.WIDTH_PRIORITY) {
            finalHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth;
            finalWidth = originalWidth;

        }else{
            finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight ;
            finalHeight = originalHeight;
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}