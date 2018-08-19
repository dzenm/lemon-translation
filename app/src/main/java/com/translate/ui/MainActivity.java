package com.translate.ui;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.card.MaterialCardView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.translate.R;
import com.translate.TransLateAPI;
import com.translate.YouDaoParseJSON;
import com.translate.apiservice.GetJSONData;
import com.translate.bean.YouDaoBean;
import com.translate.utils.Paste;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private EditText content;
    private GetJSONData get;
    private TextView us, uk, explains, explainWord, usword, ukword, text;
    private CardView show;
    private MaterialCardView translatation;
    private ImageButton us_play, uk_play;
    private YouDaoBean bean;
    private boolean visible = false;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lemon 翻译");

        show = findViewById(R.id.show);
        translatation = findViewById(R.id.translatation);
        
        content = findViewById(R.id.content);
        us = findViewById(R.id.us);
        usword = findViewById(R.id.us_pron);
        uk = findViewById(R.id.uk);
        ukword = findViewById(R.id.uk_pron);
        explains = findViewById(R.id.explains);
        explainWord = findViewById(R.id.explain_word);
        us_play = findViewById(R.id.us_play);
        uk_play = findViewById(R.id.uk_play);
        text = findViewById(R.id.text);
        
        get = new GetJSONData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear:
                content.setText("");
                break;
            case R.id.qr:
                break;
            case R.id.translate:
                String query = content.getText().toString().trim();
                String url = TransLateAPI.autoTranslate(query);
                Log.d("DZY", url);
                getResult(url);
                break;
            case R.id.us_play:
                startAnimation(us_play);
                String us_path = bean.getUs_speech();
                startVoice(us_path);
                break;
            case R.id.uk_play:
                String uk_path = bean.getUk_speech();
                startVoice(uk_path);
                startAnimation(uk_play);
                break;
            case R.id.copyToPaste:
                Paste.copyToPaste(this, text.getText().toString());
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.floatBtn:
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * 返回Json数据并解析
     *
     * @param url
     */
    private void getResult(String url) {
        get.getJsonData(url).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(String s) {
                bean = new YouDaoParseJSON().parseJson(s);
                if (bean == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            translatation.setVisibility(View.VISIBLE);
                            text.setText("请换一个词搜索");
                        }
                    });
                    return;
                }
                setData(bean);
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {
            }
        });
    }

    private void startVoice(String path) {
        Uri uri = Uri.parse(path);
        try {
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 开始播放
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置获取的数据
     *
     * @param bean
     */
    private void setData(YouDaoBean bean) {
        String us = "", uk = "", us_word = "", uk_word = "", translatation = "";
        if (bean.getL().equals("EN2zh-CHS")) {     // 判断l值语言自动选择
            us = bean.getUs_phonetic();
            uk = bean.getUk_phonetic();
            us_word = "美：";
            uk_word = "英：";
            visible = true;
        } else if (bean.getL().equals("zh-CHS2EN")) {
            us = bean.getPhonetic();
            us_word = "拼音：";
            uk_word = "";
            visible = false;
        }
        
        List<YouDaoBean.Web> webList = bean.getWeb();
        StringBuffer explain = new StringBuffer();
        for (int i = 0; i < webList.size(); i++) {
            explain.append(webList.get(i).getKey() + "\n" + webList.get(i).getValue() + "\n\n");
        }
        
        String explains = bean.getExplain();
        String text = bean.getTranslation();
        String extend = explain.toString();
        setVisible();
        showVoice(us, uk, us_word, uk_word);
        showData(explains, extend, text);
    }

    /**
     * 展示获取到的数据
     *
     * @param us_word
     * @param uk_word
     * @param us_pron
     * @param uk_pron
     */
    private void showVoice(final String us_word, final String uk_word, 
                          final String us_pron, final String uk_pron) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                us.setText(us_word);      // 美式音标
                uk.setText(uk_word);      // 英式音标
                usword.setText(us_pron);
                ukword.setText(uk_pron);
            }
        });
    }


    private void showData(final String explains_word, final String extend_word, 
                          final String translatation_text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                explains.setText(explains_word);    // 中文词义解释
                explainWord.setText(extend_word);       // 中文扩展词义
                text.setText(translatation_text);       // 中文扩展词义
            }
        });
    }

    private void setVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                translatation.setVisibility(View.VISIBLE);
                show.setVisibility(View.VISIBLE);   // 设置可见
                if (visible) {
                    us_play.setVisibility(View.VISIBLE);
                    uk_play.setVisibility(View.VISIBLE);
                } else {
                    us_play.setVisibility(View.GONE);
                    uk_play.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 播放声音图标的动画
     *
     * @param imageButton
     */
    private void startAnimation(ImageButton imageButton) {
        imageButton.setImageResource(R.drawable.play);      // 设置动画
        AnimationDrawable animationDrawable = (AnimationDrawable) imageButton.getDrawable();      // 获取动画
        animationDrawable.start();      // 开始播放
    }
}