package com.cec.zbgl.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.CourseAdapter;
import com.cec.zbgl.adapter.CourseAddAdapter;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.listener.ItemClickListener;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.utils.ImageUtil;
import com.cec.zbgl.utils.OpenFileUtil;
import com.cec.zbgl.utils.ToastUtils;
import com.cec.zbgl.utils.VideoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean isShowing = false;
    private ImageUtil imageUtil;
    private File tempFile;
    private String imgName;
    private String videoName;

    private int IS_TITLE_OR_NOT =1;
    private int MESSAGE = 2;


    private List<DeviceCourse> mData =new ArrayList<>();
    private Map<Integer, DeviceCourse> map = new HashMap<Integer, DeviceCourse>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        initData();
        initView();
        initEvent();
    }

    private void initView() {
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        add_iv = (ImageView) findViewById(R.id.device_media_add);
        add_iv.setVisibility(VISIBLE);
        mAdapter = new CourseAdapter(this,mData);
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
        list.add("导入文档");

        addAdapter = new CourseAddAdapter(R.layout.course_add,this,list,course_lv);
        course_lv.setAdapter(addAdapter);
        marsker_tv = (TextView) findViewById(R.id.course_masker);
        marsker_tv.bringToFront();
        course_lv.bringToFront();

    }

    private void initData() {

        head_tv.setText(getIntent().getStringExtra("name"));

        for (int i=0; i< 25; i++) {
            String name,typyName;
            int type;
            if (i < 9) {
                type = 1;
                name = "图片教程";
                typyName = "图片";
            }else if (i < 18){
                type = 2;
                name = "视频教程";
                typyName = "视频";
            } else {
                type = 3;
                name = "文档教程";
                typyName = "文档";
            }
            map = new HashMap<Integer, DeviceCourse>();
            DeviceCourse c0 = new DeviceCourse(String.valueOf(i),name,type,
                    "教程类别为:"+typyName,"false","item "+(i+1));
            mData.add(c0);
        }
        //对分类标题进行初始化
        DeviceCourse c1 = new DeviceCourse("true","图片教程",101);
        mData.add(0,c1);

        DeviceCourse c2 = new DeviceCourse("true","视频教程",101);
        mData.add( 10,c2);

        DeviceCourse c3 = new DeviceCourse("true","文档教程",101);
        mData.add(20,c3);

    }

    private void initEvent() {

        add_iv.setOnClickListener(this);

        back_iv.setOnClickListener(this);

        marsker_tv.setOnClickListener(this);

        mAdapter.setOnListClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                DeviceCourse c = mData.get(position);
                String fileName;
                Intent intent;
                switch (mData.get(position).getCourseType()) {
                    case 1 :
//                        fileName = "/storage/emulated/0/DCIM/Camera/IMG_20181030_113211.jpg";
                        fileName = "android.resource://" + getPackageName()
                                + "/" + R.mipmap.nba;
                        intent = OpenFileUtil.getImageFileIntent(fileName);
                        startActivity(intent);

                        ToastUtils.showShort("展示图片");
                        break;
                    case 2 :
                        System.out.println(getPackageName());
                        intent = new Intent(CourseActivity.this, MediaActivity.class);
                        startActivity(intent);
                        break;
                    case 3 :
//                        fileName = "storage/emulated/0/Download/11111.txt";
                        fileName = "android.resource://" + getPackageName()
                                + "/" + R.raw.demo3;
                        intent = OpenFileUtil.getTextFileIntent(fileName,false);
                        startActivity(intent);
                        ToastUtils.showShort("查看文档");
                        break;
                }

            }

            @Override
            public void onItemLongClick(View v, int position) {
                ToastUtils.showShort("onItemLongClick: "+mData.get(position).toString() + "-"+position);
            }
        });

        addAdapter.setOnListClickListener(position -> {
            switch (position) {
                case 0:
                    imgName = "1108";
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                            new File(Environment.getExternalStorageDirectory(),imgName)
                    ));
                    startActivityForResult(cameraIntent, Constant.CODE_CAMERA_REQUEST);
                    break;
                case 1:
                    videoName = "1108";
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (videoIntent.resolveActivity(getPackageManager()) != null) {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                                new File(Environment.getExternalStorageDirectory(),videoName)
                        ));
                        startActivityForResult(videoIntent,Constant.CODE_VIDEO_REQUEST);
                    }
                    break;
                case 2:
                    ToastUtils.showShort("导入文档");
                    break;
            }
            disappear();
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.CODE_CAMERA_REQUEST:
                tempFile = new File(Environment.getExternalStorageDirectory(), imgName);
                break;
            case Constant.CODE_VIDEO_REQUEST:
                Uri videoUri = data.getData();
                String path = VideoUtils.getPath(this, videoUri);
                boolean exits = new File(path).exists();
                ToastUtils.showShort("拍摄成功: " + path);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_back_iv :
                finish();
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


    //设置recyclerView中item的上下左右间距
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

}
