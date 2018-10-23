package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cec.zbgl.R;
import com.cec.zbgl.holder.DeviceViewHolder;
import com.cec.zbgl.holder.TypeDeviceHolder;
import com.cec.zbgl.holder.TypeOneHolder;
import com.cec.zbgl.holder.TypeThreeHolder;
import com.cec.zbgl.holder.TypeTwoHolder;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 装备信息RecyclerView 适配器
 */
public class DeviceRecyclerAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{

    private LayoutInflater mLayoutInflater;
    private List<DeviceInfo> devices;
    private OnItemClickListener mListener;

    public DeviceRecyclerAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        devices = new ArrayList<>();
    }

    public void addList(List<DeviceInfo> list) {
        devices.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定viewHolder
        DeviceViewHolder viewHolder = new DeviceViewHolder(mLayoutInflater.
        inflate(R.layout.contact_item01,parent,false));

        //绑定点击事件
        viewHolder.itemView.setOnClickListener((View.OnClickListener) v -> {
            mListener.onItemClick(v);
        });
        //绑定长按事件
        viewHolder.itemView.setOnLongClickListener(v -> {
            mListener.onItemLongClick(v);
            return true;
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (devices != null){
            ((DeviceViewHolder)holder).bindHolder(devices.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {

        return devices.get(position).getType();
    }



    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void onDateChange(List<DeviceInfo> deviceInfoList) {
        this.devices = deviceInfoList;
        this.notifyDataSetChanged();
    }

    public void setOnListClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }


    /**
     * 自定义item点击接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view); //单击事件
        void onItemLongClick(View view); //长按事件
    }
}
