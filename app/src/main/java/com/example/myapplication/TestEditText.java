package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class TestEditText extends EditText {
    private static final String TAG = "TestEditText";
    private String mInternalValue;
    private boolean mUpdatingText;

    public TestEditText(Context context) {
        super(context);
        addTextChangedListener(new TextWatcherDelegator());
    }

    public TestEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(new TextWatcherDelegator());
    }

    public TestEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(new TextWatcherDelegator());
    }

    public TestEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        addTextChangedListener(new TextWatcherDelegator());
    }

    private void updateCachedSpannable(CharSequence s) {
        mInternalValue = s + " Updated";
        Log.w(TAG, "updatedCachedSpannable ==> mInternalValue: " + mInternalValue);
        Spannable span = (Spannable)getText();
        span.setSpan(new CustomLineHeightSpan(300), 0, span.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        if (s.length() == 0) {
            mUpdatingText = true;
            Log.e(TAG, "updatedCachedSpannable ==> s.length(): " + s.length());
            SpannableString string = new SpannableString("\u200b");
            string.setSpan(new CustomLineHeightSpan(300), 0, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            setText(string, TextView.BufferType.SPANNABLE);
            invalidate();
            requestLayout();
        }
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
                updateCachedSpannable(s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.w(
                    TAG, "afterTextChanged ==> s: " + s);
        }
    }
}
