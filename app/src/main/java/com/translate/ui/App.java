package com.translate.ui;

import android.app.Application;

import com.translate.service.InitializeService;
import com.youdao.sdk.app.YouDaoApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());       // 数据库初始化

        // 默认配置
//        Realm realm = Realm.getDefaultInstance();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("DataBases.realm")
                .schemaVersion(4)
                .build();
        Realm.setDefaultConfiguration(config);

        InitializeService.start(this);

        // 注册应用ID ，建议在应用启动时，初始化，所有功能的使用都需要该初始化
        YouDaoApplication.init(this, "VJG2Z9euKKsRmpy5zzkd0GDQFTxz1buC");
    }
}