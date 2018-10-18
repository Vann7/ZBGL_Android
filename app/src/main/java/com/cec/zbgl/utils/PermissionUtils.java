package com.cec.zbgl.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
//import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;


public class PermissionUtils {
    public static final int CAMERA = 0;
    public static final int ACCESS_FINE_LOCATION = 1;
    public static final int WRITE_EXTERNAL_STORAGE = 2;

    /**
     * @param activity
     * @return true:已经有权限 false:无权限正在申请
     */
    public static boolean requestCameraPermission(Activity activity) {
        //判断当前Activity是否已经获得了该权限
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {
               ToastUtils.showShort("please give me the permission");
//                Snackbar.make(activity.getWindow().getDecorView(),
//                        "please give me the permission", Snackbar.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA}, CAMERA);
            }
            return false;
        } else {
//            UIUtils.startActivity(new Intent(UIUtils.getContext(), CaptureActivity.class));
            return true;
        }
    }

    public static boolean requestStoragePermission(Activity activity) {
        //判断当前Activity是否已经获得了该权限
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ToastUtils.showShort("please give me the permission");
//                Snackbar.make(activity.getWindow().getDecorView(),
//                        "please give me the permission", Snackbar.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean requestLocationPermission(Activity activity) {
        //判断当前Activity是否已经获得了该权限
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ToastUtils.showShort("please give me the permission");
//                Snackbar.make(activity.getWindow().getDecorView(),
//                        "please give me the permission", Snackbar.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
