package com.example.javaexample;

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
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.widget.EditText;
import java.lang.ref.WeakReference;

@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    private static final String TAG = "CustomEditText";

    // Constructors
    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // setTextDirection(TEXT_DIRECTION_LTR);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // setTextDirection(TEXT_DIRECTION_LTR);
        init();
    }

    public CustomEditText(Context context) {
        super(context);
        // setTextDirection(TEXT_DIRECTION_LTR);
    }

    private void init() {
        // do nothing
    }

    private void maybeSetText(CharSequence s) {
        // do nothing
    }

    private class TextWatcherDelegator implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // maybeSetText
        }

        @Override
        public void afterTextChanged(Editable s) {
            // do nothing
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // do nothing
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // do nothing
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        // do nothing
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}
