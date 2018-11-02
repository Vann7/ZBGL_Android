package com.cec.zbgl.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cec.zbgl.R;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.utils.ImageUtil;
import com.cec.zbgl.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static android.view.View.VISIBLE;

/**
 * 备品信息详情活动
 */
public class ContentActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name_Et;
    private EditText type_Et;
    private EditText location_Et;
    private EditText count_Et;
    private EditText status_Et;
    private EditText belongSys_Et;
    private EditText createName_Et;
    private EditText createTime_Et;
    private EditText description_Et;
    private Button course_btn;
    private Button save_btn;
    private TextView delete_tv;
    private DeviceInfo device;
    private ImageView headImage;
    private String mExtStorDir;
    private Uri mUriPath;
    private ImageUtil imageUtil;
    private ImageView back_iv;
    private TextView head_tv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        mExtStorDir = Environment.getExternalStorageDirectory().toString();
        initView();
        initData();
        initEvent();
    }



    /**
     * 初始化界面
     */
    private void initView() {
        name_Et = (EditText) findViewById(R.id.device_name_et);
        type_Et = (EditText) findViewById(R.id.device_type_et);
        location_Et = (EditText) findViewById(R.id.device_location_et);
        count_Et = (EditText) findViewById(R.id.device_count_et);
        status_Et = (EditText) findViewById(R.id.device_belongSys_et);
        belongSys_Et = (EditText) findViewById(R.id.device_belongSys_et);
        createName_Et = (EditText) findViewById(R.id.device_createrName_et);
        createTime_Et = (EditText) findViewById(R.id.device_createrTime_et);
        description_Et = (EditText) findViewById(R.id.device_description_et);
        headImage = (ImageView) findViewById(R.id.id_device_image);
        course_btn = (Button) findViewById(R.id.check_course_btn);
        save_btn = (Button) findViewById(R.id.device_save_btn);

        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        delete_tv = (TextView) findViewById(R.id.device_delete);
        delete_tv.setVisibility(VISIBLE);


    }


    /**
     * 初始化et数据
     */
    private void initData() {
        device = (DeviceInfo) getIntent().getSerializableExtra("device");
        name_Et.setText(device.getName());
        type_Et.setText(String.valueOf(device.getType()));
        location_Et.setText(device.getLocation());
        count_Et.setText(String.valueOf(device.getCount()));
        status_Et.setText(String.valueOf(device.getStatus()));
        belongSys_Et.setText(device.getBelongSys());
        createName_Et.setText(device.getCreaterName());
//        createTime_Et.setText(device.getCreateTime().toString());
        description_Et.setText(device.getDescription());

        head_tv.setText(device.getName());
    }

    /**
     * 初始化绑定事件
     */
    private void initEvent() {
        course_btn.setOnClickListener(this);
        headImage.setOnClickListener(this);
        back_iv.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        delete_tv.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_course_btn :
                Intent intent = new Intent(this,CourseActivity.class);
                intent.putExtra("name",device.getName());
                startActivity(intent);
                break;
            case R.id.id_device_image :
                imageUtil = new ImageUtil(this);
//                imageUtil.checkReadPermission();
                imageUtil.checkStoragePermission();
                break;
            case R.id.bar_back_iv :
                this.finish();
                break;
            case R.id.device_delete :
                ToastUtils.showShort("删除本条信息");
                break;
            case R.id.device_save_btn :
                ToastUtils.showShort("保存本条信息");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case Constant.CODE_GALLERY_REQUEST:
                mUriPath = imageUtil.cropRawPhoto(intent.getData());
                break;
            case Constant.CODE_CAMERA_REQUEST:
                if ( imageUtil.hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            Constant.IMAGE_FILE_NAME);
                    mUriPath = imageUtil.cropRawPhoto(imageUtil.getImageContentUri(tempFile));
                } else {
                    ToastUtils.showShort("没有SDCard!");
                }

                break;

            case Constant.CODE_RESULT_REQUEST:
                /*if (intent != null) {
                    setImageToHeadView(intent);    //此代码在小米有异常，换以下代码
                }*/
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUriPath));
                    setImageToHeadView(intent,bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * 权限申请回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Constant.PERMISSION_READ_AND_CAMERA:
                for (int i=0;i<grantResults.length;i++){
                    if (grantResults[i]==PackageManager.PERMISSION_DENIED){
                        ToastUtils.showShort("申请权限失败");
                        return;
                    }
                }
                imageUtil.choseHeadImageFromCameraCapture();
                break;
            case Constant.PERMISSION_READ:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageUtil.choseHeadImageFromGallery();
                }
                break;
        }
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent,Bitmap b) {
        try {
            if (intent != null) {
                headImage.setImageBitmap(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}