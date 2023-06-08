package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class MongolTextView extends EditText {

    private TextPaint textPaint;
    private Paint cursorPaint = new Paint();
    private boolean mCursorIsVisible;
    private CursorTouchLocationListener listener;

    // Naming is based on pre-rotated/mirrored values
    private float mCursorBaseY;
    private float mCursorBottomY;
    private float mCursorAscentY; // This is a negative number
    private float mCursorX;

    private static final float CURSOR_THICKNESS = 2f;
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
        cursorPaint.setColor(Color.BLACK); // TODO should be same as text color

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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), 357);
        // setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();

        // canvas.translate(0,  500);

        // draw the cursor
        if (mCursorIsVisible) {
            setCursorLocation(1);
            canvas.drawLine(mCursorX, mCursorBottomY, mCursorX, mCursorBaseY + mCursorAscentY,
                    cursorPaint);
        }

        // here create a copy of the layout
        // add a span
        // and draw on that layout
        Layout newLayout;
        if (getText().length() < 2) {
             newLayout = new StaticLayout("\u200b",
                    textPaint,
                    getLayout().getWidth(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0,
                    false);
        } else {
             newLayout = new StaticLayout(getText(),
                    textPaint,
                    getLayout().getWidth(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0,
                    false);
        }
        newLayout.draw(canvas);

        // getLayout().draw(canvas);

        canvas.restore();
    }

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

                int line = layout.getLineForOffset(characterOffset);
                mCursorX = layout.getPrimaryHorizontal(characterOffset);
                mCursorBaseY = layout.getLineBaseline(line);
                mCursorBottomY = layout.getLineBottom(line);
                mCursorAscentY = layout.getLineAscent(line);

                this.invalidate();
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

            if (layout != null) {

                int line = layout.getLineForVertical(y);
                int offset = layout.getOffsetForHorizontal(line, x);

                mCursorX = layout.getPrimaryHorizontal(offset);
                mCursorBaseY = layout.getLineBaseline(line);
                mCursorBottomY = layout.getLineBottom(line);
                mCursorAscentY = layout.getLineAscent(line);
                mCursorHeightY = layout.getLineTop(line);

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

            return false;
        }

    }

    public void setCursorTouchLocationListener(CursorTouchLocationListener listener) {
        this.listener = listener;
    }
}
