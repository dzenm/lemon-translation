package com.translate.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.translate.R;
import com.translate.databinding.ActivityQrCodeBinding;

public class QrCodeActivity extends AppCompatActivity {

    private ActivityQrCodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_code);
    }
}
