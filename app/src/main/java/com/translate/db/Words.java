package com.translate.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Words extends RealmObject {

    @PrimaryKey
    public String id;           // 主键

    public String query;        // 需要翻译的内容
    public String result;       // 翻译结果
    public long date;           // 查询的时间
    public boolean like;

}