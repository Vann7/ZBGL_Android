package com.cec.zbgl.activity;


import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import com.cec.zbgl.R;
import com.cec.zbgl.adapter.DeviceRecyclerAdapter;
import com.cec.zbgl.listener.ILoadListener;
import com.cec.zbgl.listener.IReflashListener;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.utils.ToastUtils;
import com.cec.zbgl.view.MeRecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DemoActivity extends AppCompatActivity implements IReflashListener, ILoadListener ,View.OnClickListener {

    private MeRecyclerView mRecyclerView;
    private List<DeviceInfo> devices = new ArrayList<>();

    int page = 1;
    int size = 20;
    List<Map<String, Object>> dataList;
    DeviceRecyclerAdapter mAdapter;
    LinearLayoutManager layoutManager;
    GridLayoutManager gridLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mRecyclerView = (MeRecyclerView) findViewById(R.id.test123);
        dataList = new ArrayList<>();
        mAdapter = new DeviceRecyclerAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
//                if(mRecyclerView.isShowFooter()){
//                    return 2;
//                }else {
                    return 1;
//                }

            }
        });

        mRecyclerView.setLoadListener(this);
        mRecyclerView.setReflashListener(this);

//        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        initEvent();

    }

    private void initEvent() {
        mAdapter.setOnListClickListener(new DeviceRecyclerAdapter.OnItemClickListener() {


            @Override
            public void onItemClick(View v, int position) {

            }

            @Override
            public void onItemLongClick(View v, int position) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.startRefresh();
    }

    private void initData() {
        Random random = new Random();
        List<DeviceInfo> list = new ArrayList<>();
        for (int i=0; i< 4; i++) {
            String name,typyName;

                name = "图片教程";
                typyName = "图片";
                int r = random.nextInt(100);
            DeviceInfo device = new DeviceInfo(String.valueOf(r), name+": "+r,  "教程类别为:"+typyName );
            list.add(device);
        }

        update(list);
    }


    /**
     * 加载更多
     */
    @Override
    public void onload() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            page += 1;
            initData();
        },3000);
    }


    /**
     * 刷新数据
     */
    @Override
    public void onReflash() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            page = 1;
            initData();
        },1000);

    }



    private void update(List<DeviceInfo> maps) {
        if (maps.size() > 0) {
            if (page == 1) {
                maps.addAll(devices);
                mAdapter.pre_addList(maps);
                devices = maps;
            } else {
                mAdapter.addList(maps);
            }
            mRecyclerView.notifyDataSetChanged();
            mRecyclerView.stopRefresh(page, false);
        } else {
            mRecyclerView.stopRefresh(page, true);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
