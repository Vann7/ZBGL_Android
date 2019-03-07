package com.cec.zbgl.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cec.zbgl.BuildConfig;
import com.cec.zbgl.R;
import com.cec.zbgl.adapter.CourseAdapter;
import com.cec.zbgl.adapter.CourseAddAdapter;
import com.cec.zbgl.adapter.ImageAdapter;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.listener.ItemClickListener;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.User;
import com.cec.zbgl.service.CourseService;
import com.cec.zbgl.service.OrgsService;
import com.cec.zbgl.transformer.ScalePageTransformer;
import com.cec.zbgl.utils.ImageUtil;
import com.cec.zbgl.utils.OpenFileUtil;
import com.cec.zbgl.utils.ToastUtils;
import com.cec.zbgl.view.MatrixImageView;
import com.cec.zbgl.view.ScaleView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.MultiImageSelectorFragment;
import me.nereo.multi_image_selector.MultiVideoSelectorActivity;
import me.nereo.multi_image_selector.bean.Video;
import me.nereo.multi_image_selector.utils.FileUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CourseActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView back_iv;
    private TextView head_tv;
    private RecyclerView mRecyclerView ;
    private ImageView add_iv;
    private CourseAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ListView course_lv;
    private TextView marsker_tv;

    private CourseAddAdapter addAdapter;
    private ProgressBar progressBar; //加载按钮
    private boolean isShowing = false;
    private ImageUtil imageUtil;
    private Uri mUriPath;
    private String videoName;
    private AlertDialog alertDialog;
    private String imageName;
    private File mTmpFile;
    private File mVideoFile;
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 110;
    private static final int REQUEST_CAMERA = 100;

    private int IS_TITLE_OR_NOT =1;
    private int MESSAGE = 2;
    private long sizeLimit = 1024*1024*1024L;//(1GB视频大小限制)
    private final int CONS_IMAGE = 0;
    private final int CONS_VIDEO = 1;
//    private final int CONS_DOCUMENT = 2;
    private final int CONS_LEAD_PHOTO = 2;
    private final int CONS_LEAD_VIDEO = 3;
    private int mGridWidth;
    private CourseService courseService;
    private String deviceId; //装备id
    private String sysId; //组织机构id
    private String sysName; //组织机构名称
    private User user;

    private List<DeviceCourse> mData =new ArrayList<>();
    private List<DeviceCourse> mData_Imag =new ArrayList<>();
    private List<DeviceCourse> mData_Video =new ArrayList<>();
    private List<DeviceCourse> mData_Doc =new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
        }
        //x 将屏幕设置为竖屏()
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        courseService = new CourseService();
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        deviceId = getIntent().getStringExtra("deviceId");
        getSession();
        initWidth();
        initView();
        initEvent();
        initData();
    }




    private void initWidth() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }else{
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width;
    }

    private void initView() {

        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        add_iv = (ImageView) findViewById(R.id.device_media_add);
        if (user.isAppUpdate()) {
            add_iv.setVisibility(VISIBLE);
        }
        mAdapter = new CourseAdapter(this,mData);
        progressBar = (ProgressBar) findViewById(R.id.course_progressBar);
        progressBar.bringToFront();
        mGridLayoutManager = new GridLayoutManager(this, 8);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                if (type == DeviceCourse.TYPE_ONE) {
                    return 2;
                }else if (type == DeviceCourse.TYPE_TWO){
                    return 4;
                } else {
                    return 8;
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.course_rv);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(12));//item之间的间距

        course_lv = (ListView) findViewById(R.id.course_lv);

        List<String> list = new ArrayList<>();
        list.add("拍摄照片");
        list.add("拍摄视频");
