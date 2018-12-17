package com.translate.parse;

import com.translate.bean.YouDaoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YouDaoParseJSON {

    private YouDaoBean bean;
    private YouDaoBean.Basic basic;
    private List<YouDaoBean.Web> webList;

    /**
     * Json解析翻译
     *
     * @param jsonData 数据
     * @return
     */
    public YouDaoBean parseJson(String jsonData) {
        if (bean == null || webList == null || basic == null) {
            bean = new YouDaoBean();
            basic = new YouDaoBean.Basic();
            webList = new ArrayList<>();
        }
        JSONObject json = null;
        try {
            json = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String language = json.optString("l");      // 源语言转换的目标语言
        if (language.equals("unknown2zh-CHS")) {                   // 输入乱码时，转换语言显示转换失败。
            return null;
        }
        bean.setL(language);
        bean.setTranslation(json.optJSONArray("translation").optString(0));    // 翻译


        JSONObject basicObject = json.optJSONObject("basic");
        if (basicObject != null) {
            parseWebList(json);
            bean.setWeb(webList);      // 扩展词

            if (language.equals("EN2zh-CHS")) {     // 判断l值语言自动选择
                parseEnBasic(basicObject);
            } else if (language.equals("zh-CHS2EN")) {
                parseZhBasic(basicObject);
            }
            bean.setBasic(basic);
        }
        return bean;
    }

    /**
     * 获取扩展词义
     *
     * @param json
     */
    private void parseWebList(JSONObject json) {
        JSONArray web = json.optJSONArray("web");

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
    }

    /**
     * 获取英文翻译的词义及音标
     *
     * @param basicObject
     */
    private void parseEnBasic(JSONObject basicObject) {
        basic.setUs_phonetic(basicObject.optString("us-phonetic"));    // 美式音标
        basic.setUs_speech(basicObject.optString("us-speech"));        // 美式读音
        basic.setUk_phonetic(basicObject.optString("uk-phonetic"));    // 英式音标
        basic.setUk_speech(basicObject.optString("uk-speech"));        // 英式读音

        JSONArray explains = basicObject.optJSONArray("explains");
        StringBuffer explain = new StringBuffer();
        for (int i = 0; i < explains.length(); i++) {
            explain.append((i == 0 ? "" : "\n") + explains.opt(i));
        }
        basic.setExplain(explain.toString());    // 词性解释的数组
    }

    /**
     * 获取中文翻译的词义和拼音
     *
     * @param basicObject
     */
    private void parseZhBasic(JSONObject basicObject) {
        basic.setPhonetic(basicObject.optString("phonetic"));    // 中文拼音
        JSONArray explains = basicObject.optJSONArray("explains");
        StringBuffer explain = new StringBuffer();
        for (int i = 0; i < explains.length(); i++) {
            explain.append((i == 0 ? "" : "\n") + explains.opt(i));
        }
        basic.setExplain(explain.toString());    // 词性解释的数组
    }
}