package com.cec.zbgl.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cec.zbgl.R;
import com.cec.zbgl.service.DeviceService;
import com.cec.zbgl.utils.ToastUtils;
import com.cec.zbgl.utils.searchview.ICallBack;
import com.cec.zbgl.utils.searchview.SearchView;
import com.cec.zbgl.utils.searchview.bCallBack;


public class SearchActivity extends AppCompatActivity {

    // 1. 初始化搜索框变量
    private SearchView searchView;
    private static final int BACK_REFRESH = -3;
    private String deviceId;
    private boolean is_rele;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2. 绑定视图
        setContentView(R.layout.activity_search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
        }

        // 3. 绑定组件
        searchView = (SearchView) findViewById(R.id.search_view);

        deviceId = getIntent().getStringExtra("deviceId");
        is_rele = getIntent().getBooleanExtra("is_rele", false);


        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
/*        searchView.setOnClickSearch(string -> {
//                System.out.println("我收到了" + string);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("result", string);
            this.setResult(-2, resultIntent);
//            SearchActivity.this.finish();
        });*/

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(() -> {
            SearchActivity.this.finish();
        });

        // 6. 设置点击item后的操作（通过回调接口）
        searchView.setOnItemClick((mid, position) ->  {
            if (!is_rele) {
                Intent intent = new Intent(this, ContentActivity.class);
                intent.putExtra("mid", mid);
                intent.putExtra("add", false);
                startActivityForResult(intent,1);
            } else {
                Intent intent = new Intent();
                if (mid.equals(deviceId)) {
                    ToastUtils.showShort("设备不能与自己进行关联");
                    return;
                }else {
                    intent.putExtra("releDeviceId", mid);
                    this.setResult(RESULT_FIRST_USER,intent);
                    this.finish();
                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case BACK_REFRESH :
                searchView.search();

        }
    }
}
