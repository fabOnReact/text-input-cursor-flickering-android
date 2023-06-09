package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

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
    private boolean mUpdatingText = false;
    private boolean mCursorVisible = true;
    private long mShowCursor;
    private Blink mBlink;
    private int BLINK = 500;

    // Constructors
    public MongolTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addTextChangedListener(new MongolTextView.TextWatcherDelegator());
        init();
    }

    public MongolTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(new MongolTextView.TextWatcherDelegator());
        init();
    }

    public MongolTextView(Context context) {
        super(context);
        addTextChangedListener(new MongolTextView.TextWatcherDelegator());
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
        // setTextCursorDrawable(R.drawable.cursor_default);
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

    private void maybeSetText(CharSequence s) {
        mUpdatingText = true;
        mUpdatingText = false;
    }

    private class TextWatcherDelegator implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.w(
                    TAG, "beforeTextChanged ==> s: " + s + " start: " + start + "count: " + count);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.w(
                    TAG, "onTextChanged ==> s: " + s + " start: " + start + " before: " + before + " count: " + count);
            if (!mUpdatingText) {
                maybeSetText(s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.w(
                    TAG, "afterTextChanged ==> s: " + s);
        }
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
        super.onDraw(canvas);
        textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();
        // canvas.translate(0,  500);
        if (getText().length() > 1){
            setCursorVisible(true);
        } else if (blinkShouldBeOn()){
            setCursorVisible(false);

            // if (mCursorIsVisible) {
            setCursorLocation(getText().length());
            canvas.drawLine(mCursorX + 10.0f,
                    mCursorBottomY + 30.0f,
                    mCursorX + 10.0f,
                    mCursorBaseY + mCursorAscentY + 30f,
                    cursorPaint);
            // }
        }
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

    private boolean blinkShouldBeOn() {
        //noinspection SimplifiableIfStatement
        if (!mCursorVisible || !isFocused()) return false;
        return (SystemClock.uptimeMillis() - mShowCursor) % (2 * BLINK) < BLINK;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        mShowCursor = SystemClock.uptimeMillis();
        if (focused) {
            makeBlink();
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    private void invalidateCursorPath() {
        int start = getSelectionStart();
        if (start < 0) return;
        // Rect cursorPath = getCursorPath(start);
        // invalidate(cursorPath.left, cursorPath.top, cursorPath.right, cursorPath.bottom);
        invalidate();
    }

    private void makeBlink() {
        if (!mCursorVisible) {
            if (mBlink != null) {
                mBlink.removeCallbacks(mBlink);
            }


            return;
        }


        if (mBlink == null)
            mBlink = new Blink(this);


        mBlink.removeCallbacks(mBlink);
        mBlink.postAtTime(mBlink, mShowCursor + BLINK);
    }

    public void setCursorLocation(int characterOffset) {

        Layout layout = this.getLayout();

        if (layout != null) {

            try {
                // This method is giving a lot of crashes so just surrounding with
                // try catch for now
                mCursorX = layout.getPrimaryHorizontal(characterOffset);
                if (getText().length() > 0 && blinkShouldBeOn()) {
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

    private static class Blink extends Handler implements Runnable {
        private WeakReference<MongolTextView> mView;
        private boolean mCancelled;
        private long BLINK;


        Blink(MongolTextView v) {
            mView = new WeakReference<>(v);
        }


        public void run() {
            if (mCancelled) {
                return;
            }


            removeCallbacks(Blink.this);


            MongolTextView met = mView.get();


            if (met != null && met.isFocused()) {
                int st = met.getSelectionStart();
                int en = met.getSelectionEnd();


                if (st == en && st >= 0 && en >= 0) {
                    if (met.getLayout() != null) {
                        met.invalidateCursorPath();
                    }


                    postAtTime(this, SystemClock.uptimeMillis() + BLINK);
                }
            }
        }


        void cancel() {
            if (!mCancelled) {
                removeCallbacks(Blink.this);
                mCancelled = true;
            }
        }


        void uncancel() {
            mCancelled = false;
        }
    }
}
