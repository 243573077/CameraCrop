
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
                CameraCropHelper.openCameraCrop(DemoActivity.this);
                //建议每次调用时，对路径进行重新赋值,避免和相册选图的路径引用同一个String对象时，加载上一次的图片
                // String filePaht = "mnt/sdcard/temp.jpg";
//                CameraCropHelper.openCameraCrop(DemoActivity.this,filePath);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CameraCropHelper.OPEN_CAMERA_CROP_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(CameraCropHelper.CROP_FILE_PATH);
                    mIvCropResult.setImageBitmap(BitmapFactory.decodeFile(path)); // 真实项目中需要将图片进行缩放压缩处理
                }
                break;
        }
    }
}
