package com.translate.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.translate.R;
import com.translate.adapter.WordsAdapter;
import com.translate.api.Api;
import com.translate.bean.WordsBean;
import com.translate.bean.YouDaoBean;
import com.translate.databinding.ActivityMainBinding;
import com.translate.db.Words;
import com.translate.db.WordsHelper;
import com.translate.presenter.MainPresenter;
import com.translate.utils.FabGroupAnimation;
import com.translate.utils.Paste;
import com.translate.viewmodel.MainModel;
import com.translate.viewmodel.WordModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements MainPresenter.OnParseJsonListener, FabGroupAnimation.OnFabItemClickListener, WordsAdapter.OnItemClickListener, TextWatcher {

    private ActivityMainBinding binding;

    private MainPresenter presenter;
    private WordsAdapter adapter;

    private YouDaoBean.Basic basic;
    private List<WordsBean> wordsBeans;

    private MainModel mainModel;
    private WordModel wordModel;                // 数据控制ViewModel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_title);     // 设置toolbar

        mainModel = new MainModel();
        binding.setMainModel(mainModel);                // 绑定ViewModel

        binding.setPresenter(this);
        presenter = new MainPresenter();
        presenter.setOnParseJsonListener(this);         // 主ViewModel

        adapter = new WordsAdapter();
        adapter.setOnItemClickListener(this);
        binding.recyclerView.setAdapter(adapter);       // 设置adapter

        wordsBeans = new ArrayList<>();
        getData();                                      // 获取本地数据
        wordModel = new WordModel();
        wordModel.setWordsBeans(wordsBeans);
        binding.setWordModel(wordModel);


        int[] ids = {R.id.setting, R.id.about};      // Fab组ID
        binding.fabGroup.init(this, R.layout.fab_group, binding.floatBtn).setView(ids);
        binding.fabGroup.setOnFabItemClickListener(this);

        binding.content.addTextChangedListener(this);
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

    /**
     * 返回到主页不退出
     */
    @Override
    public void onBackPressed() {
        if (binding.fabGroup.isEnpand()) {
            binding.fabGroup.shrinkMenu();
        } else {
            if (!moveTaskToBack(false)) {
                super.onBackPressed();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearBtn:                 // 清空输入框
                mainModel.setContentInput(getString(R.string.null_string));
                break;
            case R.id.qrBtn:
                startActivity(new Intent(this, QrCodeActivity.class));
                break;
            case R.id.translateBtn:
                String query = mainModel.getContentInput();   // 获取输入的要翻译的文本
                if (query == null || query.equals("")) {
                    Toast.makeText(this, R.string.info_input_trans_word, Toast.LENGTH_SHORT).show();
                } else {
                    String url = Api.autoTranslate(query);                         // 不存在时去网上获取
                    presenter.requestJsonData(url);
                }
                break;
            case R.id.usPlay:                           // 美式读音播放
                String us_path = basic.getUs_speech();
                startPlayVoice(us_path);
                startAnimation(binding.usPlay);
                break;
            case R.id.ukPlay:                           // 英式读音播放
                String uk_path = basic.getUk_speech();
                startPlayVoice(uk_path);
                startAnimation(binding.ukPlay);
                break;
            case R.id.copyToPaste:                      // 复制到剪贴板
                Paste.copyToPaste(this, mainModel.getTranslation());
                Toast.makeText(this, R.string.info_copy_success, Toast.LENGTH_SHORT).show();
                break;
            case R.id.floatBtn:
                if (binding.fabGroup.isEnpand()) {
                    binding.fabGroup.shrinkMenu();
                } else {
                    binding.fabGroup.expandMenu();
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            mainModel.setDicVisible(false);
            mainModel.setTranslationVisible(false);
        }
        binding.content.setSelection(s.length());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * 播放读音
     *
     * @param path
     */
    private void startPlayVoice(String path) {
        try {
            Uri uri = Uri.parse(path);
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
     * 设置获取的数据
     *
     * @param bean
     */
    private void setTranslateData(YouDaoBean bean) {
        basic = bean.getBasic();
        if (basic != null) {
            // 音标的显示
            String us_word = "", uk_word = "";
            if (bean.getL().equals("EN2zh-CHS")) {     // 判断l值语言自动选择
                String us = basic.getUs_phonetic(), uk = basic.getUk_phonetic();

                us_word = us.equals("") ? "" : "美：/" + us + "/";
                mainModel.setUsPlay(us.equals("") ? false : true);      // 美式音标和读音的显示
                uk_word = uk.equals("") ? "" : "英：/" + uk + "/";
                mainModel.setUkPlay(uk.equals("") ? false : true);      // 英式音标和读音的显示
                mainModel.setPhoneticVisible(true);
            } else if (bean.getL().equals("zh-CHS2EN")) {
                us_word = basic.getPhonetic().equals("") ? "" : "拼音：" + basic.getPhonetic();
                uk_word = "";

                mainModel.setUsPlay(false);
                mainModel.setUkPlay(false);
                mainModel.setPhoneticVisible(false);
            }

            List<YouDaoBean.Web> webList = bean.getWeb();       // 显示扩展词义
            StringBuffer explain = new StringBuffer();          // 扩展的单词
            for (int i = 0; i < webList.size(); i++) {
                explain.append("\n\n" + webList.get(i).getKey() + "\n" + webList.get(i).getValue());
            }

            mainModel.setUsPhonetic(us_word);
            mainModel.setUkPhonetic(uk_word);                                // 显示音标数据

            mainModel.setExplains(basic.getExplain());                       // 单词翻译---词典释义
            mainModel.setExtension(explain.toString());                      // 单词翻译---扩展词义

        }

        mainModel.setDicVisible(basic == null ? false : true);
        mainModel.setTranslationVisible(true);
        mainModel.setTranslation(bean.getTranslation());                            // 单词翻译结果

        WordsHelper.insert(mainModel.getContentInput(), bean.getTranslation());     // 保存翻译数据
        getData();
        binding.recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onSucceed(YouDaoBean bean) {
        setTranslateData(bean);
    }

    @Override
    public void onFailed() {
        mainModel.setTranslationVisible(true);
        mainModel.setDicVisible(false);
        mainModel.setTranslation(getResources().getString(R.string.info_change_word_to_search));
    }

    /**
     * RecyclerView数据源
     *
     * @return
     */
    public void getData() {
        wordsBeans.clear();
        List<Words> wordsList = WordsHelper.query();
        for (int i = wordsList.size() - 1; i >= 0; i--) {
            Words words = wordsList.get(i);
            WordsBean wordsBean = new WordsBean();
            wordsBean.setId(words.id);
            wordsBean.setQuery(words.query);
            wordsBean.setResult(words.result);
            wordsBean.setLike(words.like);
            wordsBeans.add(wordsBean);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onFabItemClick(FloatingActionButton fab) {
        switch (fab.getId()) {
            case R.id.setting:
                Toast.makeText(this, "third", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about:
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
        mainModel.setContentInput(query);
        presenter.requestJsonData(Api.autoTranslate(query));
    }

    @Override
    public void onItemLongClick(final int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete)
                .setPositiveButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDelete = WordsHelper.delete(wordsBeans.get(position).getId());
                        if (isDelete) {
                            wordsBeans.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, wordsBeans.size() - position);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.delete_faild, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm.getDefaultInstance().close();
    }
}