//        list.add("导入文档");
        list.add("导入照片");
        list.add("导入视频");

        addAdapter = new CourseAddAdapter(R.layout.course_add,this,list,course_lv);
        course_lv.setAdapter(addAdapter);
        marsker_tv = (TextView) findViewById(R.id.course_masker);
        marsker_tv.bringToFront();
        course_lv.bringToFront();
    }

    /**
     * 初始化教程数据
     */
    private void initData() {
        OrgsService orgsService = new OrgsService();
        deviceId = getIntent().getStringExtra("deviceId");

        sysId = getIntent().getStringExtra("sysId");
        if (sysId != null) {
            sysName = orgsService.getName(sysId);
        }
        String name = getIntent().getStringExtra("name");
        if (name != null){
            head_tv.setText(name + " — 详情信息");
        } else {
            head_tv.setText(sysName + " — 详情信息");
        }

        if (mData.size() != 0) {
            mData = new ArrayList<>();
        }

        if(deviceId != null) {
            mData_Imag = courseService.loadByDid(deviceId, Constant.COURSE_IMAGE);
            mData_Video = courseService.loadByDid(deviceId, Constant.COURSE_VIDEO);
            mData_Doc = courseService.loadByDid(deviceId, Constant.COURSE_DOCUMENT);
        }

        if (sysId != null) {
            mData_Imag = courseService.loadBySysid(sysId, Constant.COURSE_IMAGE);
            mData_Video = courseService.loadBySysid(sysId, Constant.COURSE_VIDEO);
            mData_Doc = courseService.loadBySysid(sysId, Constant.COURSE_DOCUMENT);
        }

        //对分类标题进行初始化
        DeviceCourse c1 = new DeviceCourse(true,"图片教程",101);
        mData_Imag.add(0,c1);
        DeviceCourse c2 = new DeviceCourse(true,"视频教程",101);
        mData_Video.add( 0,c2);
        DeviceCourse c3 = new DeviceCourse(true,"文档教程",101);
        mData_Doc.add(0,c3);

        mData.addAll(mData_Imag);
        mData.addAll(mData_Video);
        mData.addAll(mData_Doc);

    }

    private void initEvent() {

        add_iv.setOnClickListener(this);

        back_iv.setOnClickListener(this);

        marsker_tv.setOnClickListener(this);

        mAdapter.setOnListClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                DeviceCourse c = mData.get(position);

                Intent intent;
                switch (mData.get(position).getCourseType()) {
                    case Constant.COURSE_IMAGE :
                        intent = new Intent(CourseActivity.this, PhotoActivity.class);
                        intent.putExtra("position", position );
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("sysId", sysId);
//                        startActivity(intent);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CourseActivity.this).toBundle());
                        break;
                    case Constant.COURSE_VIDEO :
                        File file = new File(mData.get(position).getLocation());
                        if (!file.exists()) {
                            ToastUtils.showShort("该教程视频不存在，请检查!");
                            return;
                        }
                        intent = new Intent(CourseActivity.this, MediaActivity.class);
                        intent.putExtra("id", mData.get(position).getId());
                        startActivity(intent);
//                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CourseActivity.this).toBundle());
                        break;
                    case Constant.COURSE_DOCUMENT :
                        DeviceCourse c0 = mData.get(position);
                        openDoc(c0);
                        break;
                }

            }



            //长按item 删除指定条目item
            @Override
            public void onItemLongClick(View v, int position) {


                switch (mData.get(position).getCourseType()) {
                    case Constant.COURSE_IMAGE :
//
                        break;
                    case Constant.COURSE_VIDEO :

                        break;
                    case Constant.COURSE_DOCUMENT :
//
                        break;
                }
                if (user.isAppUpdate()) {
                    deleteItem(position);
                }
            }
        });

        addAdapter.setOnListClickListener(position -> {
            switch (position) {
                case CONS_IMAGE:
                   takePhoto();
                    break;
                case CONS_VIDEO :
                    takeVideo();
                    break;
              /*  case CONS_DOCUMENT:
                    DeviceCourse c1 = new DeviceCourse();
                    c1.setCourseType(Constant.COURSE_DOCUMENT);
                    c1.setDeviceId(deviceId);
                    c1.setSysId(sysId);
                    c1.setmId(UUID.randomUUID().toString());
                    c1.setValid(true);
                    c1.setCreaterName(user.getName());
                    c1.setCreaterId(user.getmId());
                    c1.setCreateTime(new Date());
                    String filePath = Environment.getExternalStorageDirectory().getPath().concat("/Documents/");
                    c1.setName("a.doc");
                    c1.setLocation(filePath+"a.doc");
                    courseService.insert(c1);
                    break;*/
                case CONS_LEAD_PHOTO :
                    pickPhoto();
                    break;
                case CONS_LEAD_VIDEO :
                    pickVideo();
                    break;
            }
            disappear();
        });

    }

    /**
     * 通过第三方app打开文档教程
     * @param course
     */
    private void openDoc(DeviceCourse course) {
//        String filepath = course.getLocation();
        String filepath = Environment.getExternalStorageDirectory().getPath().concat("/Documents/");
        String fileName = course.getLocation();
        File file = new File(filepath+fileName);
        Uri uri;
        if (!file.exists()) {
            ToastUtils.showShort("本地文件不存在!");
            return;
        }
        String type = course.getLocation().substring(fileName.lastIndexOf("."),fileName.length());
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider",file);
        } else {
            uri = Uri.fromFile(file);
        }

        switch (type) {
            case ".doc" :
                intent.setDataAndType(uri,"application/msword");
                break;
            case ".docx" :
                intent.setDataAndType(uri,"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                break;
            case ".pdf" :
                intent.setDataAndType(uri,"application/pdf");
                break;
            default:
                ToastUtils.showShort("该类型文件无法打开!");
                break;
        }

        final PackageManager packageManager = this.getPackageManager();
        @SuppressLint("WrongConstant") List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        if (list.size() == 0) ToastUtils.showShort("请安装office及相关软件!");
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constant.CODE_CAMERA_REQUEST: //拍照 回调存储
                new TakePhotoTask().execute();
                break;
            case Constant.CODE_VIDEO_REQUEST: //拍摄视频 回调存储
                new TakeVideoTask().execute(this);
                break;
            case Constant.CODE_PHOTO_REQUEST:  //相册获取照片 回调存储
                List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                new PickPhotoTask().execute(paths);
                break;
            case Constant.CODE_PICK_VIDEO_REQUEST: //相册获取视频 回调存储
//                List<Video> videos = data.getStringArrayListExtra(MultiVideoSelectorActivity.EXTRA_RESULT);

                List<String> paths2 = data.getStringArrayListExtra(MultiVideoSelectorActivity.EXTRA_RESULT);
                new PickVideoTask().execute(paths2);
                break;
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_back_iv :
                finish();
                overridePendingTransition(R.anim.dd_mask_in, R.anim.dd_mask_out);
                break;
            case R.id.device_media_add :
                isShowing = (isShowing == false) ? true : false;
                if (isShowing == false) {
                    disappear();
                }else {
                    appear();
                }
                break;
            case R.id.course_masker :
                disappear();
                break;
        }
    }

    private void appear() {
        marsker_tv.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dd_mask_in));
        marsker_tv.setVisibility(VISIBLE);
        course_lv.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dd_mask_in));
        course_lv.setVisibility(VISIBLE);
    }

    private void disappear() {
        course_lv.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dd_mask_out));
        marsker_tv.setVisibility(GONE);
        course_lv.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dd_mask_out));
        course_lv.setVisibility(GONE);
    }


    /**
     * 拍摄照片
     */
    private void takePhoto() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(me.nereo.multi_image_selector.R.string.mis_permission_rationale_write_storage),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(this.getPackageManager()) != null) {
                try {
                    mTmpFile = FileUtils.createTmpFile(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mTmpFile != null && mTmpFile.exists()) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                    startActivityForResult(intent, Constant.CODE_CAMERA_REQUEST);
                } else {
                    Toast.makeText(this, me.nereo.multi_image_selector.R.string.mis_error_image_not_exist, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, me.nereo.multi_image_selector.R.string.mis_msg_no_camera, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 相册选取照片(支持拍照)
     */
    private void pickPhoto() {
        ArrayList<String> defaultDataArray = new ArrayList<>();
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
        startActivityForResult(intent, Constant.CODE_PHOTO_REQUEST);
    }


    /**
     * 相册选取视频
     */
    private void pickVideo() {
        ArrayList<String> defaultDataArray = new ArrayList<>();
        Intent intent = new Intent(this, MultiVideoSelectorActivity.class);
        intent.putExtra(MultiVideoSelectorActivity.EXTRA_SELECT_COUNT, 9);
        intent.putExtra(MultiVideoSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        intent.putStringArrayListExtra(MultiVideoSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
        startActivityForResult(intent, Constant.CODE_PICK_VIDEO_REQUEST);
    }


    /**
     * 拍摄视频
     */
    private void takeVideo() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(me.nereo.multi_image_selector.R.string.mis_permission_rationale_write_storage),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        }else {
            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (videoIntent.resolveActivity(this.getPackageManager()) != null) {
                try {
                    mVideoFile = com.cec.zbgl.utils.FileUtils.createMediaFile(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mVideoFile != null && mVideoFile.exists()) {
                    videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mVideoFile));
                    videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 5);
                    videoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, sizeLimit);
                    startActivityForResult(videoIntent,Constant.CODE_VIDEO_REQUEST);
                }
            }
        }
    }



    /**
     *  设置recyclerView中item的上下左右间距
     */
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //分别设置item的间距
            if (parent.getChildViewHolder(view).getItemViewType() == 0) {
                outRect.bottom = 0;
                outRect.top = space / 2;
            } else {
                outRect.bottom = space;
                outRect.top = space;
            }
            outRect.right = space;
            outRect.left = space;

        }
    }


    /**
     * 删除指定item
     * @param position
     */
    private void deleteItem(int position) {
        alertDialog = new AlertDialog.Builder(this,R.style.appalertdialog)
                .setMessage("删除本条教程")
                .setPositiveButton("删除", (dialog, which) -> {
                    courseService.delete(mData.get(position).getId());
                    int count = mData_Imag.size() + mData_Video.size();
                    if (position < mData_Imag.size()) {
                        mData_Imag.remove(position);
                    } else if (position < count){
                        mData_Video.remove(position - mData_Imag.size());
                    }
                    mAdapter.removeData(position);
                })
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .create();
        alertDialog.show();
        //修改Message字体颜色
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
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

    /**
     * 权限请求
     * @param permission
     * @param rationale
     * @param requestCode
     */
    private void requestPermission(final String permission, String rationale, final int requestCode){
        if(shouldShowRequestPermissionRationale(permission)){
            new AlertDialog.Builder(this)
                    .setTitle(me.nereo.multi_image_selector.R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(me.nereo.multi_image_selector.R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(me.nereo.multi_image_selector.R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        }else{
            requestPermissions(new String[]{permission}, requestCode);
        }
    }


    public void collectData() {
        mData.clear();
        mData.addAll(mData_Imag);
        mData.addAll(mData_Video);
        mData.addAll(mData_Doc);
        mAdapter.onDateChange(mData);
    }

    /**
     * 获取当前用户session信息
     */
    private void getSession() {
        SharedPreferences setting = this.getSharedPreferences("User", 0);
        user = new User(setting.getString("name",""),setting.getString("password",""));
        user.setId(Integer.valueOf(setting.getString("id","0")));
        user.setAppUpdate(Boolean.valueOf(setting.getBoolean("appUpdate", false)));
    }




    /**************************************内部类****************************************/

    /**
     * 选取照片异步任务
     */
    class PickPhotoTask extends AsyncTask<List<String>, Void, List<DeviceCourse>> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(VISIBLE);
        }

        //在doInBackground方法中进行异步任务的处理.
        @Override
        protected List<DeviceCourse> doInBackground(List<String>... lists) {

            for (int i=0; i< lists[0].size(); i++) {
                DeviceCourse c0 = new DeviceCourse(UUID.randomUUID().toString(),"text123",
                        Constant.COURSE_IMAGE,"教程类别为:"+"图片",false,"item "+(121+1));
                c0.setLocation(lists[0].get(i));

                Bitmap bitmap = BitmapFactory.decodeFile(lists[0].get(i));
                ImageUtil imageUtil = new ImageUtil();
                Bitmap bitmap_small = imageUtil.imageZoom(bitmap,80.00);  //图片压缩
                Bitmap bitmap_full = imageUtil.imageZoom(bitmap,200.00);  //图片压缩
                byte[] images_small = imageUtil.imageToByte(bitmap_small);
                byte[] images_full = imageUtil.imageToByte(bitmap_full);
                c0.setImage(images_small);
                c0.setImage_full(images_full);
                c0.setDeviceId(deviceId);
                c0.setSysId(sysId);
                c0.setValid(true);
                c0.setCreaterName(user.getName());
                c0.setCreaterId(user.getmId());
                c0.setCreateTime(new Date());
                courseService.insert(c0);
                mData_Imag.add(1,c0);
            }
            return mData_Imag;
        }

        //onPostExecute用于UI的更新.此方法的参数为doInBackground方法返回的值.
        @Override
        protected void onPostExecute(List<DeviceCourse> mData_Imag) {
            progressBar.setVisibility(GONE);
            collectData();

//            mImageAdapter.onDateChange(getPages());
        }
    }

    /**
     * 拍摄照片异步任务
     */
    class TakePhotoTask extends AsyncTask<DeviceCourse, Void,Void> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(VISIBLE);
        }

        @Override
        protected Void doInBackground(DeviceCourse... deviceCourses) {

            if (mTmpFile != null) {
                // notify system the image has change
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTmpFile)));
                DeviceCourse c0 = new DeviceCourse(UUID.randomUUID().toString(),"text123",
                        Constant.COURSE_IMAGE,"教程类别为:"+"图片",false,"item "+(121+1));
                c0.setLocation(mTmpFile.getAbsolutePath());
                Bitmap bitmap = BitmapFactory.decodeFile(mTmpFile.getAbsolutePath());
                ImageUtil imageUtil = new ImageUtil();
                Bitmap bitmap_small = imageUtil.imageZoom(bitmap,20.00);  //图片压缩
                Bitmap bitmap_full = imageUtil.imageZoom(bitmap,200.00);  //图片压缩
                byte[] images_small = imageUtil.imageToByte(bitmap_small);
                byte[] images_full = imageUtil.imageToByte(bitmap_full);
                c0.setImage(images_small);
                c0.setImage_full(images_full);
                c0.setDeviceId(deviceId);
                c0.setCreaterName(user.getName());
                c0.setCreaterId(user.getmId());
                c0.setCreateTime(new Date());
                c0.setSysId(sysId);
                c0.setValid(true);
                courseService.insert(c0);
                mData_Imag.add(1,c0);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(GONE);
            collectData();
        }
    }


    /**
     * 选取视频异步任务
     */
    class PickVideoTask extends AsyncTask<List<String>, Void, Void> {



        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(VISIBLE);
        }

        @Override
        protected Void doInBackground(List<String>... lists) {
            List<String> paths = lists[0];
            for (String result : paths) {
               String[] strs = result.split(" ");
                String path = strs[0];
                String vId = strs[1];
                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(),
                        Integer.valueOf(vId), MediaStore.Video.Thumbnails.MINI_KIND, null);
                DeviceCourse c0 = new DeviceCourse(UUID.randomUUID().toString(),"video",
                        Constant.COURSE_VIDEO,"教程类别为:"+"视频",false,"item");
                c0.setLocation(path);
                ImageUtil imageUtil = new ImageUtil();
                if (bitmap != null) {
                    byte[] images_small = imageUtil.imageToByte(bitmap);
                    c0.setImage(images_small);
                }
                c0.setDeviceId(deviceId);
                c0.setCreaterName(user.getName());
                c0.setCreaterId(user.getmId());
                c0.setCreateTime(new Date());
                c0.setSysId(sysId);
                c0.setValid(true);
                courseService.insert(c0);
                mData_Video.add(1,c0);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(GONE);
            collectData();
        }
    }


    /**
     * 拍摄视频异步任务
     */
    class TakeVideoTask extends AsyncTask<Context, Void, Void>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(VISIBLE);
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            Context context = contexts[0];
            if (mVideoFile != null) {
                // notify system the image has change
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mVideoFile)));
                DeviceCourse course = new DeviceCourse(UUID.randomUUID().toString(),"Vedio666",
                        Constant.COURSE_VIDEO,"教程类别为:"+"视频",false,"item "+(121+1));
                course.setLocation(mVideoFile.getAbsolutePath());
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(context,Uri.fromFile(mVideoFile));
                Bitmap bitmap = mmr.getFrameAtTime();
                ImageUtil imageUtil = new ImageUtil();
                byte[] images = imageUtil.imageToByte(bitmap);
                course.setImage(images);
                course.setDeviceId(deviceId);
                course.setSysId(sysId);
                course.setValid(true);
                course.setCreaterName(user.getName());
                course.setCreaterId(user.getmId());
                course.setCreateTime(new Date());
                courseService.insert(course);
                mData_Video.add(1,course);
                mmr.release();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(GONE);
            collectData();
        }
    }

}
