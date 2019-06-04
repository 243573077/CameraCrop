package com.crop.camera;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;


public class CameraCropHelper {
    static final int OPEN_CAMERA_CROP_CODE = 0x10000001;
    static final String CROP_FILE_PATH = "crop_file_path";


    public static void openCameraCrop(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(activity, CameraActivity.class);
        activity.startActivityForResult(intent, OPEN_CAMERA_CROP_CODE);
    }

    public static void openCameraCrop(Fragment fragment) {
        if (fragment == null || fragment.getContext() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(fragment.getContext(), CameraActivity.class);
        fragment.startActivityForResult(intent, OPEN_CAMERA_CROP_CODE);
    }
}
