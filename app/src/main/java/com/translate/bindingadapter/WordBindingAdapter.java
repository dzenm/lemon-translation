package com.translate.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import com.translate.adapter.WordsAdapter;
import com.translate.bean.WordsBean;

import java.util.List;

/**
 * @author: dinzhenyan
 * @time: 2018/12/16 下午3:56
 */
public class WordBindingAdapter {

    @BindingAdapter("data")
    public static void bindingData(RecyclerView recyclerView, List<WordsBean> wordsBean) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter instanceof WordsAdapter) {
            ((WordsAdapter) adapter).setWordsBeans(wordsBean);
        }
    }
}