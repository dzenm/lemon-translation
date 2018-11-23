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
import com.translate.viewmodel.Word;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements MainPresenter.OnParseJsonListener, FabGroupAnimation.OnFabItemClickListener, WordsAdapter.OnItemClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    private MainPresenter presenter;
    private WordsAdapter adapter;
    private YouDaoBean bean;
    private List<WordsBean> wordsBeans;
    private Word word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        word = new Word();
        binding.setWord(word);
        binding.setPresenter(this);

        wordsBeans = new ArrayList<>();
        presenter = new MainPresenter();
        adapter = new WordsAdapter();

        adapter.setWordsBeans(getData());
        presenter.setOnParseJsonListener(this);
        adapter.setOnItemClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

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
        if (item.getItemId() == R.id.setting) {
            Toast.makeText(this, R.string.settings, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearBtn:                 // 清空输入框
                word.setContentInput(getString(R.string.null_string));
                break;
            case R.id.qrBtn:
                startActivity(new Intent(this, QrCodeActivity.class));
                break;
            case R.id.translateBtn:
                String query = word.getContentInput();   // 获取输入的要翻译的文本
                if (query.length() <= 0) {
                    Toast.makeText(this, R.string.info_input_trans_word, Toast.LENGTH_SHORT).show();
                } else {
                    String url = TransLateAPI.autoTranslate(query);                         // 不存在时去网上获取
                    Log.d(TAG, url);
                    presenter.getResult(url);
                }
                break;
            case R.id.usPlay:                           // 美式读音播放
                String us_path = bean.getUs_speech();
                startPlayVoice(us_path);
                startAnimation(binding.usPlay);
                break;
            case R.id.ukPlay:                           // 英式读音播放
                String uk_path = bean.getUk_speech();
                startPlayVoice(uk_path);
                startAnimation(binding.ukPlay);
                break;
            case R.id.copyToPaste:                      // 复制到剪贴板
                Paste.copyToPaste(this, word.getTransResult());
                Toast.makeText(this, R.string.info_copy_success, Toast.LENGTH_SHORT).show();
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
    private void startPlayVoice(String path) {
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

            us_word = us.equals("") ? "" : "美：/" + us + "/";
            word.setUsPlay(us.equals("") ? false : true);
            uk_word = uk.equals("") ? "" : "英：/" + uk + "/";
            word.setUkPlay(uk.equals("") ? false : true);
            word.setShowPhonetic(true);
        } else if (bean.getL().equals("zh-CHS2EN")) {
            us_word = bean.getPhonetic().equals("") ? "" : "拼音：" + bean.getPhonetic();
            uk_word = "";

            word.setShowPhonetic(false);
            word.setUsPlay(false);
            word.setUkPlay(false);
        }

        List<YouDaoBean.Web> webList = bean.getWeb();
        StringBuffer explain = new StringBuffer();          // 扩展的单词
        for (int i = 0; i < webList.size(); i++) {
            explain.append(webList.get(i).getKey() + "\n" + webList.get(i).getValue() + "\n\n");
        }

        showVoice(us_word, uk_word);            // 显示音标数据
        showData(bean.getExplain(), explain.toString(), bean.getTranslation());               // 显示翻译数据
    }

    /**
     * 音标数据
     *
     * @param us_word
     * @param uk_word
     */
    private void showVoice(final String us_word, final String uk_word) {
        word.setUsPhonetic(us_word);
        word.setUkPhonetic(uk_word);
    }

    /**
     * 翻译数据
     *
     * @param dic_word
     * @param expand_word
     * @param trans_text
     */
    private void showData(final String dic_word, final String expand_word, final String trans_text) {
        word.setShowDic(true);
        word.setShowWord(true);
        word.setTransResult(trans_text);                // 单词翻译结果
        word.setDic(dic_word);                          // 单词翻译---词典释义
        word.setExpand(expand_word);                    // 单词翻译---扩展词义
        binding.recyclerView.smoothScrollToPosition(0);
        saveData(word.getContentInput(), trans_text);

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
     * @param query
     * @param result
     */
    private void saveData(final String query, final String result) {
        List<Words> wordsList = WordsHelper.query(query.toLowerCase());                           // 判断数据库是否存在要查询的内容
        if (wordsList.size() > 0) {
            WordsHelper.update(query, result);
        } else {
            WordsHelper.insert(query, result);
        }
        getData();
    }

    @Override
    public void onSucceed(YouDaoBean bean) {
        setTranslateData(bean);
        this.bean = bean;
    }

    @Override
    public void onFailed() {
        word.setShowWord(true);
        word.setShowDic(false);
        word.setTransResult(getResources().getString(R.string.info_change_word_to_search));
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
            Words words = wordsList.get(i);
            WordsBean wordsBean = new WordsBean();
            wordsBean.setId(words.getId());
            wordsBean.setQuery(words.getQuery());
            wordsBean.setResult(words.getResult());
            wordsBean.setLike(words.isLike());
            wordsBeans.add(wordsBean);
        }
        return wordsBeans;
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
        String query = wordsBeans.get(position).getQuery();
        word.setContentInput(query);
        String url = TransLateAPI.autoTranslate(query);
        Log.d(TAG, url);
        presenter.getResult(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm.getDefaultInstance().close();
    }
}