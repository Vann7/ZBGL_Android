package com.cec.zbgl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cec.zbgl.R;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Video;

public class OrgSearchAdapter extends BaseAdapter {

    private List<String> mList = new ArrayList<>();
    private List<String> mSelectedVideos = new ArrayList<>();
    private List<String> mFinish = new ArrayList<>();

    private LayoutInflater mInflater; //布局装载器对象
    private Context mContext;
    private ViewHolder viewHolder;
    private ListView mListView;
    private OnListClickListener mListener;

    public OrgSearchAdapter(List<String> mList, Context mContext, ListView listView) {
        mInflater = LayoutInflater.from(mContext);
        this.mList = mList;
        this.mContext = mContext;
        this.mListView = listView;

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            if (mListener != null) {
                mListener.onClick(this.mList.get(position), position);
            }
        });
    }


    @Override
    public int getCount() {
        if (mList == null) return 0;
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
            convertView = mInflater.inflate(R.layout.content_org_search, null);
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

    public void setOnListClickListener(OnListClickListener mListener) {
        this.mListener = mListener;
    }


    public void onDateChange(List<String> list) {
        this.mList = list;
        this.notifyDataSetChanged();
    }

    /**
     * listView点击Listener接口
     */
    public interface OnListClickListener {
        void onClick(String name, int position);
    }



    /**
     * 自定义装备信息view 缓存控件
     */
    class ViewHolder {
        private TextView name_tv;


        ViewHolder(View view) {
            //对viewHolder的属性进行赋值
            name_tv = (TextView) view.findViewById(R.id.org_search_tv);
            view.setTag(this);
        }

        public void  bindData(final String name) {
            name_tv.setText(name);
        }

    }
}
