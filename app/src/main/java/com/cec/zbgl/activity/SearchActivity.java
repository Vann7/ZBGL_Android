package com.cec.zbgl.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cec.zbgl.R;
import com.cec.zbgl.utils.ToastUtils;
import com.cec.zbgl.utils.searchview.ICallBack;
import com.cec.zbgl.utils.searchview.SearchView;
import com.cec.zbgl.utils.searchview.bCallBack;


public class SearchActivity extends AppCompatActivity {

    // 1. 初始化搜索框变量
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2. 绑定视图
        setContentView(R.layout.activity_search);

        // 3. 绑定组件
        searchView = (SearchView) findViewById(R.id.search_view);

        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(string -> {
//                System.out.println("我收到了" + string);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("result", string);
            this.setResult(-2, resultIntent);
            SearchActivity.this.finish();
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(() -> {
            SearchActivity.this.finish();
        });

        // 6. 设置点击item后的操作（通过回调接口）
        searchView.setOnItemClick((name, position) ->  {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("result", name);
            this.setResult(-2, resultIntent);
            SearchActivity.this.finish();
        });

    }
}
