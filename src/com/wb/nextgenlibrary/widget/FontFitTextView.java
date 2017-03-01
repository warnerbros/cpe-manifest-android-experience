package com.wb.nextgenlibrary.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.wb.nextgenlibrary.util.Size;

public class FontFitTextView extends TextView {

	//Attributes
	private float maxTextSize;
	private Paint mTestPaint;

	float mSpacingMult = 1.0f;
	float mSpacingAdd = 0.0f;

    private int numberOfLinesAllowed = Integer.MAX_VALUE;


    public FontFitTextView(Context context) {
        super(context);
        initialize();
    }

    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        mTestPaint = new Paint();
        mTestPaint.set(this.getPaint());
        //max size defaults to the initially specified text size unless it is too small
		maxTextSize = getTextSize();
    }

    public void setNumberOfLinesAllowed(int lines){
        numberOfLinesAllowed = lines;
    }



    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText(String text, Size boundarySize)
    {
        if (boundarySize.getHeight() <= 0)
            return;

        Drawable drawables[] = getCompoundDrawables();
        int drawablePadding = getCompoundDrawablePadding();

        if (drawables != null && drawables.length > 0){
            int width = boundarySize.getWidth(), height = boundarySize.getHeight();
            if (drawables.length > 1 && drawables[0] != null){      // left
                width -= drawables[0].getBounds().width() + drawablePadding;
            }
            if (drawables.length > 2 && drawables[1] != null){      // top
                height -= drawables[1].getBounds().height() + drawablePadding;

            }
            if (drawables.length > 3 && drawables[2] != null){      // right
                width -= drawables[2].getBounds().width() + drawablePadding;

            }
            if (drawables.length > 4 && drawables[3] != null){      // bottom
                height -= drawables[3].getBounds().height() + drawablePadding;
            }
            boundarySize = new Size(width, height);
        }


        int targetHeight = boundarySize.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
        float hi = maxTextSize;
        float lo = 2;
        final float threshold = 0.5f; // How close we have to be

        mTestPaint.set(this.getPaint());

        while((hi - lo) > threshold) {
            float size = (hi+lo)/2;
            int textHeight = getTextHeight(text, getPaint(), boundarySize.getWidth(), size);
            int singleLineHeight = getTextHeight("C", getPaint(), boundarySize.getWidth(), size);

            if (numberOfLinesAllowed != -1 && numberOfLinesAllowed != Integer.MAX_VALUE){

                if (textHeight >= targetHeight || textHeight > (numberOfLinesAllowed * singleLineHeight))
                    hi = size; // too big
                else
                    lo = size; // too small

            } else {

                if (textHeight >= targetHeight)
                    hi = size; // too big
                else
                    lo = size; // too small
            }
        }
        // Use lo so that we undershoot rather than overshoot
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        refitText(this.getText().toString(), new Size(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec)));
        //this.setMeasuredDimension(parentWidth, height);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), new Size(getWidth(), getHeight()));
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            refitText(this.getText().toString(), new Size(w, h));
        }
    }

    /**
     * Override the set line spacing to update our internal reference values
     */
    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }

    // Set the text size of the text paint object and use a static layout to render text off screen before measuring
    private int getTextHeight(CharSequence source, TextPaint paint, int width, float textSize) {
        // modified: make a copy of the original TextPaint object for measuring
        // (apparently the object gets modified while measuring, see also the
        // docs for TextView.getPaint() (which states to access it read-only)
        TextPaint paintCopy = new TextPaint(paint);
        // Update the text paint object
        paintCopy.setTextSize(textSize);
        // Measure using a static layout. need to use 1 as spacingMult so it'll calculate the lines properly
        StaticLayout layout = new StaticLayout(source, paintCopy, width, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);

        return layout.getHeight();
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);

    }
}