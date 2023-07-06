package com.example.myapplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void removes_all_spans_from_edit_text() {
        Context context = ApplicationProvider.getApplicationContext();
        CustomEditText editText = new CustomEditText(context);
        SpannableString string = new SpannableString("Text with a foreground color span");
        string.setSpan(new ForegroundColorSpan(Color.RED), 12, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(string, TextView.BufferType.SPANNABLE);
        editText.setSelection(0,editText.length());
        editText.onTextContextMenuItem(android.R.id.copy);
        editText.onTextContextMenuItem(android.R.id.pasteAsPlainText);
        ForegroundColorSpan[] spans = editText.getText().getSpans(0, editText.length(), ForegroundColorSpan.class);
        assertEquals(0, spans.length);
    }

    @Test
    public void removes_one_span_from_edit_text() {
        Context context = ApplicationProvider.getApplicationContext();
        CustomEditText editText = new CustomEditText(context);
        SpannableString string = new SpannableString("Text with a foreground color span");
        string.setSpan(new ForegroundColorSpan(Color.RED), 12, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(string, TextView.BufferType.SPANNABLE);
        editText.setSelection(0,6);
        editText.onTextContextMenuItem(android.R.id.copy);
        editText.onTextContextMenuItem(android.R.id.pasteAsPlainText);
        ForegroundColorSpan[] spans = editText.getText().getSpans(0, editText.length(), ForegroundColorSpan.class);
        assertEquals(1, spans.length);
    }
}