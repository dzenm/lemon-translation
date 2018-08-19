package com.translate.utils;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class EditTextManager implements TextWatcher {

    private View managerView;       // 监听需要管理的View
    private List<TextView> textViews;       // 需要监听的TextView
    private float alpha = 0.5f;        // 透明度
    private static final float NOT_ALPHA = 1;       // 不透明

    public EditTextManager(@Nullable View view) {
        if (view == null) throw new IllegalArgumentException("the view is enpty");
        managerView = view;
    }

    /**
     * 添加需要监听的View
     *
     * @param view
     */
    public void addView(@Nullable TextView... view) {
        if (textViews == null) {
            textViews = Arrays.asList(view);
        } else {
            textViews.addAll(Arrays.asList(view));
        }
        for (TextView textView : textViews) {
            textView.addTextChangedListener(this);  // 遍历所有的TextView，为其添加输入监听事件
        }
        afterTextChanged(null);     // 开始监听TextView输入的变化
    }

    /**
     * 移除需要监听的View，避免造成内存泄露
     *
     * @param views
     */
    public void removeView(@Nullable TextView... views) {
        if (textViews == null) return;
        for (TextView textView : textViews) {
            textView.removeTextChangedListener(this);  // 遍历所有的TextView，移除输入监听事件
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public synchronized void afterTextChanged(Editable editable) {
        for (TextView textView : textViews) {
            if ("".equals(textView.getText().toString())) {
                setEnabled(false);      // 如果TextView为空，设置不可点击和透明度
                return;
            }
            setEnabled(true); // 如果不为空，设置可点击和不透明度
        }
    }

    private void setEnabled(boolean enabled) {
        managerView.setEnabled(enabled);    // 设置点击
        managerView.setAlpha(enabled ? NOT_ALPHA : alpha);  // 设置透明度
    }
}