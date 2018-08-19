package com.translate.apiservice;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetJSONData {

    private OkHttpClient client;

    public GetJSONData() {
        client = new OkHttpClient();    // 新建OkHttpClient对象
    }

    public Observable<String> getJsonData(final String path) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url(path)
                        .build();       // 请求数据
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String jsonData = response.body().string();
                            emitter.onNext(jsonData);   // 请求成功数据
                        }
                        emitter.onComplete();
                    }
                });
            }
        });
        return observable;  // 返回observable
    }
}
