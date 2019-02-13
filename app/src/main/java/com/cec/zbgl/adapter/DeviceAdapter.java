package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceInfo;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {

    private int mResourcedId; //layout资源Id
    private List<DeviceInfo> mList; //数据源
    private LayoutInflater mInflater; //布局装载器对象
    private ViewHolder viewHolder;
    private ListView device_lv;


    private OnListClickListener mListener;

    // 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
    public DeviceAdapter(ListView lv, Context context, List<DeviceInfo> deviceInfoList,int resourceId) {
        mInflater = LayoutInflater.from(context);
        this.mList = deviceInfoList;
        this.mResourcedId = resourceId;
        this.device_lv = lv;

        device_lv.setOnItemClickListener((parent, view, position, id) -> {
            if (mListener != null) {
                mListener.onClick(mList.get(position - 1), position);
            }
        });

    }

    //ListView需要显示的数据数量
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    //指定的索引对应的数据项
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    //指定的索引对应的数据项ID
    public long getItemId(int position) {
        return position;
    }

    /**
     * 利用了ListView的缓存机制，而且使用(自定义)ViewHolder类来实现显示数据视图的缓存，避免多次调用findViewById来寻找控件，以达到优化程序的目的
     * @param position
     * @param convertView
     * @param parent
     * @return 返回每一项的显示内容
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 由于我们只需要将XML转化为View，并不涉及到具体的布局，所以第二个参数通常设置为null
            convertView = mInflater.inflate(mResourcedId, null);
            //对viewHolder的属性进行赋值
            viewHolder.name_tv = (TextView) convertView.findViewById(R.id.search_device_name);
            viewHolder.type_tv = (TextView) convertView.findViewById(R.id.search_device_type);
//            viewHolder.location_tv = (TextView) convertView.findViewById(R.id.id_device_location);
            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取相应索引的DeviceInfo对象
        DeviceInfo device = mList.get(position);
        //设置控件的对应属性值
        viewHolder.name_tv.setText(device.getName());
        viewHolder.type_tv.setText(String.valueOf(device.getType()));
//        viewHolder.location_tv.setText(device.getLocation());
        return convertView;
    }


    public void onDateChange(List<DeviceInfo> devices) {
        this.mList = devices;
        this.notifyDataSetChanged();
    }

    public void setOnListClickListener(OnListClickListener mListener) {
        this.mListener = mListener;
    }

    /**
     * listView点击Listener接口
     */
    public interface OnListClickListener {
        void onClick(DeviceInfo device, int position);
    }

    /**
     * 自定义装备信息view 缓存控件
     */
    class ViewHolder {
        private TextView name_tv;
        private TextView type_tv;
//        private TextView location_tv;

    }
}
