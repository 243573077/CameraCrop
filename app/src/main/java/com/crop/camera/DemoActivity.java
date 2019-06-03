
package com.crop.camera;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DemoActivity extends Activity implements View.OnClickListener {

    private TextView mTvOpenCamera;
    private ImageView mIvCropResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mTvOpenCamera = findViewById(R.id.mTvOpenCamera);
        mIvCropResult = findViewById(R.id.mIvCropResult);
        mTvOpenCamera.setOnClickListener(this);
//            mIvCropResult.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mIvCropResult:
                Intent intent = new Intent();
                intent.setClass(DemoActivity.this, CameraActivity.class);
                startActivity(intent);
                break;
        }
    }

}
