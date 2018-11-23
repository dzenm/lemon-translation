package com.translate.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.translate.BR;

public class WordsBean extends BaseObservable {

    private String id;
    private String query;
    private String result;
    private boolean like;

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
        notifyPropertyChanged(BR.query);
    }

    @Bindable
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
        notifyPropertyChanged(BR.result);
    }

    @Bindable
    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
        notifyPropertyChanged(BR.like);
    }
}