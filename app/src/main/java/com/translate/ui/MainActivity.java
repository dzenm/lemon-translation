package com.translate.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.translate.bean.WordsBean;
import com.translate.bean.YouDaoBean;
import com.translate.databinding.ActivityMainBinding;
import com.translate.db.Words;
import com.translate.db.WordsHelper;
import com.translate.presenter.MainPresenter;
import com.translate.utils.FabGroupAnimation;
import com.translate.utils.Paste;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements MainPresenter.OnParseJsonListener, FabGroupAnimation.OnFabItemClickListener, WordsAdapter.OnItemClickListener {

    private ActivityMainBinding binding;

    private MainPresenter presenter;
    private WordsAdapter adapter;
    private YouDaoBean bean;
    private List<WordsBean> wordsBeans;

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
        adapter.setOnItemClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        int[] ids = {R.id.diary, R.id.note};      // Fab组ID
        int[] textIDs = {R.id.diaryText, R.id.noteText};

        String[] descs = {"添加", "设置"};
        binding.fabGroup.init(this, R.layout.fab_group, binding.floatBtn).setView(ids, descs, textIDs);
        binding.fabGroup.setOnFabItemClickListener(this);
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
            case R.id.clearBtn:                 // 清空输入框
                binding.content.setText("");
                break;
            case R.id.qrBtn:
                startActivity(new Intent(this, QrCodeActivity.class));
                break;
            case R.id.translateBtn:
                String query = binding.content.getText().toString().trim();   // 获取输入的要翻译的文本
                if (query.length() <= 0) {
                    Toast.makeText(this, "请输入要查询的单词", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = TransLateAPI.autoTranslate(query);                         // 不存在时去网上获取
                Log.d("DZY", url);
                presenter.getResult(url);
                break;
            case R.id.usPlay:                           // 美式读音播放
                String us_path = bean.getUs_speech();
                startVoice(us_path);
                startAnimation(binding.usPlay);
                break;
            case R.id.ukPlay:                           // 英式读音播放
                String uk_path = bean.getUk_speech();
                startVoice(uk_path);
                startAnimation(binding.ukPlay);
                break;
            case R.id.copyToPaste:                      // 复制到剪贴板
                Paste.copyToPaste(this, binding.translation.getText().toString());
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.floatBtn:
                if (binding.fabGroup.isEnpand()) {
                    binding.fabGroup.shrinkMenu();
                } else {
                    binding.fabGroup.expandMenu();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 播放读音
     *
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
        String us_word = "", uk_word = "";
        if (bean.getL().equals("EN2zh-CHS")) {     // 判断l值语言自动选择
            String us = bean.getUs_phonetic(), uk = bean.getUk_phonetic();
            if (!us.equals("") && us.length() > 0) {
                us_word = "美：/" + us + "/";
                if (binding.usPlay.getVisibility() != View.VISIBLE) {
                    pronPlayVisible(binding.usPlay, true);
                }
            }
            if (!uk.equals("") && uk.length() > 0) {
                uk_word = "英：/" + uk + "/";
                if (binding.ukPlay.getVisibility() != View.VISIBLE) {
                    pronPlayVisible(binding.ukPlay, true);
                }
            }
        } else if (bean.getL().equals("zh-CHS2EN")) {
            String pron = bean.getPhonetic();
            if (!pron.equals("") && pron.length() > 0) {
                us_word = "拼音：" + pron;
                uk_word = "";
                if (binding.usPlay.getVisibility() != View.VISIBLE) {
                    pronPlayVisible(binding.usPlay, true);
                }
                pronPlayVisible(binding.ukPlay, false);
            } else {
                if (binding.usPlay.getVisibility() == View.VISIBLE) {
                    pronPlayVisible(binding.usPlay, false);
                }
                if (binding.ukPlay.getVisibility() == View.VISIBLE) {
                    pronPlayVisible(binding.ukPlay, false);
                }
            }
        }

        List<YouDaoBean.Web> webList = bean.getWeb();
        StringBuffer explain = new StringBuffer();          // 扩展的单词
        for (int i = 0; i < webList.size(); i++) {
            explain.append(webList.get(i).getKey() + "\n" + webList.get(i).getValue() + "\n\n");
        }

        String explains = bean.getExplain();            // 扩展的词义
        String text = bean.getTranslation();
        String extend = explain.toString();             // 翻译

        showVoice(us_word, uk_word);            // 显示音标数据
        showData(explains, extend, text);               // 显示翻译数据
    }

    /**
     * 音标数据
     *
     * @param us_word
     * @param uk_word
     */
    private void showVoice(final String us_word, final String uk_word) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.usPron.setText(us_word);      // 美式音标
                binding.ukPron.setText(uk_word);      // 英式音标
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
                binding.transLayout.setVisibility(View.VISIBLE);
                binding.explains.setText(explains_word);    // 中文词义解释
                saveData(translatation_text, binding.content.getText().toString());
                binding.show.setVisibility(View.VISIBLE);   // 设置可见
                binding.explainWord.setText(extend_word);       // 中文扩展词义
                binding.translation.setText(translatation_text);       // 中文扩展词义
                binding.recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void pronPlayVisible(final View view, final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(show ? View.VISIBLE : View.GONE);
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
        List<Words> wordsList = WordsHelper.query(english.toLowerCase());                           // 判断数据库是否存在要查询的内容
        if (wordsList.size() > 0) {
            WordsHelper.update(english, chinese);
        } else {
            WordsHelper.insert(english, chinese);
        }
        getData();
        adapter.notifyDataSetChanged();
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
                binding.show.setVisibility(View.GONE);   // 设置可见
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
        List<Words> wordsList = WordsHelper.query();
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

    @Override
    public void onFabItemClick(FloatingActionButton fab) {
        switch (fab.getId()) {
            case R.id.diary:
                Toast.makeText(this, "third", Toast.LENGTH_SHORT).show();
                break;
            case R.id.note:
                Toast.makeText(this, "forth", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * RecyclerView点击事件
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        String query = wordsBeans.get(position).getEnglish();
        binding.content.setText(query);
        String url = TransLateAPI.autoTranslate(query);
        Log.d("DZY", url);
        presenter.getResult(url);
    }
}