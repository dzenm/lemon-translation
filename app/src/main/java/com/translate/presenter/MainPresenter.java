package com.translate.presenter;

import com.translate.apiservice.GetData;
import com.translate.bean.YouDaoBean;
import com.translate.parse.YouDaoParseJSON;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainPresenter {

    private GetData get;
    private YouDaoBean bean;
    private OnParseJsonListener onParseJsonListener;

    public MainPresenter setOnParseJsonListener(OnParseJsonListener onParseJsonListener) {
        this.onParseJsonListener = onParseJsonListener;
        get = new GetData();
        return this;
    }

    /**
     * 返回Json数据并解析
     *
     * @param url
     */
    public MainPresenter getResult(String url) {
        get.getJsonData(url).subscribe(new Observer<String>() {
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
            }

            @Override
            public void onComplete() {
            }
        });
        return this;
    }

    public interface OnParseJsonListener {
        
        void onSucceed(YouDaoBean bean);

        void onFailed();
    }
}