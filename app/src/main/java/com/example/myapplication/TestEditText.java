package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

class TextWatcherDelegator implements TextWatcher {
    private static final String TAG = "TextWatcherDelegator";

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.e(
                TAG, "beforeTextChanged: " + s + " " + start + " " + " " + count);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.e(
                TAG, "onTextChanged: " + s + " " + start + " " + before + " " + count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.e(
                TAG, "afterTextChanged: " + s);
    }
}

@SuppressLint("AppCompatCustomView")
public class TestEditText extends EditText {
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
}
