package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    private static final String TAG = "CustomEditText";
    private Object mTextActionMode;

    // Constructors
    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomContextMenu();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomContextMenu();
    }

    public CustomEditText(Context context) {
        super(context);
        setCustomContextMenu();
    }

    private void setCustomContextMenu() {
        setCustomSelectionActionModeCallback(new CustomActionMode(TextActionMode.SELECTION));
        setCustomInsertionActionModeCallback(new CustomActionMode(TextActionMode.INSERTION));
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                id = android.R.id.pasteAsPlainText;
            } else {
                onInterceptClipDataToPlainText();
            }
        }
        return super.onTextContextMenuItem(id);
    }

    private void onInterceptClipDataToPlainText() {
        ClipboardManager clipboard = (ClipboardManager) getContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            for (int i = 0; i < clip.getItemCount(); i++) {
                final CharSequence paste;
                // Get an item as text and remove all spans by toString().
                final CharSequence text = clip.getItemAt(i).coerceToText(getContext());
                paste = (text instanceof Spanned) ? text.toString() : text;
                if (paste != null) {
                    ClipBoards.copyToClipBoard(getContext(), paste);
                }
            }
        }
    }
}

@interface TextActionMode {
    int SELECTION = 0;
    int INSERTION = 1;
    int TEXT_LINK = 2;
}
class CustomActionMode extends ActionMode.Callback2 {
    CustomActionMode(int mode) {
        super();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        menu.removeItem(android.R.id.pasteAsPlainText);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.removeItem(android.R.id.pasteAsPlainText);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}

class ClipBoards {
    public static void copyToClipBoard(@NonNull Context context, @NonNull CharSequence text) {
        ClipData clipData = ClipData.newPlainText("rebase_copy", text);
        ClipboardManager manager = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        manager.setPrimaryClip(clipData);
    }
}
