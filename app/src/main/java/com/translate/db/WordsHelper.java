package com.translate.db;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class WordsHelper {

    public static List<Words> query() {
        RealmResults<Words> words = Realm.getDefaultInstance().where(Words.class).findAll();
        List<Words> wordsList = Realm.getDefaultInstance().copyFromRealm(words);
        return wordsList;
    }

    public static List<Words> query(String filed) {
        RealmResults<Words> words = Realm.getDefaultInstance()
                .where(Words.class)
                .equalTo("query", filed)
//                .or()
//                .equalTo("result", filed)
                .findAll();
        List<Words> wordsList = Realm.getDefaultInstance().copyFromRealm(words);
        return wordsList;
    }


    public static void insert(final String query, final String result) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Words words = realm.createObject(Words.class, UUID.randomUUID().toString());
                words.setQuery(query);
                words.setResult(result);
                words.setLike(false);
            }
        });
    }

    public static void delete(final String id) {
        final RealmResults<Words> words = Realm.getDefaultInstance()
                .where(Words.class)
                .equalTo("id", id)
                .findAll();
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                words.deleteFirstFromRealm();
            }
        });
    }

    public static void update(final String query, final String result) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Words words = realm.where(Words.class).findFirst();                //先查找后得到User对象
                words.setQuery(query);
                words.setResult(result);
            }
        });
    }
}