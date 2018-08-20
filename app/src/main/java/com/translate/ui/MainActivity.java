package com.translate.ui;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.translate.R;
import com.translate.adapter.WordsAdapter;
import com.translate.api.TransLateAPI;
import com.translate.bean.YouDaoBean;
import com.translate.databases.Words;
import com.translate.databinding.ActivityMainBinding;
import com.translate.javabean.WordsBean;
import com.translate.presenter.MainPresenter;
import com.translate.utils.Paste;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements MainPresenter.OnParseJsonListener {

    private ActivityMainBinding binding;

    private MainPresenter presenter;
    private WordsAdapter adapter;
    private YouDaoBean bean;
    private List<WordsBean> wordsBeans;
    private boolean visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Lemon 翻译");
    
        presenter = new MainPresenter();
        wordsBeans = new ArrayList<>();
        presenter.setOnParseJsonListener(this);

        adapter = new WordsAdapter();
        adapter.setWordsBeans(getData());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
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
            case R.id.clearBtn:
                binding.content.setText("");
                break;
            case R.id.qrBtn:
                break;
            case R.id.translateBtn:
                String query = binding.content.getText().toString().trim();
                String url = TransLateAPI.autoTranslate(query);
                Log.d("DZY", url);
                presenter.getResult(url);
                break;
            case R.id.usPlay:
                String us_path = bean.getUs_speech();
                startVoice(us_path);
                startAnimation(binding.usPlay);
                break;
            case R.id.ukPlay:
                String uk_path = bean.getUk_speech();
                startVoice(uk_path);
                startAnimation(binding.ukPlay);
                break;
            case R.id.copyToPaste:
                Paste.copyToPaste(this, binding.translation.getText().toString());
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
     * 播放读音
     * @param path
     */
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
    private void setTranslateData(YouDaoBean bean) {
        String us = "", uk = "", us_word = "", uk_word = "";
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
        String extend = explain.toString();             // 翻译

        setVisible();
        showVoice(us, uk, us_word, uk_word);            // 显示音标数据
        showData(explains, extend, text);               // 显示翻译数据
    }

    /**
     * 音标数据
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
                binding.us.setText(us_word);      // 美式音标
                binding.uk.setText(uk_word);      // 英式音标
                binding.usPron.setText(us_pron);
                binding.ukPron.setText(uk_pron);
            }
        });
    }

    /**
     * 翻译数据
     *
     * @param explains_word
     * @param extend_word
     * @param translatation_text
     */
    private void showData(final String explains_word, final String extend_word,
                          final String translatation_text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.explains.setText(explains_word);    // 中文词义解释
                binding.explainWord.setText(extend_word);       // 中文扩展词义
                binding.translation.setText(translatation_text);       // 中文扩展词义
                saveData(translatation_text, binding.content.getText().toString());
            }
        });
    }

    /**
     * 布局可见
     */
    private void setVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.transLayout.setVisibility(View.VISIBLE);
                binding.show.setVisibility(View.VISIBLE);   // 设置可见
                if (visible) {
                    binding.usPlay.setVisibility(View.VISIBLE);
                    binding.ukPlay.setVisibility(View.VISIBLE);
                } else {
                    binding.usPlay.setVisibility(View.GONE);
                    binding.ukPlay.setVisibility(View.GONE);
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

    /**
     * 存储数据到数据库
     *
     * @param english
     * @param chinese
     */
    private void saveData(final String english, final String chinese) {
        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Words words = realm.createObject(Words.class, UUID.randomUUID().toString());
                words.setEnglish(english);
                words.setChinese(chinese);
                words.setLike(false);
                getData();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onSucceed(YouDaoBean bean) {
        setTranslateData(bean);
        this.bean = bean;
    }

    @Override
    public void onFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.transLayout.setVisibility(View.VISIBLE);
                binding.translation.setText("请换一个词搜索");
            }
        });
    }

    /**
     * RecyclerView数据源
     *
     * @return
     */
    private List<WordsBean> getData() {
        wordsBeans.clear();
        RealmResults<Words> words = Realm.getDefaultInstance().where(Words.class).findAll();
        List<Words> wordsList = Realm.getDefaultInstance().copyFromRealm(words);
        for (int i = wordsList.size() - 1; i >= 0; i--) {
            wordsBeans.add(new WordsBean(wordsList.get(i).getId(),
                    wordsList.get(i).getChinese(),
                    wordsList.get(i).getEnglish(),
                    wordsList.get(i).isLike()));
        }
        return wordsBeans;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm.getDefaultInstance().close();
    }
}