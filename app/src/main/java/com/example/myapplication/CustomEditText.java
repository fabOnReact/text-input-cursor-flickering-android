package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import java.lang.ref.WeakReference;

@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    private static final String TAG = "CustomEditText";
    private TextPaint textPaint;
    private Paint cursorPaint = new Paint();


    private static final float CURSOR_THICKNESS = 7f;
    private boolean mUpdatingText = false;

    // TODO - add logic to change this to true/false onFocus/onBlur
    private boolean mCursorVisible = true;
    private long mShowCursor;
    private Blink mBlink;
    private int BLINK = 500;
    // REACT NATIVE passes this props as lineHeight
    private int LINE_HEIGHT;
    // REACT NATIVE passes this props as cursorColor
    private String CURSOR_COLOR = "#03DAC5";
    private boolean mCursorIsVisible;

    // Constructors
    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addTextChangedListener(new CustomEditText.TextWatcherDelegator());
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(new CustomEditText.TextWatcherDelegator());
        init();
    }

    public CustomEditText(Context context) {
        super(context);
        addTextChangedListener(new CustomEditText.TextWatcherDelegator());
        init();
    }

    private void init() {
        LINE_HEIGHT = 300;
        Spannable span = (Spannable) getText();
        span.setSpan(new CustomLineHeightSpan(LINE_HEIGHT), 0, span.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setText(span, BufferType.SPANNABLE);

        cursorPaint.setStrokeWidth(CURSOR_THICKNESS);
        // TODO should be same as the TextInput cursorColor sent from React-Native
        cursorPaint.setColor(Color.parseColor(CURSOR_COLOR));
    }

    private void maybeSetText(CharSequence s) {
        mUpdatingText = true;
        // do nothing for now, it is part of react-native implementation
        mUpdatingText = false;
    }

    private class TextWatcherDelegator implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!mUpdatingText) {
                maybeSetText(s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // do nothing
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // we draw the cursor with drawLine instead of using default cursor
        // to fix an issue with android reported in https://issuetracker.google.com/issues/236615813
        // the solution is the same used on jetpack compose
        // which consist of modify measure to calculate the correct height
        // and manually drawing the cursor
        if (getText().length() == 0 && LINE_HEIGHT != 0) {
            // use react-native API to retrieve effectiveLineHeight
            int effectiveLineHeight = LINE_HEIGHT;
            // same implementation used in jetpack compose see https://tinyurl.com/mtmev3nj
            Spannable dummyString = new SpannableString("\u200B");
            dummyString.setSpan(new CustomLineHeightSpan(effectiveLineHeight), 0, dummyString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            Layout layout =
                    StaticLayout.Builder.obtain(dummyString, 0, dummyString.length(), getPaint(), (int) widthMeasureSpec)
                            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                            .setLineSpacing(0.f, 1.f)
                            .setIncludePad(getIncludeFontPadding()).build();
            super.onMeasure(widthMeasureSpec, layout.getHeight());
            setMeasuredDimension(getMeasuredWidth(), layout.getHeight());
        } else {
            super.onMeasure(widthMeasureSpec, getMeasuredHeight());
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textPaint = getPaint();
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();
        // TODO - Add logic onFocus/onBlur to remove the cursor
        if ((getText().length() > 0) || (LINE_HEIGHT == 0)) {
            setCursorVisible(true);
        } else if (blinkShouldBeOn()) {
            // we draw the cursor with drawLine instead of using default cursor
            // to fix an issue with android reported in https://issuetracker.google.com/issues/236615813
            // the solution is the same used on jetpack compose
            // which consist of modify measure to calculate the correct height
            // and manually drawing the cursor
            setCursorVisible(false);
            canvas.drawLine(0, 0, 0, LINE_HEIGHT, cursorPaint);
        }
        canvas.restore();
    }

    public void showCursor(boolean visible) {
        mCursorIsVisible = visible;
        this.invalidate();
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

        if (mBlink == null) mBlink = new Blink(this);

        mBlink.removeCallbacks(mBlink);
        mBlink.postAtTime(mBlink, mShowCursor + BLINK);
    }

    private static class Blink extends Handler implements Runnable {
        private WeakReference<CustomEditText> mView;
        private boolean mCancelled;
        private long BLINK;

        Blink(CustomEditText v) {
            mView = new WeakReference<>(v);
        }

        public void run() {
            if (mCancelled) {
                return;
            }

            removeCallbacks(Blink.this);

            CustomEditText met = mView.get();

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
