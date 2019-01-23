package com.cec.zbgl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.DeviceRele;
import com.cec.zbgl.service.DeviceService;
import com.cec.zbgl.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import me.nereo.multi_image_selector.bean.Video;

public class ReleAdapter extends BaseAdapter {

    private Context mContext;
    private List<DeviceRele> mList;
    private DeviceService deviceService;
    private LayoutInflater mInflater; //布局装载器对象
    private ViewHolder viewHolder;
    private OnListClickListener mListener;
    private GridView mGridView;

    public ReleAdapter(Context mContext, List<DeviceRele> list, GridView gridView) {
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mList = list;
        this.mGridView = gridView;
        deviceService = new DeviceService();
        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            if (mListener != null) {
                mListener.onClick(this.mList.get(position), position);
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    mListener.onLongClick(mList.get(position), position);
                }
                return true;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // 由于我们只需要将XML转化为View，并不涉及到具体的布局，所以第二个参数通常设置为null
            convertView = mInflater.inflate(R.layout.content_rele_device, null);
            viewHolder = new ViewHolder(convertView);
            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (viewHolder != null) {
            viewHolder.bindData(mList.get(position));
        }


        return convertView;
    }


    //  删除数据
    public void removeData(List<DeviceRele> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void setOnListClickListener(OnListClickListener mListener) {
        this.mListener = mListener;
    }

    /**
     * listView点击Listener接口
     */
    public interface OnListClickListener {
        void onClick(DeviceRele deviceRele, int position);
        void onLongClick(DeviceRele deviceRele, int position);
    }

    public void onDateChange(List<DeviceRele> reles) {
        this.mList = reles;
        this.notifyDataSetChanged();
    }


    class ViewHolder {
        private TextView name_tv;
        private ImageView image_iv;

        ViewHolder(View view) {
            name_tv = (TextView) view.findViewById(R.id.rele_name);
            image_iv = (ImageView) view.findViewById(R.id.rele_iv);
            view.setTag(this);
        }

        public void bindData(final DeviceRele rele) {
            DeviceInfo device = null;
            if (rele != null){
                device = deviceService.getDeviceByMid(rele.getReleDeviceId());
            }
            if (device != null) {
                name_tv.setText(device.getName());
                if (device.getImage() != null) {
                    File imageFile = FileUtils.byte2File(device.getImage(), mContext);
                    // 显示图片
                    Picasso.with(mContext)
                            .load(imageFile)
//                        .placeholder(me.nereo.multi_image_selector.R.drawable.mis_default_error)
                            .tag("deviceTag")
                            .resize(80, 80)
                            .centerCrop()
                            .into(image_iv);
                }
            }
        }

    }




}
