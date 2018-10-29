package com.cec.zbgl.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.cec.zbgl.R;
import com.cec.zbgl.activity.ContentActivity;
import com.cec.zbgl.adapter.OrgsAdapter;
import com.cec.zbgl.adapter.RefreshAdapter;
import com.cec.zbgl.listener.ILoadListener;
import com.cec.zbgl.listener.IReflashListener;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.utils.ToastUtils;
import com.cec.zbgl.view.MeRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class DiscoverFragment extends Fragment implements View.OnClickListener {

    private ListView mTreeView;
    private OrgsAdapter<SpOrgnization> mAdapter;
    private List<SpOrgnization> orgs;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshAdapter mRefreshAdapter;
    private GridLayoutManager mGridLayoutManager;
    private List<DeviceInfo> devices = new ArrayList<>();
    private Handler handler = new Handler();
    private AppCompatTextView listTip; //没有数据


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
        initData();
        initListener();
        initEvent();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view2);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshLayout2);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.GREEN);

        listTip = (AppCompatTextView) getActivity().findViewById(R.id.list_tip_message2);

        //初始化左侧机构树
        mTreeView = (ListView) getView().findViewById(R.id.id_lv_tree);
        try
        {
            initOrgs();
            mAdapter = new OrgsAdapter<>(mTreeView, getContext(),
                    orgs, 0);
            mTreeView.setAdapter(mAdapter);
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void initOrgs() {
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
    }


    private void initData() {
        Random random = new Random();
        for (int i=0; i< 15; i++) {
            String name,typyName;
            name = "21装备";
            typyName = "笔记本电脑";
            int r = random.nextInt(100);
            DeviceInfo device = new DeviceInfo(String.valueOf(r), name+": "+r,  "系统装备类别为:"+typyName );
            devices.add(device);
        }
        initRecylerView();
        showData();
    }


    /**
     * 点击左侧树节点，刷新右侧tableView
     */
    private void initEvent() {
        mAdapter.setOnTreeNodeClickListener((node, position) -> {
            if (node.isLeaf())
            {
                reloadData(node.getName());
//                ToastUtils.showShort(node.getName());
            }
        });

        mRefreshAdapter.setOnListClickListener(new RefreshAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                DeviceInfo node = devices.get(position);
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.putExtra("device",node);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View v, int position) {
                ToastUtils.showShort(devices.get(position).toString() + "-"+position);
            }

        });

        listTip.setOnClickListener(this);

    }

    /**
     * 模拟重载数据
     */
    private void reloadData(String root) {

        Random random = new Random();
        List<DeviceInfo> checkDatas = new ArrayList<>();
        if (root != "根目录2"){
            for (int i=0; i< 15; i++) {

                String name,typyName;

                name = "XX装备数据";
                typyName = "网口";
                int r = random.nextInt(100);
                DeviceInfo device = new DeviceInfo(String.valueOf(r)+"-"+root, name+": "+r,  "装备类别为:"+typyName );
                checkDatas.add(device);
            }
        }
        devices = checkDatas;
        mRefreshAdapter.checkTreeItem(checkDatas);
        showData();
    }

    private void initRecylerView() {

        mRefreshAdapter = new RefreshAdapter(getContext(),devices);
        mGridLayoutManager = new GridLayoutManager(getContext(),2);

        //添加动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //添加分割线
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                if (type == 1) {//判定为footer
                    return 2;
                }else { //正常item
                    return 1;
                }
            }
        });
        mRecyclerView.setAdapter(mRefreshAdapter);
    }

    private void initListener() {

        initPullRefresh();

        initLoadMoreListener();

    }

    private void showData() {
        listTip.setVisibility(devices.size() == 0 ? VISIBLE : GONE);
        mSwipeRefreshLayout.setVisibility(devices.size() == 0 ? GONE : VISIBLE);

    }

    private void initPullRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Random random = new Random();
                        List<DeviceInfo> headDatas = new ArrayList<>();
                        for (int i=0; i< 4; i++) {
                            String name,typyName;

                            name = "装备信息";
                            typyName = "服务器";
                            int r = random.nextInt(100);
                            DeviceInfo device = new DeviceInfo(String.valueOf(r), name+": "+r,  "装备归属类别为:"+typyName );
                            headDatas.add(device);
                        }

                        mRefreshAdapter.AddHeaderItem(headDatas);

                        //刷新完成
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "更新了 "+headDatas.size()+" 条目数据", Toast.LENGTH_SHORT).show();
                    }

                }, 2000);

            }
        });
    }

    private void initLoadMoreListener() {

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mRefreshAdapter.getItemCount()) {

                    //设置正在加载更多
                    mRefreshAdapter.changeMoreStatus(mRefreshAdapter.LOADING_MORE);

                    //改为网络请求
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //

                            Random random = new Random();
                            List<DeviceInfo> footerDatas = new ArrayList<>();
                            for (int i=0; i< 4; i++) {
                                String name,typyName;

                                name = "图片教程";
                                typyName = "图片";
                                int r = random.nextInt(100);
                                DeviceInfo device = new DeviceInfo(String.valueOf(r), name+": "+r,  "教程类别为:"+typyName );
                                footerDatas.add(device);
                            }

                            mRefreshAdapter.AddFooterItem(footerDatas);
                            //设置回到上拉加载更多
                            mRefreshAdapter.changeMoreStatus(mRefreshAdapter.PULLUP_LOAD_MORE);
                            //没有加载更多了
                            //mRefreshAdapter.changeMoreStatus(mRefreshAdapter.NO_LOAD_MORE);
                            Toast.makeText(getActivity(), "更新了 " + footerDatas.size() + " 条目数据", Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);


                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_tip_message2 :
                reloadData("根目录2-1");
                break;
        }
    }
}
