package com.translate.presenter;

import com.translate.bean.YouDaoBean;
import com.translate.parse.YouDaoParseJSON;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainPresenter {

    private YouDaoBean bean;
    private OnParseJsonListener onParseJsonListener;
    private OkHttpClient client;

    /**
     * 设置回调监听事件
     *
     * @param onParseJsonListener
     * @return
     */
    public MainPresenter setOnParseJsonListener(OnParseJsonListener onParseJsonListener) {
        this.onParseJsonListener = onParseJsonListener;
        client = new OkHttpClient();
        return this;
    }

    /**
     * 请求Json数据
     *
     * @param path
     * @return
     */
    public Observable<String> getJsonData(final String path) {
        return Observable.create(new ObservableOnSubscribe<String>() {  // 返回observable
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
    }

    /**
     * 返回Json数据并解析
     *
     * @param url
     */
    public MainPresenter requestJsonData(String url) {
        getJsonData(url).subscribe(new Observer<String>() {
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