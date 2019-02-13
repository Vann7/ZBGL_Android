package com.cec.zbgl.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.ReleAdapter;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.DeviceRele;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.model.User;
import com.cec.zbgl.service.CourseService;
import com.cec.zbgl.service.DeviceService;
import com.cec.zbgl.service.OrgsService;
import com.cec.zbgl.utils.ImageUtil;
import com.cec.zbgl.utils.LogUtil;
import com.cec.zbgl.utils.ToastUtils;

import org.w3c.dom.Text;

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
public class ContentActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name_Et;
    private TextView type_Et;
    private EditText location_Et;
    private EditText count_Et;
    private TextView status_Et;
    private TextView belongSys_Et;
    private String belongSys_Value;
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
    private ImageView rele_device_iv;
    private TextView head_tv;
    private LinearLayout type_ll;
    private LinearLayout status_ll;
    private LinearLayout belongSys_ll;
    private AlertDialog.Builder builder;
    private String mid; //装备mid
    private String releMid; //装备mid
    private boolean add; //是否新增
    private DeviceService deviceService;
    private OrgsService orgsService;

    private String status[] = {"未入库","已入库","已出库"};
    private String types[] = {"移动Pad","交换机","服务器","磁盘阵列","计算机","显示终端","笔记本","打印机","网线","水晶头"};
    private String systems[];
    private String systemIds[];
    private String icons[] = {"从相册选取","拍摄"};
    private List<String> names;
    private List<String> mIds;
    private HashMap<String, String> sysMap = new HashMap<>();
    private List<SpOrgnization> orgList;
    private List<DeviceRele> rele_dList;
    private List<DeviceRele> rele_temp_list;
    private List<DeviceRele> rele_delete_list;
    private GridView rele_gv;
    private ReleAdapter releAdapter;
    private Bitmap icon_bitmap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
