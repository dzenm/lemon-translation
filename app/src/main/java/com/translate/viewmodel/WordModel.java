package com.translate.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Handler;

import com.translate.BR;
import com.translate.bean.WordsBean;

import java.util.List;

/**
 * @author: dinzhenyan
 * @time: 2018/12/14 上午9:26
 */
public class WordModel extends BaseObservable {

    private List<WordsBean> wordsBeans;

    @Bindable
    public List<WordsBean> getWordsBeans() {
        return wordsBeans;
    }

    public void setWordsBeans(List<WordsBean> wordsBeans) {
        this.wordsBeans = wordsBeans;
        notifyPropertyChanged(BR.wordsBeans);
    }
}