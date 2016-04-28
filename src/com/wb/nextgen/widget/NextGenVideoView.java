package com.wb.nextgen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.wb.nextgen.interfaces.NextGenPlayerInterface;

/**
 * Created by gzcheng on 4/28/16.
 */
public class NextGenVideoView extends VideoView implements NextGenPlayerInterface{

    public NextGenVideoView(Context context) {
        super(context);
    }

    public NextGenVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NextGenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NextGenVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
