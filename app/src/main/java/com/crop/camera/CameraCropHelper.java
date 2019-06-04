package com.crop.camera;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;


public class CameraCropHelper {
    static final int OPEN_CAMERA_CROP_CODE = 0x10000001;
    static final String CROP_FILE_PATH = "crop_file_path";
    static final String FILE_PATH_DEFAULT = "mnt/sdcard/temp.jpg";


    public static void openCameraCrop(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(activity, CameraActivity.class);
        intent.putExtra(CROP_FILE_PATH, FILE_PATH_DEFAULT);
        activity.startActivityForResult(intent, OPEN_CAMERA_CROP_CODE);
    }

    public static void openCameraCrop(Activity activity, String filePath) {
        if (activity == null || TextUtils.isEmpty(filePath)) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(activity, CameraActivity.class);
        intent.putExtra(CROP_FILE_PATH, filePath);
        activity.startActivityForResult(intent, OPEN_CAMERA_CROP_CODE);
    }

    public static void openCameraCrop(Fragment fragment) {
        if (fragment == null || fragment.getContext() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(fragment.getContext(), CameraActivity.class);
        intent.putExtra(CROP_FILE_PATH, FILE_PATH_DEFAULT);
        fragment.startActivityForResult(intent, OPEN_CAMERA_CROP_CODE);
    }


    public static void openCameraCrop(Fragment fragment, String filePath) {
        if (fragment == null || fragment.getContext() == null || TextUtils.isEmpty(filePath)) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(fragment.getContext(), CameraActivity.class);
        intent.putExtra(CROP_FILE_PATH, filePath);
        fragment.startActivityForResult(intent, OPEN_CAMERA_CROP_CODE);
    }
}
