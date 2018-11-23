package com.translate.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Words extends RealmObject {

    @PrimaryKey
    private String id;

    private String query;
    private String result;
    private boolean like;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}