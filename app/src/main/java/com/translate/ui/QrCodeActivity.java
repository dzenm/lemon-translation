package com.translate.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.translate.R;
import com.translate.databinding.ActivityQrCodeBinding;
import com.youdao.ocr.online.ImageOCRecognizer;
import com.youdao.ocr.online.Line;
import com.youdao.ocr.online.Line_Line;
import com.youdao.ocr.online.OCRListener;
import com.youdao.ocr.online.OCRParameters;
import com.youdao.ocr.online.OCRResult;
import com.youdao.ocr.online.OcrErrorCode;
import com.youdao.ocr.online.Region;
import com.youdao.ocr.online.Region_Line;
import com.youdao.ocr.online.Word;
import com.youdao.sdk.app.EncryptHelper;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class QrCodeActivity extends AppCompatActivity {

    private ActivityQrCodeBinding binding;

    private TextView resultText;

    private List<SpannableString> shibieEntivitys;

    private String filePath;

    private Handler handler = new Handler();

    private OCRParameters tps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_code);
    }

    public void recognize(View view) {
        if (shibieEntivitys == null || shibieEntivitys.size() == 0) {
            Toast.makeText(this, "请拍摄或选择图片", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    for (int i = 0; i < shibieEntivitys.size(); i++) {
                        startRecognize(i);
                    }
                }
            }).start();
        } catch (Exception e) {
        }
    }

    private void startRecognize(final int i) {
        final Bitmap bitmap = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] datas = baos.toByteArray();
        String bases64 = EncryptHelper.getBase64(datas);
        int count = bases64.length();
        while (count > 1.4 * 1024 * 1024) {
            quality = quality - 10;
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            datas = baos.toByteArray();
            bases64 = EncryptHelper.getBase64(datas);
        }
        final String base64 = bases64;
        handler.post(new Runnable() {
            @Override
            public void run() {
                resultText.setText("识别中....");
            }
        });
        ImageOCRecognizer.getInstance(tps).recognize(base64,
                new OCRListener() {
                    @Override
                    public void onResult(final OCRResult result, String input) {
                        //若有更新界面操作，请切换到主线程
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                String text = getResult(result);
                                SpannableString spannableString = new SpannableString(text);
                                int start = 0;
                                while (start < text.length() && start >= 0) {
                                    int s = text.indexOf("文本", start);
                                    int end = text.indexOf("：", s) + 1;
                                    if (s >= 0) {
                                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#808080"));
                                        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(35);
                                        UnderlineSpan underlineSpan = new UnderlineSpan();
                                        spannableString.setSpan(sizeSpan, s, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                        spannableString.setSpan(colorSpan, s, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                        spannableString.setSpan(underlineSpan, s, end - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                        start = end;
                                    } else {
                                        break;
                                    }
                                }
                                shibieEntivitys.add(spannableString);
                                resultText.setText("识别完成");
                            }
                        });
                    }

                    @Override
                    public void onError(final OcrErrorCode error) {
                        // resultText.setText("识别失败");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                resultText.setText("识别失败" + error.name());
                            }
                        });
                    }
                });
    }

    private String getResult(OCRResult result) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        if (OCRParameters.TYPE_TEXT.equals(result.getType())) {
            //按文本识别
            List<Region> regions = result.getRegions();
            for (Region region : regions) {
                List<Line> lines = region.getLines();
                for (Line line : lines) {
                    sb.append("文本" + i++ + "： ");
                    List<Word> words = line.getWords();
                    for (Word word : words) {
                        sb.append(word.getText()).append(" ");
                    }
                    sb.append("\n");
                }
            }
        } else {
            //按行识别
            List<Region_Line> regions = result.getRegions_Line();
            for (Region_Line region : regions) {
                List<Line_Line> lines = region.getLines();
                for (Line_Line line : lines) {
                    sb.append("文本" + i++ + "： ");
                    sb.append(line.getText());
                    sb.append("\n");
                }
            }
        }
        String text = sb.toString();
        if (!TextUtils.isEmpty(text)) {
            text = text.substring(0, text.length() - 2);
        }
        return text;
    }
}
