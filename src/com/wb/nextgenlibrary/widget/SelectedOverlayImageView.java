package com.wb.nextgenlibrary.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.wb.nextgenlibrary.R;

/**
 * Created by gzcheng on 3/7/16.
 */
public class SelectedOverlayImageView extends ImageView {

    Drawable inActiveOverlayDrawable = null;
    int inActiveOverlayColor = getResources().getColor(android.R.color.transparent);

    public SelectedOverlayImageView(Context context) {
        super(context);
    }

    public SelectedOverlayImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SelectedOverlayImageView,
                0, 0);
        inActiveOverlayDrawable = a.getDrawable(R.styleable.SelectedOverlayImageView_inActiveOverlayDrawable);
        inActiveOverlayColor = a.getInt(R.styleable.SelectedOverlayImageView_inActiveOverlayColor, inActiveOverlayColor);
    }

    public SelectedOverlayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SelectedOverlayImageView,
                0, 0);
        inActiveOverlayDrawable = a.getDrawable(R.styleable.SelectedOverlayImageView_inActiveOverlayDrawable);
        inActiveOverlayColor = a.getInt(R.styleable.SelectedOverlayImageView_inActiveOverlayColor, inActiveOverlayColor);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectedOverlayImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SelectedOverlayImageView,
                0, 0);
        inActiveOverlayDrawable = a.getDrawable(R.styleable.SelectedOverlayImageView_inActiveOverlayDrawable);
        inActiveOverlayColor = a.getInt(R.styleable.SelectedOverlayImageView_inActiveOverlayColor, inActiveOverlayColor);
    }


    @Override
    public void setActivated(boolean bool){
        super.setActivated(bool);
        dispatchDraw(new Canvas());
    }

    @Override
    public void setImageDrawable(Drawable drawable){
        Drawable d = drawable;
        if (drawable != null) {
            Bitmap bitmap;
            if (drawable instanceof GlideBitmapDrawable)
                bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
            else if (drawable instanceof BitmapDrawable)
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            else
                bitmap = null;
            if (bitmap != null) {
                Bitmap clipped = overlayBitmap(bitmap);
                d = new BitmapDrawable(getResources(), clipped);
            }
        }
        super.setImageDrawable(d);
    }
    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null)
            super.setImageBitmap(overlayBitmap(bm) );

        super.setImageBitmap(bm);
    }

    public Bitmap overlayBitmap(Bitmap bm) {
        if (bm == null)
            return bm;
        Bitmap output = Bitmap.createBitmap(bm.getWidth(),
                bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(bm, rect, rect, paint);

        if (!isActivated()) {

            // draw a circle on the overlay bitmap
            paint.setColor(inActiveOverlayColor);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            canvas.drawRect(rect, paint);

            if (inActiveOverlayDrawable != null) {
                int destWidth = Math.max(bm.getWidth(), bm.getHeight()) / 5;
                int xPos = (bm.getWidth() - destWidth)/2;
                int yPos = (bm.getHeight() - destWidth)/2;
                Rect desRect = new Rect(xPos, yPos, xPos + destWidth, yPos + destWidth);
                if (inActiveOverlayDrawable instanceof BitmapDrawable) {
                    final Rect srcRect = new Rect(0, 0, ((BitmapDrawable)inActiveOverlayDrawable).getBitmap().getWidth(), ((BitmapDrawable)inActiveOverlayDrawable).getBitmap().getWidth());
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
                    canvas.drawBitmap(((BitmapDrawable) (inActiveOverlayDrawable)).getBitmap(), srcRect, desRect, paint);
                }
            }
            // draw on top of normal state image
        }else{


            int boarderSize = getResources().getDimensionPixelSize(R.dimen.selected_thumbnail_border_size);
            final Rect innerRect = new Rect(boarderSize, boarderSize, bm.getWidth() - boarderSize, bm.getHeight() - boarderSize );

            Bitmap overlay = Bitmap.createBitmap(bm.getWidth(),
                    bm.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas onCanvas = new Canvas(overlay);

            // draw a circle on the overlay bitmap
            paint.setColor(getResources().getColor(android.R.color.white));
            onCanvas.drawRect(rect, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            paint.setColor(getResources().getColor(android.R.color.black));
            onCanvas.drawRect(innerRect, paint);

            // draw on top of normal state image
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
            canvas.drawBitmap(overlay, rect, rect, paint);
        }
        return output;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

}
