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
public class CustomEditText extends EditText {
    private static final String TAG = "CustomEditText";
    private boolean mUpdatingText;

    public CustomEditText(Context context) {
        super(context);
        addTextChangedListener(new TextWatcherDelegator());
        setInitialState();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(new TextWatcherDelegator());
        setInitialState();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(new TextWatcherDelegator());
        setInitialState();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        addTextChangedListener(new TextWatcherDelegator());
        setInitialState();
    }
    private void setInitialState() {
        mUpdatingText = true;
        SpannableString string = new SpannableString("\u200b");
        string.setSpan(new CustomLineHeightSpan(300), 0, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setText(string, TextView.BufferType.SPANNABLE);
        mUpdatingText = false;
    }

    private void maybeSetText(CharSequence s) {
        Spannable span = (Spannable)getText();
        if (s.length() == 0) {
            setInitialState();
        } else {
            span.setSpan(new CustomLineHeightSpan(300), 0, span.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
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
}
