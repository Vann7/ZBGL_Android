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
import com.cec.zbgl.model.FileFtp;
import com.cec.zbgl.utils.CacheUtil;

import java.util.ArrayList;
import java.util.List;

public class SyncAdapter extends BaseAdapter {

    private List<String> mList;
    private List<String> mSelected = new ArrayList<>();
    private List<String> mFinish = new ArrayList<>();
    private LayoutInflater mInflater; //布局装载器对象
    private Context mContext;
    private ViewHolder viewHolder;
    private ListView mListView;
    private OnListClickListener mListener;

    public SyncAdapter(List<String> rList, Context rContext, ListView rListView) {
        mInflater = LayoutInflater.from(rContext);
        this.mList = rList;
        this.mContext = rContext;
        this.mListView = rListView;

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            if (mListener != null) {
                mListener.onClick(mList.get(position), position);
            }
        });

    }


    public void syncData(List<String> list) {
        mSelected = list;
      //  mList = ftpFiles;
        notifyDataSetChanged();
    }

    public void downFinish(List<String> list, List<String> syncLIst) {
        mList = list;
        mFinish = syncLIst;
        notifyDataSetChanged();
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
            convertView = mInflater.inflate(R.layout.content_sync, null);
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


    public void onDateChange(List<String> ftpFiles) {
        mList = ftpFiles;
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
        private ImageView uncheck_btn;
        private ImageView check_btn;

        ViewHolder(View view) {
            //对viewHolder的属性进行赋值
            name_tv = (TextView) view.findViewById(R.id.sync_name);
            uncheck_btn = (ImageView) view.findViewById(R.id.sync_uncheck_btn);
            check_btn = (ImageView) view.findViewById(R.id.sync_check_btn);
            view.setTag(this);
        }

        public void  bindData(final String name) {
            if (name == null) return;

            if (mSelected.size() > 0) {
                for (String str : mSelected) {
                    if (name.equals(str)){
                        uncheck_btn.setVisibility(View.GONE);
                        check_btn.setVisibility(View.VISIBLE);
                        break;
                    }else {
                        uncheck_btn.setVisibility(View.VISIBLE);
                        check_btn.setVisibility(View.GONE);
                    }
                }
            }else {
                uncheck_btn.setVisibility(View.VISIBLE);
                check_btn.setVisibility(View.GONE);
            }

            if (mFinish.size() > 0) {
                for (String str : mFinish) {
                    if (name.equals(str)) {
                        check_btn.setImageResource(R.mipmap.roundcheckfill_finish);
                        uncheck_btn.setVisibility(View.GONE);
                        check_btn.setVisibility(View.VISIBLE);
                    }
                }
            }
            name_tv.setText(name);
        }

    }
}
