package com.example.javaexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.text.LineBreaker;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {

    private StaticLayout mStaticLayout;

    public float toPixelFromDIP(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private static final String TAG = "CustomEditText";

    // Constructors
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context) {
        super(context);
    }

    // https://stackoverflow.com/a/12267248/7295772
    // https://stackoverflow.com/a/41779935/7295772
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // StaticLayout.Builder.obtain(getText(), 0, getText().length(), getPaint(), (int) toPixelFromDIP(widthMeasureSpec))
        /*
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        */

        float dip = 360f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        /*
        int textWidth = (int) getPaint().measureText((String) getText());
        StaticLayout.Builder builder =
                StaticLayout.Builder.obtain(getText(), 0, getText().length(), getPaint(), (int) px)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0.f, 1.f)
                        .setIncludePad(true)
                        .setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
                        .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE);
        Layout layout = builder.build();
        int lineWidth = (int) layout.getLineWidth(0);
        Log.w("TESTING ", "screenWidth: " + screenWidth + "\n textWidth: " + textWidth + "\n lineWidth: " + lineWidth + "\n widthMeasureSpec: " + widthMeasureSpec);
        */
        Log.w("TESTING ", "px: " + px);
        setMeasuredDimension((int) px, heightMeasureSpec);
    }
}
