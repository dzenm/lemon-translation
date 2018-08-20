package com.translate.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TransLateAPI {

    private static final String API = "http://openapi.youdao.com/api?";
    private static final String APP_KEY = "7211f1407e248b47";       // app_key
    private static final String KEY = "VJG2Z9euKKsRmpy5zzkd0GDQFTxz1buC";       // 密钥
    private static final String SALT = "212";       // 随机数
    private static final String AUTO = "auto";       // 中文
    
    public static String autoTranslate(String query) {
        String sign = TransLateAPI.md5(APP_KEY + query + SALT + KEY);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("q", query);
        params.put("from", AUTO);
        params.put("to", AUTO);
        params.put("appKey", APP_KEY);
        params.put("salt", SALT);
        params.put("sign", sign);
        return url(API, params);
    }

    /**
     * 生成32位MD5摘要
     *
     * @param string
     * @return
     */
    public static String md5(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes();
        try {
            /** 获得MD5摘要算法的 MessageDigest 对象 */
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            /** 使用指定的字节更新摘要 */
            mdInst.update(btInput);
            /** 获得密文 */
            byte[] md = mdInst.digest();
            /** 把密文转换成十六进制的字符串形式 */
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String url(String url, Map<String, String> params) {
        if (params == null) return url;
        StringBuilder builder = new StringBuilder(url);
        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (i != 0) builder.append("&");
            builder.append(key);
            builder.append("=");
            builder.append(encode(value));
            i++;
        }
        return builder.toString();
    }

    /**
     * 进行URL编码
     *
     * @param input
     * @return
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }
        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return input;
    }
}