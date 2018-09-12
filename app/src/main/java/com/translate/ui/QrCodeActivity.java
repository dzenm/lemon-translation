package com.translate.ui;

import android.databinding.DataBindingUtil;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;

import com.translate.R;
import com.translate.databinding.ActivityQrCodeBinding;

import cn.simonlee.xcodescanner.core.CameraScanner;
import cn.simonlee.xcodescanner.core.GraphicDecoder;
import cn.simonlee.xcodescanner.core.NewCameraScanner;
import cn.simonlee.xcodescanner.core.ZBarDecoder;

public class QrCodeActivity extends AppCompatActivity implements CameraScanner.CameraListener, TextureView.SurfaceTextureListener, GraphicDecoder.DecodeListener, View.OnClickListener {

    private ActivityQrCodeBinding binding;
    private NewCameraScanner cameraScanner;
    private GraphicDecoder graphicDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_code);
        binding.textureview.setSurfaceTextureListener(this);
        cameraScanner = NewCameraScanner.getInstance();

    }

    @Override
    protected void onRestart() {
        //部分机型在后台转前台时会回调onSurfaceTextureAvailable开启相机，因此要做判断防止重复开启
        if (binding.textureview.isAvailable()) {
            cameraScanner.setSurfaceTexture(binding.textureview.getSurfaceTexture());
            cameraScanner.setPreviewSize(binding.textureview.getWidth(), binding.textureview.getHeight());
            cameraScanner.openCamera(this.getApplicationContext());
        }
        super.onRestart();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        cameraScanner.setSurfaceTexture(surface);
        cameraScanner.setPreviewSize(width, height);
        cameraScanner.openCamera(this.getApplicationContext());
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        binding.textureview.setImageFrameMatrix();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    /**
     * 每有一帧画面，都会回调一次此方法
     *
     * @param surface
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onPause() {
        cameraScanner.closeCamera();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cameraScanner.setGraphicDecoder(null);
        if (graphicDecoder != null) {
            graphicDecoder.setDecodeListener(null);
            graphicDecoder.detach();
        }
        cameraScanner.detach();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void openCameraSuccess(int surfaceWidth, int surfaceHeight, int surfaceDegree) {
        binding.textureview.setImageFrameMatrix(surfaceWidth, surfaceHeight, surfaceDegree);
        if (graphicDecoder == null) {
//            graphicDecoder = new ZBarDecoder(this);//可使用二参构造方法指定条码识别的类型
        }
        //调用setFrameRect方法会对识别区域进行限制，注意getLeft等获取的是相对于父容器左上角的坐标，实际应传入相对于TextureView左上角的坐标。
        cameraScanner.setFrameRect(binding.scannerframe.getLeft(), binding.scannerframe.getTop(), binding.scannerframe.getRight(), binding.scannerframe.getBottom());
        cameraScanner.setGraphicDecoder(graphicDecoder);
    }

    @Override
    public void openCameraError() {

    }

    @Override
    public void noCameraPermission() {

    }

    @Override
    public void cameraDisconnected() {

    }

    @Override
    public void decodeComplete(String result, int type, int quality, int requestCode) {

    }
}
