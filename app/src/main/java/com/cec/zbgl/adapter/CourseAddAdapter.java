package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cec.zbgl.R;

import java.util.List;

public class CourseAddAdapter extends BaseAdapter {

    private int mResourcedId; //layout资源Id
    private List<String> mList; //数据源
    private LayoutInflater mInflater; //布局装载器对象
    private ViewHolder viewHolder;
    private ListView course_lv;
    private OnListClickListener mListener;

    public CourseAddAdapter(int mResourcedId, Context context, List<String> mList, ListView course_lv) {
        mInflater = LayoutInflater.from(context);
        this.mResourcedId = mResourcedId;
        this.mList = mList;
        this.course_lv = course_lv;



      course_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if (mListener != null) {
                  mListener.onClick(position);
              }
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
        viewHolder = new ViewHolder();
        convertView = mInflater.inflate(mResourcedId, null);
        viewHolder.image = (ImageView) convertView.findViewById(R.id.course_add_image);
        viewHolder.name = (TextView) convertView.findViewById(R.id.course_add_name);
        if (position == 0) {
            viewHolder.name.setText("拍摄照片");
            viewHolder.image.setImageResource(R.mipmap.camera);
        }else if(position == 1) {
            viewHolder.name.setText("拍摄视频");
            viewHolder.image.setImageResource(R.mipmap.camera);
        } else {
            viewHolder.name.setText("导入文档");
            viewHolder.image.setImageResource(R.mipmap.file);
        }

        return  convertView;
    }


    public void setOnListClickListener(OnListClickListener mListener) {
        this.mListener = mListener;
    }

    /**
     * listView点击Listener接口
     */
    public interface OnListClickListener {
        void onClick(int position);
    }


    class ViewHolder {
        private ImageView image;
        private TextView name;
    }


}
