package com.translate;

import android.util.Log;

import com.translate.bean.YouDaoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YouDaoParseJSON {

    private YouDaoBean bean;
    private List<YouDaoBean.Web> webList;

    /**
     * Json解析翻译
     *
     * @param jsonData 数据
     * @return
     */
    public YouDaoBean parseJson(String jsonData) {
        if (bean == null || webList == null) {
            bean = new YouDaoBean();
            webList = new ArrayList<>();
        }
        JSONObject json = null;
        try {
            json = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject basic = json.optJSONObject("basic");
        if (basic == null) {    // 输入乱码，basic不存在。
            return null;
        }
        String language = json.optString("l");      // 源语言转换的目标语言
        bean.setL(language);
        Log.d("DZY", "" + language);
        bean.setTranslation(json.optJSONArray("translation").optString(0));    // 翻译
        JSONArray web = json.optJSONArray("web");
        getWeb(web);
        if (language.equals("EN2zh-CHS")) {     // 判断l值语言自动选择
            getEnBasic(basic);
        } else if (language.equals("zh-CHS2EN")) {
            getZhBasic(basic);
        }
        return bean;
    }

    /**
     * 获取扩展词义
     *
     * @param web
     */
    private void getWeb(JSONArray web) {
        for (int i = 0; i < web.length(); i++) {
            JSONObject content = web.optJSONObject(i);
            JSONArray valueArray = content.optJSONArray("value");
            StringBuffer value = new StringBuffer();
            for (int j = 0; j < valueArray.length(); j++) {
                value.append(valueArray.opt(j) + ", ");
            }
            YouDaoBean.Web webBean = new YouDaoBean().new Web();
            webBean.setKey(content.optString("key"));
            webBean.setValue(value.toString());    // 扩展词翻译
            webList.add(webBean);
        }
        bean.setWeb(webList);      // 扩展词
    }

    /**
     * 获取英文翻译的词义及音标
     *
     * @param basic
     */
    private void getEnBasic(JSONObject basic) {
        bean.setUs_phonetic(basic.optString("us-phonetic"));    // 美式音标
        bean.setUs_speech(basic.optString("us-speech"));        // 美式读音
        bean.setUk_phonetic(basic.optString("uk-phonetic"));    // 英式音标
        bean.setUk_speech(basic.optString("uk-speech"));        // 英式读音

        JSONArray explains = basic.optJSONArray("explains");
        StringBuffer explain = new StringBuffer();
        for (int i = 0; i < explains.length(); i++) {
            explain.append(explains.opt(i) + "\n");
        }
        bean.setExplain(explain.toString());    // 词性解释的数组
    }

    /**
     * 获取中文翻译的词义和拼音
     *
     * @param basic
     */
    private void getZhBasic(JSONObject basic) {
        bean.setPhonetic(basic.optString("phonetic"));    // 中文拼音
        JSONArray explains = basic.optJSONArray("explains");
        StringBuffer explain = new StringBuffer();
        for (int i = 0; i < explains.length(); i++) {
            explain.append(explains.opt(i) + "\n");
        }
        bean.setExplain(explain.toString());    // 词性解释的数组
    }
}