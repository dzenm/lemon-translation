package com.translate.ui;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);       // 数据库初始化

        // 默认配置
//        Realm realm = Realm.getDefaultInstance();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("DataBases.realm")
                .schemaVersion(4)
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
