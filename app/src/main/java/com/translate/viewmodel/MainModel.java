package com.translate.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.translate.BR;

/**
 * @author: dinzhenyan
 * @time: 2018/11/22 下午11:04
 */
public class MainModel extends BaseObservable {

    private String contentInput;        // 输入框输入的内容

    private boolean translationVisible; // 是否显示单词翻译结果
    private String translation;         // 单词翻译结果

    private boolean dicVisible;         // 是否显示全部的翻译结果词典

    private boolean phoneticVisible;    // 是否显示音标
    private String usPhonetic;          // 美式音标
    private boolean usPlay;             // 美式音标读音是否显示
    private String ukPhonetic;          // 英式音标
    private boolean ukPlay;             // 英式音标读音是否显示

    private String explains;            // 词典和解释
    private String extension;           // 词典翻译拓展

    @Bindable
    public String getContentInput() {
        return contentInput;
    }

    public void setContentInput(String contentInput) {
        contentInput.replace(",", "");
        this.contentInput = contentInput;
        notifyPropertyChanged(BR.contentInput);
    }

    @Bindable
    public boolean isTranslationVisible() {
        return translationVisible;
    }

    public void setTranslationVisible(boolean translationVisible) {
        this.translationVisible = translationVisible;
        notifyPropertyChanged(BR.translationVisible);
    }

    @Bindable
    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
        notifyPropertyChanged(BR.translation);
    }

    @Bindable
    public boolean isDicVisible() {
        return dicVisible;
    }

    public void setDicVisible(boolean dicVisible) {
        this.dicVisible = dicVisible;
        notifyPropertyChanged(BR.dicVisible);
    }

    @Bindable
    public boolean isPhoneticVisible() {
        return phoneticVisible;
    }

    public void setPhoneticVisible(boolean phoneticVisible) {
        this.phoneticVisible = phoneticVisible;
        notifyPropertyChanged(BR.phoneticVisible);
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
    public String getExplains() {
        return explains;
    }

    public void setExplains(String explains) {
        this.explains = explains;
        notifyPropertyChanged(BR.explains);
    }

    @Bindable
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
        notifyPropertyChanged(BR.extension);
    }
}