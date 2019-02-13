package com.cec.zbgl.utils.searchview;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.SearchAdapter;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.listener.ItemClickListener;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.service.CourseService;
import com.cec.zbgl.service.DeviceService;
import com.cec.zbgl.service.OrgsService;
import com.cec.zbgl.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Carson_Ho on 17/8/10.
 */

public class SearchView extends LinearLayout {

    /**
     * 初始化成员变量
     */
    private Context context;

    // 搜索框组件
    private EditText et_search; // 搜索按键
    private TextView tv_clear;  // 删除搜索记录按键
    private LinearLayout search_block; // 搜索框布局
    private ImageView searchBack; // 返回按键
    private TextView search_tv; //检索

    private ScrollView list_sv; //历史搜索sv
//    private ScrollView result_sv; //搜索结果sv
    private LinearLayout result_ll;
    private ListView result_lv; //所搜结果lv


    // ListView列表 & 适配器
    private SearchListView listView;
    private BaseAdapter adapter;

    // 数据库变量
    // 用于存放历史搜索记录
    private RecordSQLiteOpenHelper helper ;
    private SQLiteDatabase db;

    // 回调接口
    private  ICallBack mCallBack;// 搜索按键回调接口
    private  bCallBack bCallBack; // 返回按键回调接口
    private OnListClickListener mListener; //点击item回调接口

    // 自定义属性设置
    // 1. 搜索字体属性设置：大小、颜色 & 默认提示
    private Float textSizeSearch;
    private int textColorSearch;
    private String textHintSearch;

    // 2. 搜索框设置：高度 & 颜色
    private int searchBlockHeight;
    private int searchBlockColor;

    private List<DeviceInfo> devices = new ArrayList<>();
    private List<SpOrgnization> orgs = new ArrayList<>();
    private SearchAdapter mAdapter;
    private DeviceService deviceService;
    private OrgsService orgsService;

    private boolean isRele;

    /**
     * 构造函数
     * 作用：对搜索框进行初始化
     */
    public SearchView(Context context) {
        super(context);
        this.context = context;
        initData();
        init();
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(context, attrs); // ->>关注a
        initData();
        init();// ->>关注b
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(context, attrs);
        initData();
        init();
    }

    private void initData() {

    }

