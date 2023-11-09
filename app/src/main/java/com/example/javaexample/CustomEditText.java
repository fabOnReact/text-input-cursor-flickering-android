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
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // setTextDirection(TEXT_DIRECTION_LTR);
    }

    public CustomEditText(Context context) {
        super(context);
        // setTextDirection(TEXT_DIRECTION_LTR);
    }
}
