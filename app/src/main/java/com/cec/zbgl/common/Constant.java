package com.cec.zbgl.common;

import android.Manifest;
import android.support.annotation.StringDef;

import com.cec.zbgl.db.DatabaseHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 全局变量
 */
public class Constant {

    public static final String DATABASE_NAME = "df.db";
    public static final int DATABASE_VERSION = 1;

    /* 头像文件 */
    public static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    public static final String CROP_IMAGE_FILE_NAME = "bala_crop.jpg";
    /* 请求识别码 */
    public static final int CODE_GALLERY_REQUEST = 0xa0;
    public static final int CODE_CAMERA_REQUEST = 0xa1;
    public static final int CODE_RESULT_REQUEST = 0xa2;
    public static final int CODE_VIDEO_REQUEST = 0xa2;
    public static final int CODE_PHOTO_REQUEST = 0xa3;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    public static int output_X = 480;
    public static int output_Y = 480;
    //改变头像的标记位
    public static int new_icon=0xa3;

    public static final int PERMISSION_READ_AND_CAMERA =0;//读和相机权限
    public static final int PERMISSION_READ =1;//读取权限

    public static final int TYPE_FOOTER_VIEW = 1;

    public static final String CONTENT_TYPE = "类别";
    public static final String CONTENT_STATUS = "状态";
    public static final String CONTENT_STSTEM = "系统";

    public static final int COURSE_IMAGE = 1;
    public static final int COURSE_VIDEO = 2;
    public static final int COURSE_DOCUMENT = 3;



}
