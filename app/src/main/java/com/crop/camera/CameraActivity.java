package com.crop.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crop.camera.camera.CameraPreview;
import com.crop.camera.utils.CameraUtils;
import com.crop.camera.utils.ImageUtils;
import com.crop.camera.utils.PermissionUtil;
import com.crop.camera.utils.ScreenUtils;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private String tag = CameraActivity.class.getName();
    private CameraPreview mCameraPreview;
    private ImageView mIvScanRect, mIvTakePhoto;
    private TextView mTvCropTip;
    private Thread cropThread;

    private String filePath;
    private Bitmap mCropBitmap;
//    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
// Manifest.permission.CAMERA};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionUtil.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            setContentView(R.layout.activity_camera);
            //支持竖屏或者横屏，不支持页面内横竖屏切换，需要此功能的请自行修改源码
            initView();
            initListener();
            initData();
        /*增加0.5秒过渡界面，解决个别手机首次申请权限导致预览界面启动慢的问题*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCameraPreview.setVisibility(View.VISIBLE);
                            mCameraPreview.focus();
                        }
                    });
                }
            }, 500);
        } else {
            //缺少相机，存储卡权限
            Toast.makeText(this, getString(R.string.str_permission_need_tip), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void initView() {
        mCameraPreview = (CameraPreview) findViewById(R.id.mCameraPreview);
        mIvScanRect = (ImageView) findViewById(R.id.mIvScanRect);
        mIvTakePhoto = (ImageView) findViewById(R.id.mIvTakePhoto);
        mTvCropTip = (TextView) findViewById(R.id.mTvCropTip);
    }

    private void initListener() {
        mIvScanRect.setOnClickListener(this);
        mIvTakePhoto.setOnClickListener(this);
        mCameraPreview.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mIvScanRect:
                mCameraPreview.switchFlashLight();
                break;
            case R.id.mCameraPreview:
                mCameraPreview.focus();
                break;
            case R.id.mIvTakePhoto:
                mIvTakePhoto.setClickable(false);
                mCameraPreview.setEnabled(false);
                CameraUtils.getCamera().setOneShotPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(final byte[] data, Camera camera) {
                        final Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小
                        camera.stopPreview();
                        if (cropThread != null) {
                            cropThread.interrupt();
                        }
                        cropThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = getBitmapFromByte(data, size.width, size.height);
                                //裁剪算法是，先将图片转换到屏幕大小(以预览界面宽或者高为准，图片按对应的高度比，裁剪掉多余的尺寸)
                                //再按屏幕尺寸和裁剪区域所占比例，等比例裁剪图片
                                cropImage(bitmap);
                            }
                        });
                        cropThread.start();

                    }
                });
                break;
        }
    }


    /**
     * 将byte[]转换成Bitmap
     *
     * @param bytes
     * @param width
     * @param height
     * @return
     */
    public Bitmap getBitmapFromByte(byte[] bytes, int width, int height) {
        final YuvImage image = new YuvImage(bytes, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
        if (!image.compressToJpeg(new Rect(0, 0, width, height), 100, os)) {
            return null;
        }
        byte[] tmp = os.toByteArray();
        Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
        return bmp;
    }


    private void cropImage(Bitmap bitmap) {
        //注意：相机尺寸和屏幕尺寸不一定是等宽高比的；
        //注意：相机实时预览尺寸的中心点和 mCameraPreView的中心点位置一样,没有包含状态栏,因为预览界面在状态栏之下


        // 实现原理：先算出最接近屏幕尺寸(除开状态栏)的预览界面，截取出与预览界面等比例的图片，最后在其中截取坐标换算后的取景框图片

        // 1 调整图片的方向，获取处理后的 图片 宽高
        int bitmapW, bitmapH;
        boolean portraitScreen = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        //根据屏幕位置来确定 图片在屏幕中的宽高
        if (portraitScreen) {
            //预览界面旋转了相机角度,位图进行相应旋转
            if (mCameraPreview.getRotatePreview() > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(mCameraPreview.getRotatePreview());
                //将其旋转为同预览界面方向的图片，此处最后得到竖向图片
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            //竖屏下  长的为高,短的为宽
            bitmapH = Math.max(bitmap.getWidth(), bitmap.getHeight());
            bitmapW = Math.min(bitmap.getWidth(), bitmap.getHeight());
        } else {
            //横屏下  长的为宽，短的为高
            bitmapW = Math.max(bitmap.getWidth(), bitmap.getHeight());
            bitmapH = Math.min(bitmap.getWidth(), bitmap.getHeight());
        }
        Log.i(tag, " bitmapW: " + bitmapW + " ,bitmapH: " + bitmapH);


        int bitmapNeedH, bitmapNeedW;
        int screenNeedW, screenNeedH;
        //这里采用预览view的宽高，因为状态栏高度不包括在预览界面中
        screenNeedW = mCameraPreview.getWidth();
        screenNeedH = mCameraPreview.getHeight();
        //    screenW /screenH = bitmapNeedW /bitmapNeedH 相当于 screenW /bitmapNeedW = screenH /bitmapNeedH
        // 2  根据预览界面宽高比，以图片的宽或者高为基准，算出该宽高比下预览view所需的图片尺寸大小

        float scale = screenNeedW * 1f / screenNeedH;
        float scaleW = screenNeedW * 1f / bitmapW;
        float scaleH = screenNeedH * 1f / bitmapH;
        // 不管横竖屏，均以比例最接近1的边计算，即对应比例大的,避免图片所需尺寸不够
        if (scaleW >= scaleH) {
            //宽的比例 最接近
            bitmapNeedW = bitmapW;
            bitmapNeedH = (int) (bitmapNeedW / scale);
        } else {
            bitmapNeedH = bitmapH;
            bitmapNeedW = (int) (bitmapNeedH * scale);
        }
        Log.i(tag, "need bitmap  bitmapNeedW: " + bitmapNeedW + " ,bitmapNeedH: " + bitmapNeedH);
        Log.i(tag, "preview bitmap  left,top:0, 0,width ,height : " + bitmapNeedW + " , " + bitmapNeedH);
        Log.i(tag, "-------------------------------------------------------------------------------------");
        //按中心点裁剪出与预览view 等比例的图片
        Bitmap preBitmap = Bitmap.createBitmap(bitmap, (bitmapW - bitmapNeedW) / 2, (bitmapH - bitmapNeedH) / 2, bitmapNeedW, bitmapNeedH);


        // 3 计算取景框/证件框在整个视图(包括了状态栏)中的绝对坐标
        int[] location = new int[2];
        //getLocationOnScreen(location)-----包含含状态栏,toolBar
        //activity 的style为NoTitle时getLocationInWindow和getLocationOnScreen得到结果一样
        mIvScanRect.getLocationInWindow(location); //包含toolBar
        float cropLeft = location[0];
        float cropTop = location[1];
        float cropRight = mIvScanRect.getRight();
        float cropBottom = mIvScanRect.getHeight() + cropTop;
        Log.i(tag, "crop onScreen left: " + cropLeft + " ,right: " + cropRight + " ,width--" + mIvScanRect.getWidth());
        Log.i(tag, "crop onScreen top  :" + cropTop + ",  bottom : " + cropBottom + ",height --" + mIvScanRect.getHeight());


        // 4 将取景框坐标换算成在整个屏幕中的比例
        int screenW, screenH;
        screenW = ScreenUtils.getScreenWidth(this);
        screenH = ScreenUtils.getScreenHeight(this);
        float percentLeftCrop = cropLeft / screenW;
        float percentTopCrop = cropTop / screenH;
        float percentRightCrop = cropRight / screenW;
        float percentBottomCrop = cropBottom / screenH;
        Log.i(tag, " percent left: " + percentLeftCrop + " ,right: " + percentRightCrop);
        Log.i(tag, " percent top: " + percentTopCrop + "  ,bottom: " + percentBottomCrop);


        mCameraPreview.getLocationInWindow(location);
        Log.i(tag, "preview left abs screen :" + location[0] + " ,top: " + location[1]);
        //计算状态栏高度对预览view的比例
        float statusBarPercent;
        //优化状态栏对预览view的影响
        float screenScale = screenW * 1f / screenH;
        float bitmapScale = bitmapW * 1f / bitmapH;
        boolean sameScale = screenScale == bitmapScale; //相机预览尺寸是否支持屏幕尺寸
        if (!sameScale) {
            // 相机尺寸不支持屏幕尺寸的预览界面
            statusBarPercent = location[1] * 1f / screenNeedH;  // 预览view的顶点即为状态栏高度
        } else {
            // 相机尺寸支持屏幕尺寸的预览界面
            statusBarPercent = location[1] * 1f / screenH;
        }
        Log.i(tag, "statusBarPercent : " + statusBarPercent);

        // 5 去除状态栏影响，裁剪出取景框对应比例的图片
        // 屏幕比预览view多一个状态栏高度，为保证屏幕中预览效果和位图(按预览view相同宽高比处理)效果一致，屏幕应该往上移动状态栏高度
        mCropBitmap = Bitmap.createBitmap(preBitmap, (int) (percentLeftCrop * bitmapNeedW),
                (int) ((percentTopCrop - statusBarPercent) * bitmapNeedH), (int) ((percentRightCrop - percentLeftCrop) * bitmapNeedW),
                (int) ((percentBottomCrop - percentTopCrop) * bitmapNeedH));
        Log.i(tag, " crop bitmap,no status bar left ,top :   " + (int) (percentLeftCrop * bitmapNeedW) + " ,"
                + (int) ((percentTopCrop - statusBarPercent) * bitmapNeedH));


        if (!preBitmap.isRecycled()) {
            preBitmap.recycle();
        }
        //保存裁剪后的区域
        if (mCropBitmap != null) {
            filePath = "mnt/sdcard/temp.jpg";
            final boolean success = ImageUtils.save(mCropBitmap, filePath, Bitmap.CompressFormat.JPEG, true);

//            Log.i(tag, "image file  rotation --" + GalleryUtil.getBitmapRotate(filePath));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (success) {
                        Intent result = new Intent();
                        result.putExtra(CameraCropHelper.CROP_FILE_PATH, filePath);
                        setResult(Activity.RESULT_OK, result);
                        //返回结果
                        finish();
                    } else {
                        // 保存图片失败 给出提示
                        mIvTakePhoto.setClickable(true);
                    }
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraPreview != null) {
            mCameraPreview.onStop();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mCropBitmap != null && mCropBitmap.isRecycled()) {
            mCropBitmap.recycle();
            mCropBitmap = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (cropThread != null) {
            cropThread.interrupt();
        }
        super.onDestroy();
    }
}
