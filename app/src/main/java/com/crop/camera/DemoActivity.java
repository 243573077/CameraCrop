
package com.crop.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class DemoActivity extends Activity implements View.OnClickListener {

    private TextView mTvOpenCamera;
    private ImageView mIvCropResult;
    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mTvOpenCamera = findViewById(R.id.mTvOpenCamera);
        mIvCropResult = findViewById(R.id.mIvCropResult);
        mTvOpenCamera.setOnClickListener(this);
        mIvCropResult.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //相机预览图片需裁剪到合适区域
                width = mIvCropResult.getWidth();
                height = mIvCropResult.getHeight();

                if (Build.VERSION.SDK_INT > 15) {
                    mIvCropResult.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mIvCropResult.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
//            mIvCropResult.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvOpenCamera:
                Intent intent = new Intent();
                intent.setClass(DemoActivity.this, CameraActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String path = "mnt/sdcard/temp.jpg";
            mIvCropResult.setImageBitmap(BitmapFactory.decodeFile(path)); // 真实项目中需要将图片进行缩放压缩处理
        }
    }
}
