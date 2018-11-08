package com.cec.zbgl.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cec.zbgl.R;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.utils.ImageUtil;
import com.cec.zbgl.utils.ToastUtils;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.cec.zbgl.common.Constant.CONTENT_STATUS;
import static com.cec.zbgl.common.Constant.CONTENT_STSTEM;
import static com.cec.zbgl.common.Constant.CONTENT_TYPE;

/**
 * 备品信息详情活动
 */
public class ContentActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name_Et;
    private TextView type_Et;
    private EditText location_Et;
    private EditText count_Et;
    private TextView status_Et;
    private TextView belongSys_Et;
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
    private LinearLayout type_ll;
    private LinearLayout status_ll;
    private LinearLayout belongSys_ll;
    private AlertDialog.Builder builder;

    private String status[] = {"使用中","未入库","已入库","已出库"};
    private String types[] = {"网线","显示器","路由器","鼠标","键盘","笔记本","电源","耳机"};
    private String systems[] = {"系统1","系统2","系统3","系统4"};


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
        type_Et = (TextView) findViewById(R.id.device_type_et);
        type_Et.setFocusable(false);
        location_Et = (EditText) findViewById(R.id.device_location_et);
        count_Et = (EditText) findViewById(R.id.device_count_et);
        status_Et = (TextView) findViewById(R.id.device_status_et);
        status_Et.setFocusable(false);
        belongSys_Et = (TextView) findViewById(R.id.device_belongSys_et);
        belongSys_Et.setFocusable(false);
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

        type_ll = (LinearLayout)findViewById(R.id.device_type_ll);
        status_ll = (LinearLayout)findViewById(R.id.device_status_ll);
        belongSys_ll = (LinearLayout)findViewById(R.id.device_belongSys_ll);

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

        type_ll.setOnClickListener(this);
        status_ll.setOnClickListener(this);
        belongSys_ll.setOnClickListener(this);
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
            case R.id.device_type_ll :
                popView(CONTENT_TYPE,types);
                break;
            case R.id.device_status_ll :
                popView(CONTENT_STATUS,status);
                break;
            case R.id.device_belongSys_ll :
                popView(CONTENT_STSTEM,systems);
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


    /**
     * 弹出层选项view
     * @param list 弹出层选项
     * @param type 弹出层类型
     */
    private void popView(String type, String[] list) {
//
        builder = new AlertDialog.Builder(ContentActivity.this,R.style.appalertdialog);
        switch (type) {
            case CONTENT_TYPE :
                builder.setTitle("请选择设备类别");
                builder.setItems(list, ((dialog, which) -> {
                    type_Et.setText(list[which]);
                }));
                break;
            case CONTENT_STATUS :
                builder.setTitle("请选择设备状态");
                builder.setItems(list, ((dialog, which) -> {
                    status_Et.setText(list[which]);
                }));
                break;
            case CONTENT_STSTEM :
                builder.setTitle("请选择设备所属系统");
                builder.setItems(list, ((dialog, which) -> {
                    belongSys_Et.setText(list[which]);
                }));
                break;
        }
        builder.show();
    }


    public void hideKetboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


}