package com.cec.zbgl.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cec.zbgl.R;
import com.cec.zbgl.activity.ContentActivity;
import com.cec.zbgl.adapter.DeviceRecyclerAdapter;
import com.cec.zbgl.adapter.SimpleTreeListViewAdapter;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


public class DiscoverFragment extends Fragment {

    private ListView mTreeView;
    private SimpleTreeListViewAdapter<SpOrgnization> mAdapter;
    private List<SpOrgnization> orgs;
    private RecyclerView mRecyclerView;
    private DeviceRecyclerAdapter deviceAdapter;
    private List<DeviceInfo> devices;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab03, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
    }


    /**
     * 初始化界面view,并绑定相关adapter
     */
    public void initView() {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        deviceAdapter = new DeviceRecyclerAdapter(getContext());
        mRecyclerView.setAdapter(deviceAdapter);


        initDatas();
        //初始化左侧机构树
        mTreeView = (ListView) getView().findViewById(R.id.id_lv_tree);
        try
        {
            mAdapter = new SimpleTreeListViewAdapter<>(mTreeView, getContext(),
                    orgs, 0);
            mTreeView.setAdapter(mAdapter);

        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                return 1;
            }
        });

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = layoutParams.getSpanSize();
                int spanIndex = layoutParams.getSpanIndex();
                outRect.top = 20;
                if (spanSize != gridLayoutManager.getSpanCount()) {
                    if (spanIndex == 1) {
                        outRect.left = 10;
                    }else {
                        outRect.right = 10;
                    }
                }
            }
        });


    }

    /**
     * 初始化模拟数据
     */
    private void initDatas() {
        orgs = new ArrayList<>();
        SpOrgnization org = new SpOrgnization("1","0","根目录1");
        orgs.add(org);
        org = new SpOrgnization("2","0","根目录2");
        orgs.add(org);
        org = new SpOrgnization("3","0","根目录3");
        orgs.add(org);
        org = new SpOrgnization("4","1","根目录1-1");
        orgs.add(org);
        org = new SpOrgnization("5","1","根目录1-2");
        orgs.add(org);

        devices = new ArrayList<>();
        for (int i=0; i< 40; i++) {
            String name,typyName,sys;
            int type;
            if (i <15 ) {
                name = "服务器 " + i;
                typyName = "服务器设备";
                sys = "综保系统";
            }else if (15<=i && i<= 31) {
                name = "路由器 " + i;
                typyName = "复制设备";
                sys = "业务系统";
            } else {
                name = "笔记本电脑 "+ i;
                typyName = "便携设备";
                sys = "后勤系统";
            }

            DeviceInfo device = new DeviceInfo(name, sys, "设备描述为:"+typyName );
            devices.add(device);
        }
        deviceAdapter.addList(devices);
        deviceAdapter.notifyDataSetChanged();

    }


    /**
     * 点击左侧树节点，刷新右侧tableView
     */
    private void initEvent() {
        mAdapter.setOnTreeNodeClickListener((node, position) -> {
            if (node.isLeaf())
            {
//                reloadData(node.getName());
                ToastUtils.showShort(node.getName());
            }
        });

        deviceAdapter.setOnListClickListener(new DeviceRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = mRecyclerView.getChildAdapterPosition(view);
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.putExtra("device",devices.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view) {
                int position = mRecyclerView.getChildAdapterPosition(view);
                ToastUtils.showShort("onItemLongClick: " + position);
            }
        });

    }
}
