package com.translate.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.translate.BR;

/**
 * @author: dinzhenyan
 * @time: 2018/11/22 下午11:04
 */
public class Word extends BaseObservable {

    private String contentInput;

    private boolean showWord;
    private String transResult;

    private boolean showDic;

    private boolean showPhonetic;
    private String usPhonetic;
    private boolean usPlay;
    private String ukPhonetic;
    private boolean ukPlay;

    private String dic;
    private String expand;

    @Bindable
    public String getContentInput() {
        return contentInput;
    }

    public void setContentInput(String contentInput) {
        this.contentInput = contentInput;
        notifyPropertyChanged(BR.contentInput);
    }

    @Bindable
    public boolean isShowWord() {
        return showWord;
    }

    public void setShowWord(boolean showWord) {
        this.showWord = showWord;
        notifyPropertyChanged(BR.showWord);
    }

    @Bindable
    public String getTransResult() {
        return transResult;
    }

    public void setTransResult(String transResult) {
        this.transResult = transResult;
        notifyPropertyChanged(BR.transResult);
    }

    @Bindable
    public boolean isShowDic() {
        return showDic;
    }

    public void setShowDic(boolean showDic) {
        this.showDic = showDic;
        notifyPropertyChanged(BR.showDic);
    }

    @Bindable
    public boolean isShowPhonetic() {
        return showPhonetic;
    }

    public void setShowPhonetic(boolean showPhonetic) {
        this.showPhonetic = showPhonetic;
        notifyPropertyChanged(BR.showPhonetic);
    }

    @Bindable
    public String getUsPhonetic() {
        return usPhonetic;
    }

    public void setUsPhonetic(String usPhonetic) {
        this.usPhonetic = usPhonetic;
        notifyPropertyChanged(BR.usPhonetic);
    }

    @Bindable
    public boolean isUsPlay() {
        return usPlay;
    }

    public void setUsPlay(boolean usPlay) {
        this.usPlay = usPlay;
        notifyPropertyChanged(BR.usPlay);
    }

    @Bindable
    public String getUkPhonetic() {
        return ukPhonetic;
    }

    public void setUkPhonetic(String ukPhonetic) {
        this.ukPhonetic = ukPhonetic;
        notifyPropertyChanged(BR.ukPhonetic);
    }

    @Bindable
    public boolean isUkPlay() {
        return ukPlay;
    }

    public void setUkPlay(boolean ukPlay) {
        this.ukPlay = ukPlay;
        notifyPropertyChanged(BR.ukPlay);
    }

    @Bindable
    public String getDic() {
        return dic;
    }

    public void setDic(String dic) {
        this.dic = dic;
        notifyPropertyChanged(BR.dic);
    }

    @Bindable
    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
        notifyPropertyChanged(BR.expand);
    }
}