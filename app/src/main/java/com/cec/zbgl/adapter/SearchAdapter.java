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
import com.cec.zbgl.common.Constant;
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
    private TypeHolder typeHolder;
    private OrgHolder orgHolder;
    private ListView device_lv;
    private Context mContext;

    private OnListClickListener mListener;
    /**
     * Item Type 的数量
     * */
    private static final int TYPE_ITEM_COUNT = 3;


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

    @Override
    public int getItemViewType(int position) {
        DeviceInfo device = mList.get(position);
        if (device == null) {
            return -1;
        } else {
            return device.getSearchType();
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_ITEM_COUNT;
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
        DeviceInfo device = mList.get(position);
        //获取相应索引的DeviceInfo对象

        View itemView = null;
        View orgView = null;
        View deviceView = null;
        int type = getItemViewType(position);

        switch (type) {
            case Constant.SEARCH_ITEM :
                typeHolder = null;
                if (convertView == null) {
                    itemView = mInflater.inflate(R.layout.content_search_item, null);
                    typeHolder = new TypeHolder(itemView);

                    itemView.setTag(typeHolder);
                    convertView = itemView;

                } else {
                    typeHolder = (TypeHolder) convertView.getTag();
                }
                typeHolder.bindData(device);
               break;
            case Constant.SEARCH_ORGNIZATION :
                orgHolder = null;
                if (convertView == null) {
                    orgView = mInflater.inflate(R.layout.content_search_org, null);
                    orgHolder = new OrgHolder(orgView);
                    orgView.setTag(orgHolder);
                    orgHolder.image_iv.setTag(device.getId());
                    convertView = orgView;

                } else {
                    orgHolder = (OrgHolder) convertView.getTag();
                }
                orgHolder.bindData(device);
                break;
            case Constant.SEARCH_DEVICE :
                viewHolder = null;
                if (convertView == null) {
                    deviceView = mInflater.inflate(R.layout.content_wx, null);
                    viewHolder = new ViewHolder(deviceView);
                    deviceView.setTag(viewHolder);
                    convertView = deviceView;
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.bindData(device);
                break;
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

        ViewHolder(View view) {
            image_iv = (ImageView) view.findViewById(R.id.search_device_image);
            name_tv = (TextView) view.findViewById(R.id.search_device_name);
            type_tv = (TextView) view.findViewById(R.id.search_device_type);
            view.setTag(this);
        }

        public void bindData(DeviceInfo device) {
            name_tv.setText(device.getName());
            type_tv.setText(String.valueOf(device.getType()));
            if (device.getImage() != null) {
                File imageFile = FileUtils.byte2File(device.getImage(), mContext);
                // 显示图片
                Picasso.with(mContext)
                        .load(imageFile)
                        .placeholder(me.nereo.multi_image_selector.R.drawable.mis_default_error)
                        .tag(device.getId())
                        .resize(100, 100)
                        .centerCrop()
                        .into(image_iv);

            }
        }

    }

    /**
     * 自定义系统信息view 缓存控件
     */
    class OrgHolder {
        private ImageView image_iv;
        private TextView name_tv;

        OrgHolder(View view) {
            name_tv = (TextView) view.findViewById(R.id.search_org_name);
            image_iv = (ImageView) view.findViewById(R.id.search_org_image);
        }

        public void bindData(DeviceInfo device) {
            name_tv.setText(device.getName());
            if (image_iv.getTag().equals(device.getId())) {
                if (device.getImage() != null) {
                    File imageFile = FileUtils.byte2File(device.getImage(), mContext);
                    // 显示图片
                    Picasso.with(mContext)
                            .load(imageFile)
                            .placeholder(me.nereo.multi_image_selector.R.drawable.mis_default_error)
                            .tag(device.getId())
                            .resize(100, 100)
                            .centerCrop()
                            .into(image_iv);
                }
            }

        }
    }

    /**
     * 查询结果分类控件
     */
    class TypeHolder{
        private TextView name_tv;

        TypeHolder (View view) {
            name_tv = (TextView) view.findViewById(R.id.search_item_name);
            view.setTag(this);
        }

        public void bindData(DeviceInfo device) {
            if (device.getName() != null) {
                name_tv.setText(device.getName());
            }
        }

    }

}
