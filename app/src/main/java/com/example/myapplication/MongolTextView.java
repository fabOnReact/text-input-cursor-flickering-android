package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class MongolTextView extends EditText {

    private static final String TAG = "MongolTextView";
    private TextPaint textPaint;
    private Paint cursorPaint = new Paint();
    private boolean mCursorIsVisible;
    private CursorTouchLocationListener listener;

    // Naming is based on pre-rotated/mirrored values
    private float mCursorBaseY;
    private float mCursorBottomY;
    private float mCursorAscentY; // This is a negative number
    private float mCursorX;

    private static final float CURSOR_THICKNESS = 5f;
    private int mCursorHeightY;

    // Constructors
    public MongolTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MongolTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MongolTextView(Context context) {
        super(context);
        init();
    }

    // This class requires the mirrored Mongolian font to be in the assets/fonts folder
    private void init() {

        Spannable span = (Spannable)getText();
        span.setSpan(new CustomLineHeightSpan(300), 0, span.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setText(span, BufferType.SPANNABLE);
        this.mCursorIsVisible = true;

        cursorPaint.setStrokeWidth(CURSOR_THICKNESS);
        cursorPaint.setColor(Color.parseColor("#03DAC5")); // TODO should be same as text color

    }

    // This interface may be deleted if touch functionality is not needed
    public interface CursorTouchLocationListener {

        /**
         * Returns the touch location to be used for the cursor so you can update the insert
         * location in a text string.
         *
         * @param glyphIndex You will need to translate glyphIndex into a Unicode index if you are using a
         *                   Unicode string.
         */
        public void onCursorTouchLocationChanged(int glyphIndex);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // swap the height and width
        super.onMeasure(widthMeasureSpec, 357);
        setMeasuredDimension(getMeasuredWidth(), 357);
        // setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();
        // if (getText().length() < 4) {
            // canvas.translate(0,  500);

            // draw the cursor
            if (mCursorIsVisible) {
                setCursorLocation(getText().length());
                canvas.drawLine(mCursorX, mCursorBottomY, mCursorX, mCursorBaseY + mCursorAscentY,
                  cursorPaint);
                // canvas.drawLine(0.0f, 300.0f, 0.0f, 170.0f + -170.0f,
                //       cursorPaint);
                // canvas.drawLine(0.0f, 300.0f, 0.0f, 170.0f + -170.0f, cursorPaint);
                // canvas.drawLine(10.0f, 330.0f, 10.0f, 25f, cursorPaint);
                // canvas.drawRect(getCursorPath(2), cursorPaint);
            }

        // } else {
            // super.onDraw(canvas);
            //canvas.drawLine(10.0f, 330.0f, 10.0f, 25f, cursorPaint);
        // }

        getLayout().draw(canvas);
        canvas.restore();
    }

  /*
    private Rect getCursorPath(int cursorLocation) {
        int line = getLayout().getLineForOffset(cursorLocation);
        int width = getLayout().getLineDescent(line) - getLayout().getLineAscent(line);
        int x = getLayout().getLineBottom(line) + getPaddingLeft();
        int y = (int) getLayout().getVertical(cursorLocation) + getPaddingTop();

        int cursorWidthPX = (int) (CURSOR_THICKNESS * getResources().getDisplayMetrics().density);
        return new Rect(x, y, x + width, y + cursorWidthPX);
    }


    public int getLineForOffset(int offset) {
        int high = getLineCount();
        int low = -1;
        int guess;

        while (high - low > 1) {
            guess = (high + low) / 2;

            if (getLineStart(guess) > offset)
                high = guess;
            else
                low = guess;
        }

        if (low < 0)
            return 0;
        else
            return low;
    }


    public final int getLineStart(int line) {
        if (mLinesInfo == null || mLinesInfo.size() == 0) return 0;
        return mLinesInfo.get(line).startOffset;
    }
     */

    public void showCursor(boolean visible) {
        mCursorIsVisible = visible;
        this.invalidate();
        // TODO make the cursor blink
    }

    public void setCursorColor(int color) {
        cursorPaint.setColor(color);
    }

    public void setCursorLocation(int characterOffset) {

        Layout layout = this.getLayout();

        if (layout != null) {

            try {
                // This method is giving a lot of crashes so just surrounding with
                // try catch for now
                mCursorX = layout.getPrimaryHorizontal(characterOffset);
                if (getText().length() > 0) {
                    int line = layout.getLineForOffset(characterOffset);
                    mCursorBaseY = layout.getLineBaseline(line);
                    mCursorBottomY = layout.getLineBottom(line);
                    mCursorAscentY = layout.getLineAscent(line);
                    Log.w(TAG,
                            "mCursorX: " + mCursorX
                                    + " mCursorBaseY: " + mCursorBaseY
                                    + " mCursorBottomY: " + mCursorBottomY
                                    + " mCursorAscentY: " + mCursorAscentY);

                    this.invalidate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class InputWindowTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            Layout layout = ((TextView) view).getLayout();

            // swapping x and y for touch events
            int y = (int) event.getX();
            int x = (int) event.getY();
            setCursorLocation(getText().length());
            if (layout != null) {

                if (getText().length() != 0) {
                    int line = layout.getLineForVertical(y);
                    int offset = layout.getOffsetForHorizontal(line, x);

                    mCursorX = layout.getPrimaryHorizontal(offset);
                    mCursorBaseY = layout.getLineBaseline(line);
                    mCursorBottomY = layout.getLineBottom(line);
                    mCursorAscentY = layout.getLineAscent(line);
                    mCursorHeightY = layout.getLineTop(line);
                    Log.w(TAG, "mCursorX: " + mCursorX +
                            " mCursorBaseY: " + mCursorBaseY +
                            " mCursorBottomY: " + mCursorBottomY +
                            " mCursorAscentY: " + mCursorAscentY + " mCursorHeightY: " + mCursorHeightY);


                    view.invalidate();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //handler.postDelayed(mLongPressed, 1000);
                            listener.onCursorTouchLocationChanged(offset);
                            break;
                        case MotionEvent.ACTION_UP:
                            //handler.removeCallbacks(mLongPressed);
                            // notify the host activity of the new cursor location

                            break;
                    }
                }

            }

            return false;
        }

    }

    public void setCursorTouchLocationListener(CursorTouchLocationListener listener) {
        this.listener = listener;
    }
}
