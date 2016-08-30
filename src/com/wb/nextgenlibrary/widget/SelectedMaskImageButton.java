package com.wb.nextgenlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.wb.nextgenlibrary.R;


/**
 * Created by gzcheng on 3/7/16.
 */
public class SelectedMaskImageButton extends ImageButton{

    int selectedOverlayColor = getResources().getColor(R.color.transparent_black);

    public SelectedMaskImageButton(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SelectedMaskImageButton,
                0, 0);
        selectedOverlayColor = a.getInt(R.styleable.SelectedMaskImageButton_selectedOverlayColor, selectedOverlayColor);
    }

    public SelectedMaskImageButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.SelectedMaskImageButton,
            0, 0);
        selectedOverlayColor = a.getInt(R.styleable.SelectedMaskImageButton_selectedOverlayColor, selectedOverlayColor);
    }

    public SelectedMaskImageButton(final Context context)
    {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && isEnabled()) {
            setColorFilter(selectedOverlayColor, PorterDuff.Mode.SRC_ATOP); //your color here
        }
        if(event.getAction() == MotionEvent.ACTION_UP)
            setColorFilter(null);

        return super.onTouchEvent(event);
    }

}
