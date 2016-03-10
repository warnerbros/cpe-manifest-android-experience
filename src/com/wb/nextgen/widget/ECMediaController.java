package com.wb.nextgen.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.wb.nextgen.R;

/**
 * Created by gzcheng on 3/9/16.
 */
public class ECMediaController extends MediaController {
    //private VideoView videoView;
    ViewGroup container;
    Context mContext;
    ImageButton maxminButton;
    public ECMediaController(Context context, ViewGroup videoViewContainer) {
        super(context);
        //init(context, videoView);
        mContext = context;
        container = videoViewContainer;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        LayoutInflater inflater2 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout wrapperLayout2 = new LinearLayout(mContext);

        inflater2.inflate(R.layout.media_player_maxmin_button, wrapperLayout2, true);
        maxminButton = (ImageButton)wrapperLayout2.findViewById(R.id.fmc_cc);
        if (mContext != null){
            /*DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) container.getLayoutParams();
            params.width = width;
            params.height=height-80;// -80 for android controls
            params.setMargins(0, 0, 0, 50);*/

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(view.getWidth(), 0, 5, 20);
            //params.gravity =  Gravity.RIGHT;
            LayoutParams wrapperParams2 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, Gravity.RIGHT);

            addView(wrapperLayout2, wrapperParams2);

            maxminButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //Log.e("media controller", "full screen onclick");

                    Intent i = new Intent("xyxyxyxhx");

                    mContext.sendBroadcast(i);

                }
            });
        }
    }
}
