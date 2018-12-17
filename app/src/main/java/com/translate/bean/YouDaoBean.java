package com.translate.bean;

import java.util.List;

public class YouDaoBean {

    private List<Web> web;
    private String translation;     // 翻译
    private Basic basic;            // 单词详细信息
    private String l;               // 语言转换

    public class Web {
        private String key;     // 扩展词
        private String value;   // 扩展词翻译

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Basic {

        private List<String> examType;  // 考试类型

        private String us_phonetic;     // 美式音标
        private String us_speech;       // 美式读音
        private String uk_phonetic;     // 英式音标
        private String uk_speech;       // 英式读音

        private String phonetic;        // 中文拼音
        private String explain;         // 词性解释

        public List<String> getExamType() {
            return examType;
        }

        public void setExamType(List<String> examType) {
            this.examType = examType;
        }

        public String getUs_phonetic() {
            return us_phonetic;
        }

        public void setUs_phonetic(String us_phonetic) {
            this.us_phonetic = us_phonetic;
        }

        public String getUs_speech() {
            return us_speech;
        }

        public void setUs_speech(String us_speech) {
            this.us_speech = us_speech;
        }

        public String getUk_phonetic() {
            return uk_phonetic;
        }

        public void setUk_phonetic(String uk_phonetic) {
            this.uk_phonetic = uk_phonetic;
        }

        public String getUk_speech() {
            return uk_speech;
        }

        public void setUk_speech(String uk_speech) {
            this.uk_speech = uk_speech;
        }

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }
    }

    public List<Web> getWeb() {
        return web;
    }

    public void setWeb(List<Web> web) {
        this.web = web;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }
}