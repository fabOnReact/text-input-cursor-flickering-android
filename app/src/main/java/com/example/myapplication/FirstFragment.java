package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentFirstBinding;
import android.graphics.Paint;
import android.text.style.LineHeightSpan;

@SuppressLint("AppCompatCustomView")
class TestEditText extends EditText {
    public TestEditText(Context context) {
        super(context);
    }

    public TestEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TestEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}

/**
 * We use a custom {@link LineHeightSpan}, because `lineSpacingExtra` is broken. Details here:
 * https://github.com/facebook/react-native/issues/7546
 */
class CustomLineHeightSpan implements LineHeightSpan {
    private final int mHeight;
    private boolean mAlign;

    public CustomLineHeightSpan(float height) {
        this.mHeight = (int) Math.ceil(height);
    }

    public CustomLineHeightSpan(float height, boolean align) {
        this.mHeight = (int) Math.ceil(height);
        this.mAlign = align;
    }

    public int getLineHeight() {
        return mHeight;
    }

    @Override
    public void chooseHeight(
            CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        // This is more complicated that I wanted it to be. You can find a good explanation of what the
        // FontMetrics mean here: http://stackoverflow.com/questions/27631736.
        // The general solution is that if there's not enough height to show the full line height, we
        // will prioritize in this order: descent, ascent, bottom, top

        if (fm.descent > mHeight) {
            // Show as much descent as possible
            fm.bottom = fm.descent = Math.min(mHeight, fm.descent);
            fm.top = fm.ascent = 0;
        } else if (-fm.ascent + fm.descent > mHeight) {
            // Show all descent, and as much ascent as possible
            fm.bottom = fm.descent;
            fm.top = fm.ascent = -mHeight + fm.descent;
        } else if (-fm.ascent + fm.bottom > mHeight) {
            // Show all ascent, descent, as much bottom as possible
            fm.top = fm.ascent;
            fm.bottom = fm.ascent + mHeight;
        } else if (-fm.top + fm.bottom > mHeight) {
            // Show all ascent, descent, bottom, as much top as possible
            fm.top = fm.bottom - mHeight;
        } else {
            // Show proportionally additional ascent / top & descent / bottom
            final int additional = mHeight - (-fm.top + fm.bottom);

            // Round up for the negative values and down for the positive values  (arbitrary choice)
            // So that bottom - top equals additional even if it's an odd number.
            fm.top -= Math.ceil(additional / 2.0f);
            fm.bottom += Math.floor(additional / 2.0f);
            fm.ascent = fm.top;
            fm.descent = fm.bottom;
        }
    }
}

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TestEditText inputText = (TestEditText) view.findViewById(R.id.edit_text);
        String LOREM_IPSUM = "L";
        SpannableString string = new SpannableString(LOREM_IPSUM);
        inputText.setText(string, TextView.BufferType.SPANNABLE);
        Spannable span = (Spannable)inputText.getText();
        span.setSpan(new CustomLineHeightSpan(300), 0, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}