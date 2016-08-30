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

import com.wb.nextgenlibrary.R;

/**
 * Created by gzcheng on 4/22/16.
 */
public class DotIndicatorImageView extends ImageView {

    Drawable dotIndicatorDrawable = null;
    float keyCropProductY = -1.0f;
    float keyCropProductX = -1.0f;
    public DotIndicatorImageView(Context context)
    {
        super(context);
    }

    public DotIndicatorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DotIndicatorImageView,
                0, 0);
        dotIndicatorDrawable = a.getDrawable(R.styleable.DotIndicatorImageView_indicatorDrawable);
    }

    public DotIndicatorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DotIndicatorImageView,
                0, 0);
        dotIndicatorDrawable = a.getDrawable(R.styleable.DotIndicatorImageView_indicatorDrawable);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DotIndicatorImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DotIndicatorImageView,
                0, 0);
        dotIndicatorDrawable = a.getDrawable(R.styleable.DotIndicatorImageView_indicatorDrawable);
    }

    @Override
    public void setImageDrawable(Drawable drawable){
        Drawable d = drawable;
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap clipped = dotIndicatorOverlayedBitmap(bitmap);
            d = new BitmapDrawable(getResources(), clipped);
        }
        super.setImageDrawable(d);
    }
    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null)
            super.setImageBitmap(dotIndicatorOverlayedBitmap(bm) );

        super.setImageBitmap(bm);
    }

    public Bitmap dotIndicatorOverlayedBitmap(Bitmap bm) {
        if (bm == null)
            return bm;
        Bitmap output = Bitmap.createBitmap(bm.getWidth(),
                bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(bm, rect, rect, paint);




        if (dotIndicatorDrawable != null && keyCropProductY != -1.0f && keyCropProductX != -1.0f) {



            if (dotIndicatorDrawable instanceof BitmapDrawable) {
                final Rect srcRect = new Rect(0, 0, ((BitmapDrawable)dotIndicatorDrawable).getBitmap().getWidth(), ((BitmapDrawable)dotIndicatorDrawable).getBitmap().getWidth());


                int xPos = (int)(bm.getWidth() * keyCropProductX) - srcRect.width() / 2;
                int yPos = (int)(bm.getHeight() * keyCropProductY) - srcRect.height() / 2;
                Rect desRect = new Rect(xPos, yPos, xPos + srcRect.width(), yPos + srcRect.height());

                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
                canvas.drawBitmap(((BitmapDrawable) (dotIndicatorDrawable)).getBitmap(), srcRect, desRect, paint);
            }
        }

        return output;
    }

    public void setKeyCropXY(float xRatio, float yRatio){
        keyCropProductX = xRatio;
        keyCropProductY = yRatio;
    }

}
