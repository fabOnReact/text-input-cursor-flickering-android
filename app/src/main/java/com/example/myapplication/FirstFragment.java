package com.example.myapplication;

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

class CustomLineHeightSpan implements LineHeightSpan {
    private final int mHeight;
    private final int mStartIndex;
    private final int mEndIndex;

    CustomLineHeightSpan(
            int lineHeight,
            int startIndex,
            int endIndex
    ) {
        mHeight = lineHeight;
        mStartIndex = startIndex;
        mEndIndex = endIndex;
    }
    public int getHeight() {
        return mHeight;
    }

    @Override
    public void chooseHeight(@NonNull CharSequence text, int start, int end,
                             int spanstartv, int lineHeight,
                             @NonNull Paint.FontMetricsInt fm) {

        int currentHeight = fm.descent - fm.ascent;
        // If current height is not positive, do nothing.
        if (currentHeight <= 0) return;

        boolean isFirstLine = (start == mStartIndex);
        boolean isLastLine = (end == mEndIndex);

        // if single line and should not apply, return
        // if (isFirstLine && isLastLine && trimFirstLineTop && trimLastLineBottom) return

        // if (isFirstLine) calculateTargetMetrics(fm);

       if (isFirstLine) {
           fm.ascent = -144;
           fm.descent = 8;
       }
       /*if (isLastLine) {
           fm.descent = -144;
           fm.ascent = 8;
       }*/
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
        TextView inputText = (TextView) view.findViewById(R.id.edit_text);
        inputText.setGravity(Gravity.CENTER_VERTICAL);
        // SpannableString string = new SpannableString("\n");
        // string.setSpan(new LineHeightSpan.Standard(800), 0, string.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing "
                + "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad "
                + "minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea "
                + "commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse "
                + "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non "
                + "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        SpannableString string = new SpannableString(LOREM_IPSUM);
        inputText.setText(string, TextView.BufferType.SPANNABLE);
        Spannable span = (Spannable)inputText.getText();
        // span.setSpan(new LineHeightSpan.Standard(200), 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}