    /**
     * 关注a
     * 作用：初始化自定义属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {

        // 控件资源名称
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Search_View);

        // 搜索框字体大小（dp）
        textSizeSearch = typedArray.getDimension(R.styleable.Search_View_textSizeSearch, 20);

        // 搜索框字体颜色（使用十六进制代码，如#333、#8e8e8e）
        int defaultColor = context.getResources().getColor(R.color.darkgray); // 默认颜色 = 灰色
        textColorSearch = typedArray.getColor(R.styleable.Search_View_textColorSearch, defaultColor);

        // 搜索框提示内容（String）
        textHintSearch = typedArray.getString(R.styleable.Search_View_textHintSearch);

        // 搜索框高度
//        searchBlockHeight = typedArray.getInteger(R.styleable.Search_View_searchBlockHeight, 20);
        searchBlockHeight = typedArray.getInteger(R.styleable.Search_View_searchBlockHeight, 150);

        // 搜索框颜色
        int defaultColor2 = context.getResources().getColor(R.color.gray); // 默认颜色 = 白色
        searchBlockColor = typedArray.getColor(R.styleable.Search_View_searchBlockColor, defaultColor2);

        // 释放资源
        typedArray.recycle();
    }


    /**
     * 关注b
     * 作用：初始化搜索框
     */
    private void init(){

        // 1. 初始化UI组件->>关注c
        initView();

        // 2. 实例化数据库SQLiteOpenHelper子类对象
        helper = new RecordSQLiteOpenHelper(context);

        // 3. 第1次进入时查询所有的历史搜索记录
        queryData("");

        /**
         * "清空搜索历史"按钮
         */
        tv_clear.setOnClickListener(v -> {
            // 清空数据库->>关注2
            deleteData();
            // 模糊搜索空字符 = 显示所有的搜索历史（此时是没有搜索记录的）
            queryData("");
        });

        /**
         * 监听输入键盘更换后的搜索按键
         * 调用时刻：点击键盘上的搜索键时
         */
        et_search.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                checkAndSearch();
//                // 1. 点击搜索按键后，根据输入的搜索字段进行查询
//                // 注：由于此处需求会根据自身情况不同而不同，所以具体逻辑由开发者自己实现，此处仅留出接口
//                if (!(mCallBack == null)){
//                    mCallBack.SearchAciton(et_search.getText().toString());
//                }
////                Toast.makeText(context, "需要搜索的是" + et_search.getText(), Toast.LENGTH_SHORT).show();
//                if (!et_search.getText().toString().trim().equals("")){
//                    // 2. 点击搜索键后，对该搜索字段在数据库是否存在进行检查（查询）->> 关注1
//                    boolean hasData = hasData(et_search.getText().toString().trim());
//                    // 3. 若存在，则不保存；若不存在，则将该搜索字段保存（插入）到数据库，并作为历史搜索记录
//                    if (!hasData) {
//                        insertData(et_search.getText().toString().trim());
//                        queryData("");
//                    }
//                }
            }
            return false;
        });


        /**
         * 搜索框的文本变化实时监听
         */
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                hideResult();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // 输入文本后调用该方法
            @Override
            public void afterTextChanged(Editable s) {
                // 每次输入后，模糊查询数据库 & 显示
                // 注：若搜索框为空,则模糊搜索空字符 = 显示所有的搜索历史
                String tempName = et_search.getText().toString();
                queryData(tempName); // ->>关注1

            }
        });





        /**
         * 搜索记录列表（ListView）监听
         * 即当用户点击搜索历史里的字段后,会直接将结果当作搜索字段进行搜索
         */
        listView.setOnItemClickListener((parent, view, position, id) -> {

            // 获取用户点击列表里的文字,并自动填充到搜索框内
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            String name = textView.getText().toString();
            et_search.setText(name);
            checkAndSearch();
//            if (mListener != null) {
//                mListener.onClick(name, position);
//            }

//            Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
        });

        /**
         * 点击返回按键后的事件
         */
        searchBack.setOnClickListener(v -> {
            // 注：由于返回需求会根据自身情况不同而不同，所以具体逻辑由开发者自己实现，此处仅留出接口
            if (!(bCallBack == null)){
                bCallBack.BackAciton();
            }
            //根据输入的内容模糊查询商品，并跳转到另一个界面，这个根据需求实现
//            Toast.makeText(context, "返回到上一页", Toast.LENGTH_SHORT).show();
        });

        search_tv.setOnClickListener(v -> {
            checkAndSearch();

        });

        result_lv.setOnItemClickListener((parent, view, position, id) -> {

            if (mListener != null) {
                mListener.onClick(devices.get(position), position);
            }

        });

    }

    private void checkAndSearch() {
        String name = et_search.getText().toString();
        if (name.trim().equals("")) {
            ToastUtils.showShort("请输入检索信息!");
            return;
        }
        search();
        showResult();
    }

    /**
     * 检索信息
     */
    public void search() {
        String name = et_search.getText().toString();

        // 1. 点击搜索按键后，根据输入的搜索字段进行查询
        // 注：由于此处需求会根据自身情况不同而不同，所以具体逻辑由开发者自己实现，此处仅留出接口
        if (!(mCallBack == null)){
            mCallBack.SearchAciton(name);
        }
//                Toast.makeText(context, "需要搜索的是" + et_search.getText(), Toast.LENGTH_SHORT).show();
        if (!et_search.getText().toString().trim().equals("")){
            // 2. 点击搜索键后，对该搜索字段在数据库是否存在进行检查（查询）->> 关注1
            boolean hasData = hasData(name.trim());
            // 3. 若存在，则不保存；若不存在，则将该搜索字段保存（插入）到数据库，并作为历史搜索记录
            if (!hasData) {
                insertData(et_search.getText().toString().trim());
                queryData("");
            }
        }

        List<DeviceInfo> datas = new ArrayList<>();
        deviceService = new DeviceService();
        orgsService = new OrgsService();
        //根据name 查询装备信息list
       devices = deviceService.searchByName(name);
       if (!isRele) {
           orgs = orgsService.searchByName(name);
           if (orgs.size() > 0) {
               datas.add(new DeviceInfo("系统信息", Constant.SEARCH_ITEM));
               List<DeviceInfo> courseList = new ArrayList<>();
               orgs.stream().forEach( org -> {
                   DeviceInfo d = new DeviceInfo();
                   d.setName(org.getName());
                   d.setImage(org.getImage());
                   d.setmId(org.getmId());
                   d.setCode(org.getCode());
                   d.setSearchType(Constant.SEARCH_ORGNIZATION);
                   datas.add(d);
               });
           }

           if (devices.size() > 0) {
               datas.add(new DeviceInfo("设备信息", Constant.SEARCH_ITEM));

           }
       }
        datas.addAll(devices);
        mAdapter.onDateChange(datas);
        devices = datas;
    }


    /**
     * 关注c：绑定搜索框xml视图
     */
    private void initView(){

        // 1. 绑定R.layout.search_layout作为搜索框的xml文件
        LayoutInflater.from(context).inflate(R.layout.search_layout,this);

        // 2. 绑定搜索框EditText
        et_search = (EditText) findViewById(R.id.et_search);
//        et_search.setTextSize(textSizeSearch);
//        et_search.setTextColor(textColorSearch);
        et_search.setHint(textHintSearch);

        // 3. 搜索框背景颜色
//        search_block = (LinearLayout)findViewById(R.id.search_block);
//        LayoutParams params = (LayoutParams) search_block.getLayoutParams();
//        params.height = searchBlockHeight;
//        search_block.setBackgroundColor(searchBlockColor);
//        search_block.setLayoutParams(params);

        // 4. 历史搜索记录 = ListView显示
        listView = (SearchListView) findViewById(R.id.listView);

        // 5. 删除历史搜索记录 按钮
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        tv_clear.setVisibility(INVISIBLE);

        // 6. 返回按键
        searchBack = (ImageView) findViewById(R.id.search_back);
        search_tv = (TextView) findViewById(R.id.search_item);
//        tv_back = (ImageView) findViewById(R.id.search_back);

        list_sv = (ScrollView) findViewById(R.id.list_sv);

        //7.搜索结果sv lv ll
        result_ll = (LinearLayout) findViewById(R.id.result_ll);
//        result_sv = (ScrollView) findViewById(R.id.result_sv);
        result_lv = (ListView) findViewById(R.id.search_result_lv);
//        result_sv.setVisibility(GONE);

        mAdapter = new SearchAdapter(result_lv, context,  devices);
        result_lv.setAdapter(mAdapter);
    }

    /**
     * 关注1
     * 模糊查询数据 & 显示到ListView列表上
     */
    private void queryData(String tempName) {

        // 1. 模糊搜索
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);
        // 2. 创建adapter适配器对象 & 装入模糊搜索的结果
        adapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, cursor, new String[] { "name" },
                new int[] { android.R.id.text1 }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 3. 设置适配器
        listView.setAdapter(adapter);
         adapter.notifyDataSetChanged();

        // 当输入框为空 & 数据库中有搜索记录时，显示 "删除搜索记录"按钮
        if (tempName.equals("") && cursor.getCount() != 0){
            tv_clear.setVisibility(VISIBLE);
        }
        else {
            tv_clear.setVisibility(INVISIBLE);
        };

    }

    /**
     * 关注2：清空数据库
     */
    private void deleteData() {

        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
        tv_clear.setVisibility(INVISIBLE);
    }

    /**
     * 关注3
     * 检查数据库中是否已经有该搜索记录
     */
    private boolean hasData(String tempName) {
        // 从数据库中Record表里找到name=tempName的id
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        //  判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 关注4
     * 插入数据到数据库，即写入搜索字段到历史搜索记录
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 点击键盘中搜索键后的操作，用于接口回调
     */
    public void setOnClickSearch(ICallBack mCallBack){
        this.mCallBack = mCallBack;

    }

    /**
     * 点击返回后的操作，用于接口回调
     */
    public void setOnClickBack(bCallBack bCallBack){
        this.bCallBack = bCallBack;
    }

    /**
     * 点击item后的操作，用于接口回调
     */
    public void setOnItemClick(OnListClickListener mListener) {
        this.mListener = mListener;
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.et_search :
//                hideResult();
//        }
//    }

    /**
     * listView点击Listener接口
     */
    public interface OnListClickListener {
        void onClick(DeviceInfo device, int position);
    }


    private void hideResult() {
        result_ll.setVisibility(GONE);
        list_sv.setVisibility(VISIBLE);
    }

    private void showResult() {
        hideKeyBoard();
        result_ll.setVisibility(VISIBLE);
        list_sv.setVisibility(GONE);
    }

    /**
     * 隐藏键盘
     */
    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this,InputMethodManager.SHOW_FORCED);

        imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    public boolean isRele() {
        return isRele;
    }

    public void setRele(boolean rele) {
        isRele = rele;
    }
}
