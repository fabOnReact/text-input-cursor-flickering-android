package com.example.javaexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.text.LineBreaker;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {
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
    // https://stackoverflow.com/questions/12266899/onmeasure-custom-view-explanation
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        StaticLayout.Builder builder =
                StaticLayout.Builder.obtain(getText(), 0, getText().length(), getPaint(), parentWidth)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0.f, 1.f)
                        .setIncludePad(true)
                        .setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
                        .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL);
        StaticLayout layout = builder.build();

        // UNCOMMENT THIS LINE TO FIX THE ISSUE
        // this works with StaticLayout
        int newWidth = (int) layout.getEllipsizedWidth();

        // UNCOMMENT THIS LINE TO REPRODUCE THE ISSUE
        // this does not work with StaticLayout and single line
        // int newWidth = (int) layout.getLineWidth(0);

        setMeasuredDimension(newWidth, heightMeasureSpec);
    }
}
