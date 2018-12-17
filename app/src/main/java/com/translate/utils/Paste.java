package com.translate.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Paste {

    /**
     * 复制到剪切板
     * @param activity
     * @param text
     */
    public static void copyToPaste(Activity activity,String text) {
        ClipboardManager clipboardManager = (ClipboardManager) 
                activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * 从剪切板读取数据
     * @param activity
     * @return
     */
    public static String readFromPaste(Activity activity) {
        ClipboardManager clipboardManager = (ClipboardManager)
                activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData text = clipboardManager.getPrimaryClip();
        return String.valueOf(text);
    }
}