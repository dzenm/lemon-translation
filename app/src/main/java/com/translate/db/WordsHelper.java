package com.translate.db;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class WordsHelper {

    /**
     * 按日期排序查询所有数据
     *
     * @return
     */
    public static List<Words> query() {
        RealmResults<Words> words = Realm.getDefaultInstance()
                .where(Words.class)
                .sort("date")
                .findAll();
        List<Words> wordsList = Realm.getDefaultInstance().copyFromRealm(words);
        return wordsList;
    }

    /**
     * 增加数据，增加之前，先查询是否存在，如果存在则更新，不存在再增加新数据
     *
     * @param query
     * @param result
     */
    public static void insert(final String query, final String result) {
        final Words words = Realm.getDefaultInstance()
                .where(Words.class)
                .equalTo("query", query)
                .and()
                .equalTo("result", result)
                .findFirst();
        if (words == null) {
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Words words = realm.createObject(Words.class, UUID.randomUUID().toString());
                    words.query = query;
                    words.result = result;
                    words.date = System.currentTimeMillis();
                    words.like = false;
                }
            });
        } else {
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    words.query = query;                        //先查找后得到对象
                    words.result = result;
                    words.date = System.currentTimeMillis();
                }
            });
        }
    }

    /**
     * 根据ID删除数据
     *
     * @param id
     */
    public static boolean delete(final String id) {
        boolean isDelete = false;
        final Words words = Realm.getDefaultInstance()
                .where(Words.class)
                .equalTo("id", id)
                .findFirst();
        if (words != null) {
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    words.deleteFromRealm();
                }
            });
            isDelete = true;
        }
        return isDelete;
    }
}