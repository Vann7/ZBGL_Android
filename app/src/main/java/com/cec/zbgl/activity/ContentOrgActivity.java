package com.cec.zbgl.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.service.DeviceService;
import com.cec.zbgl.service.OrgsService;
import com.cec.zbgl.utils.ImageUtil;
import com.cec.zbgl.utils.LogUtil;
import com.cec.zbgl.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.cec.zbgl.common.Constant.CONTENT_ICON;
import static com.cec.zbgl.common.Constant.CONTENT_STATUS;
import static com.cec.zbgl.common.Constant.CONTENT_STSTEM;
import static com.cec.zbgl.common.Constant.CONTENT_TYPE;

/**
 * 备品信息详情活动
 */
public class ContentOrgActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView name_tv;
    private TextView description_tv;
    private Button course_btn;
    private AlertDialog.Builder builder;
    private SpOrgnization orgnization;
    private ImageView headImage;
    private String mExtStorDir;
    private Uri mUriPath;
    private ImageUtil imageUtil;
    private ImageView back_iv;
    private TextView head_tv;
    private String sysId; //系统mid
    private OrgsService orgsService;
    private String icons[] = {"从相册选取","拍摄"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_org);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
//            getWindow().setStatusBarColor(Color.BLACK);
        }
        mExtStorDir = Environment.getExternalStorageDirectory().toString();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        orgsService = new OrgsService();
        initView();
        initData();
        initEvent();
    }



    /**
     * 初始化界面
     */
    private void initView() {
        name_tv = (TextView) findViewById(R.id.org_name_et);
        description_tv = (TextView) findViewById(R.id.org_description_et);
        headImage = (ImageView) findViewById(R.id.id_org_image);
        course_btn = (Button) findViewById(R.id.check_course_org_btn);
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
    }


    /**
     * 初始化et数据
     */
    private void initData() {
        sysId = getIntent().getStringExtra("sysId");
        orgnization = orgsService.getOrg(sysId);
        head_tv.setText("系统详情");
        if (orgnization.getName() != null) name_tv.setText(orgnization.getName());
        if (orgnization.getDescription() != null) description_tv.setText(orgnization.getDescription());
        if (orgnization.getImage() != null) {
            byte[] bytes = orgnization.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            headImage.setImageBitmap(bitmap);
        }

    }

    /**
     * 初始化绑定事件
     */
    private void initEvent() {
        course_btn.setOnClickListener(this);
        headImage.setOnClickListener(this);
        back_iv.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_course_org_btn :
                    Intent intent = new Intent(this,CourseActivity.class);
                    intent.putExtra("sysId", sysId);
                    intent.putExtra("name",orgnization.getName());
                    startActivityForResult(intent,1);
                break;
            case R.id.bar_back_iv :
                this.finish();
                break;
            case R.id.id_org_image :
                popView(icons);
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case Constant.CODE_GALLERY_REQUEST:
                mUriPath = imageUtil.cropRawPhoto(intent.getData());
                break;
            case Constant.CODE_CAMERA_REQUEST:
                if ( imageUtil.hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory().getPath().concat("/images/"),
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
            case Constant.CODE_PHOTO_REQUEST:
                List<String> paths = intent.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                String src = paths.get(0);

                Bitmap bitmap = BitmapFactory.decodeFile(src);
                setImageToHeadView(intent,bitmap);
                break;
        }
    }


    /**
     * 弹出层选项view
     * @param list 弹出层选项
     */
    private void popView(String[] list) {
//
        builder = new AlertDialog.Builder(ContentOrgActivity.this,R.style.appalertdialog);

        builder.setTitle("设置设备图片");
        builder.setItems(list, ((dialog, which) -> {
            changeIcon(which);
        }));

        builder.show();
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
                ImageUtil imageUtil = new ImageUtil(this);
                Bitmap bitmap = imageUtil.imageZoom(b,20.00);  //图片压缩
                byte[] images = imageUtil.imageToByte(bitmap);
                orgnization.setImage(images);
                orgsService.update(orgnization);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void hideKetboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


    private void changeIcon(int position){
        if (position == 0) {
            ArrayList<String> defaultDataArray = new ArrayList<>();
            Intent intent = new Intent(this, MultiImageSelectorActivity.class);
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
            intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
            startActivityForResult(intent, Constant.CODE_PHOTO_REQUEST);
        } else {
            imageUtil = new ImageUtil(this);
//            imageUtil.checkReadPermission();
            imageUtil.checkStoragePermission();
        }

    }


}