//            getWindow().setStatusBarColor(Color.BLACK);
        }
        mExtStorDir = Environment.getExternalStorageDirectory().toString();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        deviceService = new DeviceService();
        orgsService = new OrgsService();
        initView();
        initData();
        initEvent();
        initGridView();
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
        type_ll = (LinearLayout)findViewById(R.id.device_type_ll);
        status_ll = (LinearLayout)findViewById(R.id.device_status_ll);
        belongSys_ll = (LinearLayout)findViewById(R.id.device_belongSys_ll);
        rele_gv = (GridView) findViewById(R.id.rele_device_gv);
        rele_device_iv = (ImageView) findViewById(R.id.rele_device_iv);
        rele_temp_list = new ArrayList<>();
        rele_dList = new ArrayList<>();
        rele_delete_list = new ArrayList<>();
    }

    /**
     * 初始化et数据
     */
    private void initData() {
        orgList = orgsService.loadNames();
        for (SpOrgnization org : orgList) {
            sysMap.put(org.getName(), org.getmId());
        }
        systems = new String[orgList.size()];
        systemIds = new String[orgList.size()];
        names = orgList.stream().map(SpOrgnization::getName).collect(Collectors.toList());
        mIds = orgList.stream().map(SpOrgnization::getmId).collect(Collectors.toList());
        names.toArray(systems);
        mIds.toArray(systemIds);

        mid = getIntent().getStringExtra("mid");
        add = getIntent().getBooleanExtra("add",false);
        if (!add) {
            device = deviceService.getDeviceByMid(mid);
            if (device != null) {
                name_Et.setText(device.getName());
                type_Et.setText(String.valueOf(device.getType()));
                location_Et.setText(device.getLocation());
                count_Et.setText(String.valueOf(device.getCount()));
                status_Et.setText(String.valueOf(device.getStatus()));
//            belongSys_Et.setText(orgsService.getName(device.getBelongSys()));
                LogUtil.i("value",String.valueOf(mIds.indexOf(device.getBelongSys())));
                if (mIds.indexOf(device.getBelongSys()) >= 0) {
                    belongSys_Et.setText(names.get(mIds.indexOf(device.getBelongSys())));
                    belongSys_Value = device.getBelongSys();
                }
                createName_Et.setText(device.getCreaterName());
                if (device.getCreateTime() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    createTime_Et.setText(sdf.format(device.getCreateTime()));
                }
                description_Et.setText(device.getDescription());
                head_tv.setText("设备详情");
                delete_tv.setVisibility(VISIBLE);
                if (device.getImage() != null) {
                    byte[] bytes = device.getImage();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    headImage.setImageBitmap(bitmap);
                }
            }

        }else {
            SharedPreferences setting = getSharedPreferences("User", 0);
            String name = setting.getString("name","");
            device = new DeviceInfo();
            device.setmId(mid);
            belongSys_Et.setText(orgList.get(0).getName());
            belongSys_Value = orgList.get(0).getmId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            createTime_Et.setText(sdf.format(new Date()));
            createName_Et.setText(name);
            delete_tv.setVisibility(GONE);
        }

    }



    /**
     * 设置关联设备GridView
     */
    private void initGridView() {
        rele_dList = deviceService.getDeviceReleList(mid); //加载关联设备
        set_GridView();
        releAdapter = new ReleAdapter(getApplicationContext(),
                rele_dList, rele_gv);
        rele_gv.setAdapter(releAdapter);
        releAdapter.setOnListClickListener(new ReleAdapter.OnListClickListener() {
            @Override
            public void onClick(DeviceRele rele, int position) {
                if (mid.equals(rele.getReleDeviceId())) return;

                Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
//                intent.putExtra("device",node);
                intent.putExtra("mid", rele.getReleDeviceId());
                intent.putExtra("releMid", rele.getDeviceId());
                intent.putExtra("add", false);
                startActivityForResult(intent,1);
            }

            @Override
            public void onLongClick(DeviceRele deviceRele, int position) {
                deleteItem(deviceRele.getId(), position);
            }
        });
    }


    private void set_GridView() {
        int size = rele_dList.size();
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        rele_gv.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        rele_gv.setColumnWidth(itemWidth); // 设置列表项宽
        rele_gv.setHorizontalSpacing(5); // 设置列表项水平间距
        rele_gv.setStretchMode(GridView.NO_STRETCH);
        rele_gv.setNumColumns(size); // 设置列数量=列表集合数
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
        rele_device_iv.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_course_btn :
                if (!add) {
                    Intent intent = new Intent(this,CourseActivity.class);
                    intent.putExtra("deviceId", device.getmId());
                    intent.putExtra("name",device.getName());
                    startActivityForResult(intent,1);
                }else {
                    ToastUtils.showShort("请先保存该设备信息！");
                }

                break;
            case R.id.id_device_image :
                popView(CONTENT_ICON, icons);
                break;
            case R.id.bar_back_iv :
                this.finish();
                break;
            case R.id.device_delete :
                deleteItem(0L,0);
                break;
            case R.id.device_save_btn :
                save();
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
            case R.id.rele_device_iv :
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("deviceId", mid);
                intent.putExtra("is_rele", true);
                startActivityForResult(intent, 1);
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

                    ImageUtil imageUtil = new ImageUtil(this);
                    icon_bitmap = imageUtil.imageZoom(bitmap,30.00);  //图片压

                    setImageToHeadView(intent,icon_bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case Constant.CODE_PHOTO_REQUEST:
                List<String> paths = intent.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                String src = paths.get(0);
                Bitmap bitmap = BitmapFactory.decodeFile(src);
                ImageUtil imageUtil = new ImageUtil(this);
                icon_bitmap = imageUtil.imageZoom(bitmap,30.00);  //图片压
                setImageToHeadView(intent,icon_bitmap);
                break;
        }
        String releDeviceId = null;
        if (resultCode == RESULT_FIRST_USER) {
            releDeviceId  = intent.getStringExtra("releDeviceId");
                DeviceRele rele = new DeviceRele(mid, releDeviceId);
                rele_temp_list.add(rele);
                rele_dList.add(0,rele);
//            rele_dList = deviceService.getDeviceReleList(mid);
                set_GridView();
                releAdapter.onDateChange(rele_dList);
        }
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
                    int i = names.indexOf(list[which]);
                    belongSys_Value = mIds.get(i);
                    LogUtil.i("value",belongSys_Value);
                }));
                break;
            case CONTENT_ICON :
                builder.setTitle("设置设备图片");
                builder.setItems(list, ((dialog, which) -> {
                    changeIcon(which);
                }));
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

    //删除指定item
    private void deleteItem(long id, int position) {
        String title;
        if(id == 0L) {
            title = "删除本条设备信息";
        } else {
            title = "删除本条关联信息";
        }
        AlertDialog deleteDialog = new AlertDialog.Builder(this,R.style.appalertdialog)
                .setMessage(title)
                .setPositiveButton("删除", (dialog, which) -> {
                    if (id == 0L) {
                        deviceService.delete(device.getId());
                        CourseService courseService = new CourseService();
                        courseService.deleteByDid(device.getmId());
                        Intent intent1 = new Intent();
                        setResult(-3, intent1);
                        finish();
                    } else {
                        rele_delete_list.add(rele_dList.get(position));
                        rele_dList.remove(position);
                        releAdapter.removeData(rele_dList);
                    }

                })
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .create();
        deleteDialog.show();
        //修改Message字体颜色
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(deleteDialog);
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextColor(Color.BLACK);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
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

    private void save() {
        String temp = name_Et.getText().toString().trim();
        if (temp.equals("")) {
            ToastUtils.showShort("装备名称不能为空");
            return;
        }

        if (add) {
            boolean flag = addDevice();
            if (flag) {
                Intent intent1 = new Intent();
                setResult(-3, intent1);
                finish();
                ToastUtils.showShort("保存本条信息成功");
            }else {
                ToastUtils.showShort("保存本条信息失败");
            }
        } else { //更新操作
            int a = updateDevice();
            Intent intent1 = new Intent();
            setResult(-3, intent1);
            finish();
            ToastUtils.showShort("更新本条信息成功");
        }

        if (rele_temp_list.size() > 0) {
                rele_temp_list.stream().forEach( rele -> {
                    deviceService.releDevice(rele.getDeviceId(), rele.getReleDeviceId());
                });
        }
        if (rele_delete_list.size() > 0) {
            rele_delete_list.stream().forEach(rele -> {
                    deviceService.unReleDevice(rele.getId());
            });
        }
    }

    /**
     * 保存装备信息
     * @return
     */
    public boolean addDevice() {
        device.setName(name_Et.getText().toString());
        device.setType(type_Et.getText().toString());
        device.setLocation(location_Et.getText().toString());
        if (!count_Et.getText().toString().equals("")){
            device.setCount(Integer.valueOf(count_Et.getText().toString()));
        }

        device.setStatus(status_Et.getText().toString());
//        device.setBelongSys(belongSys_Et.getText().toString());
        device.setBelongSys(belongSys_Value);
        device.setCreaterName(createName_Et.getText().toString());
        device.setDescription(description_Et.getText().toString());
        device.setCreateTime(new Date());
        device.setValid(true);
//        headImage.setDrawingCacheEnabled(true);
//        Bitmap bitmap = Bitmap.createBitmap(headImage.getDrawingCache());
//        headImage.setDrawingCacheEnabled(false);

//        ImageUtil imageUtil = new ImageUtil(this);
////        bitmap = imageUtil.imageZoom(bitmap,30.00);  //图片压缩

        if (icon_bitmap != null) {
            ImageUtil imageUtil = new ImageUtil(this);
            byte[] images = imageUtil.imageToByte(icon_bitmap);
            device.setImage(images);
        }

        return deviceService.insert(device);
    }

    /**
     * 更新装备信息
     * @return
     */
    public int updateDevice() {
        device.setName(name_Et.getText().toString());
        device.setType(type_Et.getText().toString());
        device.setLocation(location_Et.getText().toString());
        if (!count_Et.getText().toString().equals("")){
            device.setCount(Integer.valueOf(count_Et.getText().toString()));
        }
        device.setStatus(status_Et.getText().toString());
        device.setCreaterName(createName_Et.getText().toString());
//        device.setBelongSys(sysMap.get(belongSys_Et.getText().toString()));
//        device.setBelongSys(belongSys_Et.getText().toString());
        device.setBelongSys(belongSys_Value);
        device.setDescription(description_Et.getText().toString());
        device.setCreateTime(new Date());
        device.setValid(true);
//        headImage.setDrawingCacheEnabled(true);
//        Bitmap bitmap = Bitmap.createBitmap(headImage.getDrawingCache());
//        headImage.setDrawingCacheEnabled(false);

//        bitmap = imageUtil.imageZoom(bitmap,30.00);  //图片压缩
        if (icon_bitmap != null) {
            ImageUtil imageUtil = new ImageUtil(this);
            byte[] images = imageUtil.imageToByte(icon_bitmap);
            device.setImage(images);
        }
        return deviceService.update(device);
    }




}