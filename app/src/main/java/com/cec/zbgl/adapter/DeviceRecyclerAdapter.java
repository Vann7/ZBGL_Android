package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cec.zbgl.R;
import com.cec.zbgl.holder.DeviceViewHolder;
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
    private Context mContext;


    public DeviceRecyclerAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        devices = new ArrayList<>();
    }

    public void addList(List<DeviceInfo> list) {
        devices.addAll(list);
    }

    public void pre_addList(List<DeviceInfo> list) {
         devices = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定viewHolder
        DeviceViewHolder viewHolder = new DeviceViewHolder(mLayoutInflater.
        inflate(R.layout.contact_item01,parent,false),mContext);

        //绑定点击事件
        viewHolder.itemView.setOnClickListener(v -> {
                mListener.onItemClick(v, viewHolder.getLayoutPosition());
        });
        //绑定长按事件
        viewHolder.itemView.setOnLongClickListener(v -> {
            mListener.onItemLongClick(v, viewHolder.getLayoutPosition());
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
//        return devices.get(position).getType();
        return position;
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
        void onItemClick(View v, int position); //单击事件
        void onItemLongClick(View v, int position); //长按事件
    }
}
