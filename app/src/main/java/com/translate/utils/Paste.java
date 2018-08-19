package com.translate.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Paste {

    public static void copyToPaste(Activity activity,String text) {
        ClipboardManager clipboardManager = (ClipboardManager) 
                activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static String readFromPaste(Activity activity) {
        ClipboardManager clipboardManager = (ClipboardManager)
                activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData text = clipboardManager.getPrimaryClip();
        return String.valueOf(text);
    }
}