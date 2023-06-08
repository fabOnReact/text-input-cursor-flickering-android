package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
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

@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {
    private static final String TAG = "CustomEditText";
    private boolean mUpdatingText;
    private StaticLayout mStaticLayout = null;
    private TextPaint mTextPaint;
    // private String mText = "\u200b";
    private String mText = "X";

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
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
        mTextPaint.setColor(0xFF000000);

        // default to a single line of text
        int width = (int) mTextPaint.measureText(mText);
        mStaticLayout = new StaticLayout(mText, mTextPaint, (int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);

        // New API alternate
        //
        // StaticLayout.Builder builder = StaticLayout.Builder.obtain(mText, 0, mText.length(), mTextPaint, width)
        //        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        //        .setLineSpacing(0, 1) // add, multiplier
        //        .setIncludePad(false);
        // mStaticLayout = builder.build();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Tell the parent layout how big this view would like to be
        // but still respect any requirements (measure specs) that are passed down.

        // determine the width
        int width;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthRequirement = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthRequirement;
        } else {
            width = mStaticLayout.getWidth() + getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                if (width > widthRequirement) {
                    width = widthRequirement;
                    // too long for a single line so relayout as multiline
                    mStaticLayout = new StaticLayout(mText, mTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
                }
            }
        }

        // determine the height
        int height;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightRequirement = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightRequirement;
        } else {
            height = mStaticLayout.getHeight() + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightRequirement);
            }
        }

        // Required call: set width and height
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        // https://stackoverflow.com/a/6017003/7295772
        if (getText().length() == 0) {
            Paint paint = new Paint();
            Path path = new Path();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.TRANSPARENT);
            canvas.drawPaint(paint);
            for (int i = 50; i < 100; i++) {
                path.moveTo(i, i-1);
                path.lineTo(i, i);
            }
            path.close();
            paint.setStrokeWidth(30);
            paint.setPathEffect(null);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, paint);
            Path highlightPath = new Path();
            // highlightPath.moveTo(0,100);
            highlightPath.lineTo(500, 0);
            highlightPath.close();
            getLayout().getCursorPath(0, highlightPath, getText());
            // Paint paint = new Paint();
            paint.setColor(Color.BLUE);

            // do as little as possible inside onDraw to improve performance

            // draw the text on the canvas after adjusting for padding
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop() + 500);
            getLayout().draw(canvas, highlightPath, paint, 0);
            // mStaticLayout.draw(canvas);
            canvas.restore();
        } else {
            // getLayout().draw(canvas);
            super.onDraw(canvas);
        }
    }

    private void maybeSetText(CharSequence s) {
        // Spannable span = (Spannable)getText();
        requestLayout();
        invalidate();
        if (s.length() == 0) {
            // setInitialState();
        } else {
            // span.setSpan(new CustomLineHeightSpan(300), 0, span.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
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
