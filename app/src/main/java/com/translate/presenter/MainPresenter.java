package com.translate.presenter;

import android.util.Log;

import com.translate.bean.YouDaoBean;
import com.translate.parse.YouDaoParseJSON;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainPresenter {

    private YouDaoBean bean;
    private OnParseJsonListener onParseJsonListener;
    private OkHttpClient client;

    public MainPresenter() {
        client = new OkHttpClient();
    }

    /**
     * 设置回调监听事件
     *
     * @param onParseJsonListener
     * @return
     */
    public MainPresenter setOnParseJsonListener(OnParseJsonListener onParseJsonListener) {
        this.onParseJsonListener = onParseJsonListener;
        return this;
    }

    /**
     * 返回Json数据并解析
     *
     * @param url
     */
    public MainPresenter requestJsonData(final String url) {
        Observable.create(new ObservableOnSubscribe<String>() {  // 返回observable
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url(url)
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
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String s) {
                        bean = new YouDaoParseJSON().parseJson(s);
                        if (bean == null) {
                            onParseJsonListener.onFailed();
                            return;
                        }
                        onParseJsonListener.onSucceed(bean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onParseJsonListener.onFailed();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        return this;
    }

    /**
     * Json数据解析结果
     */
    public interface OnParseJsonListener {

        void onSucceed(YouDaoBean bean);

        void onFailed();
    }
}