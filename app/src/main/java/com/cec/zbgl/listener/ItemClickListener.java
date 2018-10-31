package com.cec.zbgl.listener;

import android.view.View;

/**
 * 自定义item点击接口
 */
public interface ItemClickListener {
    void onItemClick(View v, int position); //单击事件
    void onItemLongClick(View v, int position); //长按事件
}
