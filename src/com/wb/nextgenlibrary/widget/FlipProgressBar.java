package com.wb.nextgenlibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by gzcheng on 7/8/16.
 */
public class FlipProgressBar extends ProgressBar {
    public FlipProgressBar(Context context) {
        this(context, null);
    }

    public FlipProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlipProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(-1, 1);
        canvas.translate(-canvas.getWidth(), 0);
        super.onDraw(canvas);
        canvas.restore();
    }
}
