package com.cec.zbgl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;


/**
 * 装备信息自定义检索Adapter
 */
public class SearchAdapter extends BaseAdapter {

    private List<DeviceInfo> mList; //数据源
    private LayoutInflater mInflater; //布局装载器对象
    private ViewHolder viewHolder;
    private ListView device_lv;
    private Context mContext;

    private OnListClickListener mListener;


    // 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
    public SearchAdapter(ListView lv, Context context, List<DeviceInfo> deviceInfoList) {
        mInflater = LayoutInflater.from(context);
        this.mList = deviceInfoList;
        this.device_lv = lv;
        mContext = context;

        device_lv.setOnItemClickListener((parent, view, position, id) -> {
            if (mListener != null) {
                mListener.onClick(mList.get(position), position);
            }
        });
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
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
            convertView = mInflater.inflate(R.layout.content_wx, null);
            //对viewHolder的属性进行赋值
            viewHolder.image_iv = (ImageView) convertView.findViewById(R.id.id_device_image);
            viewHolder.name_tv = (TextView) convertView.findViewById(R.id.id_device_name);
            viewHolder.type_tv = (TextView) convertView.findViewById(R.id.id_device_type);
            viewHolder.location_tv = (TextView) convertView.findViewById(R.id.id_device_location);
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
        viewHolder.location_tv.setText(device.getLocation());
        if (device.getImage() != null) {
            File imageFile = FileUtils.byte2File(device.getImage(), mContext);
            // 显示图片
            Picasso.with(mContext)
                    .load(imageFile)
                    .placeholder(me.nereo.multi_image_selector.R.drawable.mis_default_error)
                    .tag(MultiImageSelectorFragment.TAG)
                    .resize(60, 60)
                    .centerCrop()
                    .into(viewHolder.image_iv);

        }
        return convertView;
    }

    public void setOnListClickListener(OnListClickListener mListener) {
        this.mListener = mListener;
    }


    public void onDateChange(List<DeviceInfo> devices) {
        this.mList = devices;
        this.notifyDataSetChanged();
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
        private ImageView image_iv;
        private TextView name_tv;
        private TextView type_tv;
        private TextView location_tv;

    }

}
