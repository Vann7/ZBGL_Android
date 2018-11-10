package com.cec.zbgl.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import com.cec.zbgl.R;
import com.cec.zbgl.activity.ContentActivity;
import com.cec.zbgl.activity.CourseActivity;
import com.cec.zbgl.activity.SearchActivity;
import com.cec.zbgl.adapter.FilterAdapter;
import com.cec.zbgl.adapter.OrgsAdapter;
import com.cec.zbgl.adapter.RefreshAdapter;
import com.cec.zbgl.holder.FilterItemViewHolder;
import com.cec.zbgl.listener.ItemClickListener;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.thirdLibs.zxing.activity.CaptureActivity;
import com.cec.zbgl.utils.ToastUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DeviceFragment extends Fragment implements View.OnClickListener{
    private ListView mTreeView;
    private OrgsAdapter<SpOrgnization> mAdapter;
    private List<SpOrgnization> orgs;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshAdapter mRefreshAdapter;
    private GridLayoutManager mGridLayoutManager;
    private List<DeviceInfo> devices = new ArrayList<>();
    private AppCompatTextView listTip; //没有数据view
    private RecyclerView filter_rv;
    private TextView masker_tv;
    private FilterAdapter filterAdapter;

    private ImageView scan_iv;
    private EditText search_ev;
    private TextView search_filter;
    private TextView clear_tv;
    private TextView confirm_tv;
    private RelativeLayout filter_rl;

    private RelativeLayout org_course_rl;
    private TextView org_course_btn;
    private TextView org_course_name;
    private TextView org_course_desc;
    private AlertDialog alertDialog;

    private boolean isShowing = false;
    private List<HashMap<Integer, String>> mList = new ArrayList<>();

    private static final int FILTER_TYPE_ITEM   = 1;
    private static final int FILTER_TYPE_HEADER = 0;
    private static final int SEARCH_BACK = -2;
//    private List<String> selectedItems = new ArrayList<>();
    private Map<String, Integer> selectedMaps = new HashMap<>();


    private String status[] = {"使用中","未入库","已入库","已出库"};
    private String types[] = {"网线","显示器","路由器","鼠标","键盘","笔记本","电源","耳机"};
    private String systems[] = {"系统1","系统2","系统3","系统4"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_device, container, false);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String result = data.getStringExtra("result");
                ToastUtils.showShort("二维码信息为:"+ result);
                break;
            case SEARCH_BACK :
                String name = data.getStringExtra("result");
                search(name);
                ToastUtils.showShort("检索词: "+ name);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {

        //主界面RV
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.GREEN);

        //没有数据view
        listTip = (AppCompatTextView) getActivity().findViewById(R.id.list_tip_message);

        //扫描二维码
        scan_iv = (ImageView) getView().findViewById(R.id.top_scan_btn);
        scan_iv.setOnClickListener(this);

        //搜索框
        search_ev = (EditText) getActivity().findViewById(R.id.search_ev);
        search_ev.setFocusable(false);

        //筛选框
        search_filter = (TextView) getActivity().findViewById(R.id.search_filter);
        search_filter.setOnClickListener(this);

        //蒙版
        masker_tv = (TextView) getActivity().findViewById(R.id.filter_masker);
        masker_tv.getBackground().setAlpha(100);
        masker_tv.setVisibility(GONE);

        //筛选条件
        filter_rv = (RecyclerView) getActivity().findViewById(R.id.filter_view);
//        filter_rv.setVisibility(GONE);

        //清空
        clear_tv = (TextView) getActivity().findViewById(R.id.filter_clear);
//        clear_tv.setVisibility(GONE);

        //确定
        confirm_tv = (TextView) getActivity().findViewById(R.id.filter_confirm);
//        confirm_tv.setVisibility(GONE);
        //筛选条件 RelativeLayout
        filter_rl = (RelativeLayout) getActivity().findViewById(R.id.filter_rl);
        filter_rl.setVisibility(GONE);

        //组织机构教程rl
        org_course_rl = (RelativeLayout) getActivity().findViewById(R.id.org_course);
        //组织机构查看btn
        org_course_btn = (TextView) getActivity().findViewById(R.id.org_course_btn);
        org_course_name = (TextView) getActivity().findViewById(R.id.org_course_name);
        org_course_desc = (TextView) getActivity().findViewById(R.id.org_course_desc);
        org_course_name.setText("组织机构-XX部门");
        org_course_desc.setText("组织机构描述信息——XX部门信息使用教程");

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
        org = new SpOrgnization("4","0","根目录4");
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

        //初始化filter数据
        HashMap<Integer, String>  map = new HashMap<Integer, String>();
        map.put(0,"设备状态");
        mList.add(map);
        for (int i=0; i< status.length; i++) {
            HashMap<Integer, String>  map1 = new HashMap<Integer, String>();
            map1.put(1,status[i]);
            mList.add(map1);
        }

        HashMap<Integer, String>  map2 = new HashMap<Integer, String>();
        map2.put(0,"设备类别");
        mList.add(map2);
        for (int i=0; i< types.length; i++) {
            HashMap<Integer, String>  map1 = new HashMap<Integer, String>();
            map1.put(1,types[i]);
            mList.add(map1);
        }
        HashMap<Integer, String>  map3 = new HashMap<Integer, String>();
        map3.put(0,"归属系统");
        mList.add(map3);
        for (int i=0; i< systems.length; i++) {
            HashMap<Integer, String>  map1 = new HashMap<Integer, String>();
            map1.put(1,systems[i]);
            mList.add(map1);
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

        //装备列表监听 点击事件
        mRefreshAdapter.setOnListClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                DeviceInfo node = devices.get(position);
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.putExtra("device",node);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View v, int position) {
                deleteItem(position);
            }

        });

        listTip.setOnClickListener(this);

        search_ev.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(intent,1);
            }
            return false;
        });

        masker_tv.setOnClickListener(this);
        clear_tv.setOnClickListener(this);
        confirm_tv.setOnClickListener(this);
        org_course_btn.setOnClickListener(this);
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
        mRefreshAdapter.changeData(checkDatas);
        showData();
    }

    //初始化主界面RV 及筛选RV
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

        //筛选Adapter
        filterAdapter = new FilterAdapter(getContext(), mList);
        //绑定点击事件
        filterAdapter.setOnListClickListener((v, position) -> {
            String itemName = mList.get(position).get(FILTER_TYPE_ITEM);
            if (selectedMaps.get(mList.get(position).get(FILTER_TYPE_ITEM)) == null) {
                selectedMaps.put(mList.get(position).get(FILTER_TYPE_ITEM), 1);

            } else {
                selectedMaps.remove(mList.get(position).get(FILTER_TYPE_ITEM));
            }
            filterAdapter.changeSelected(position,selectedMaps,itemName);
        });
        filter_rv.setAdapter(filterAdapter);
        GridLayoutManager filterManager = new GridLayoutManager(getContext(),5);
        filter_rv.setLayoutManager(filterManager);
        filterManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                int type = filter_rv.getAdapter().getItemViewType(position);
                if (type == FILTER_TYPE_HEADER) {//判定为标题
                    return 5;
                }else { //正常item
                    return 1;
                }
            }
        });
    }


    private void search(String name) {
        List<DeviceInfo> sList =  devices.stream().filter(device ->
            name.equals(device.getName())
        ).collect(Collectors.toList());
        mRefreshAdapter.changeData(sList);
    }

    //初始化监听接口
    private void initListener() {
        initPullRefresh();
        initLoadMoreListener();

    }

    //判定当devices数据为空时，展示listTip提示
    private void showData() {
        org_course_rl.setVisibility(devices.size() == 0 ? GONE : VISIBLE);
        listTip.setVisibility(devices.size() == 0 ? VISIBLE : GONE);
        mSwipeRefreshLayout.setVisibility(devices.size() == 0 ? GONE : VISIBLE);

    }

    //刷新Listener监听
    private void initPullRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
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
        }, 2000));
    }

    //加载更多Listener监听
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
                    new Handler().postDelayed(() -> {
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

    //控件点击事件监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_tip_message :
                reloadData("根目录2-1");
                break;
            case R.id.top_scan_btn :
                scanQc();
                break;
            case R.id.search_filter :
               isShowing = (isShowing == false) ? true : false;
               if (isShowing == false) {
                  disappear();
               }else {
                   appear();
               }
               break;
            case R.id.filter_masker :
                disappear();
                break;
            case R.id.filter_clear :
                ToastUtils.showShort("clear");
                selectedMaps.clear();
//                filterAdapter.notifyDataSetChanged();
                filterAdapter.clearSelected(selectedMaps);
                break;
            case R.id.filter_confirm :
                ToastUtils.showShort("confirm: " );

                disappear();
                break;
            case R.id.org_course_btn :
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                startActivity(intent);
        }
    }


    /**
     * 开启二维码扫描
     */
    public void scanQc() {
        if (authority()) {
            startActivityForResult(new Intent(getActivity(),
                    CaptureActivity.class), 1);
        } else {
            showDialogTipUserGoToAppSettting();
        }
    }


    //权限判断
    public boolean authority() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                return false;
            }
        }
        return true;
    }


    // 提示用户去应用设置界面手动开启权限
    private void showDialogTipUserGoToAppSettting(){
        alertDialog = new AlertDialog.Builder(getContext(),R.style.appalertdialog)
                .setTitle("存储权限不可用").setMessage("请在-应用设置-权限-中，允许应用使用摄像头权限")
                .setPositiveButton("立即开启", (dialog1, which) -> {
                    // 跳转到应用设置界面
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 123);
                }).setNegativeButton("取消", (dialog12, which) ->
                        dialog12.cancel()).setCancelable(false).show();
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

    //隐藏筛选tv
    private void disappear() {
        isShowing = false;
        search_filter.setTextColor(this.getResources().getColor(R.color.gray));
        filter_rv.setVisibility(GONE);
        filter_rv.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
        filter_rl.setVisibility(GONE);
        filter_rl.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
        masker_tv.setVisibility(GONE);
        masker_tv.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
    }

    //展示筛选tv
    private void appear() {
        isShowing = true;
        search_filter.setTextColor(this.getResources().getColor(R.color.orange));
        filter_rl.setVisibility(VISIBLE);
        filter_rl.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
        filter_rv.setVisibility(VISIBLE);
        filter_rv.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
        masker_tv.setVisibility(VISIBLE);
        masker_tv.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
    }

    //删除指定item
    private void deleteItem(int position) {
        alertDialog = new AlertDialog.Builder(getContext(),R.style.appalertdialog)
                .setMessage("删除本条信息")
                .setPositiveButton("删除", (dialog, which) -> {
                    mRefreshAdapter.removeData(position);
//                    mList.remove(position);
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


}
