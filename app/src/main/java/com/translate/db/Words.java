package com.translate.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Words extends RealmObject {

    @PrimaryKey
    private String id;

    private String english;
    private String chinese;
    private boolean like;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}