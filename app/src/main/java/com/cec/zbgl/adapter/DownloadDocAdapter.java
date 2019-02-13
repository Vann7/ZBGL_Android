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

import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.List;

public class DownloadDocAdapter extends BaseAdapter {

    private List<FileFtp> mList;
    private List<FileFtp> mSelectedVideos = new ArrayList<>();
    private List<FileFtp> mFinish = new ArrayList<>();

    private LayoutInflater mInflater; //布局装载器对象
    private Context mContext;
    private ViewHolder viewHolder;
    private ListView mListView;
    private OnListClickListener mListener;

    public DownloadDocAdapter(List<FileFtp> rList, Context rContext, ListView rListView) {
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


    public void down(List<FileFtp> ftpFiles) {
        mSelectedVideos = ftpFiles;
        notifyDataSetChanged();
    }

    public void downFinish(List<FileFtp> ftpFiles, List<FileFtp> downList) {
        mList = ftpFiles;
        mFinish.addAll(downList);
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
            convertView = mInflater.inflate(R.layout.content_download, null);
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


    public void onDateChange(List<FileFtp> ftpFiles) {
        mList = ftpFiles;
        this.notifyDataSetChanged();
    }

    /**
     * listView点击Listener接口
     */
    public interface OnListClickListener {
        void onClick(FileFtp ftpFile, int position);
    }



    /**
     * 自定义装备信息view 缓存控件
     */
    class ViewHolder {
        private TextView name_tv;
        private TextView size_tv;
        private ImageView uncheck_btn;
        private ImageView check_btn;

        ViewHolder(View view) {
            //对viewHolder的属性进行赋值
            name_tv = (TextView) view.findViewById(R.id.download_name);
            size_tv = (TextView) view.findViewById(R.id.download_size);
            uncheck_btn = (ImageView) view.findViewById(R.id.download_uncheck_btn);
            check_btn = (ImageView) view.findViewById(R.id.download_check_btn);
            view.setTag(this);
        }

        public void  bindData(final FileFtp ftpFile) {
            if (ftpFile == null) return;
//            if (!mSelectedVideos.contains(ftpFile)) {
//                uncheck_btn.setVisibility(View.VISIBLE);
//                check_btn.setVisibility(View.GONE);
//            }else {
//                uncheck_btn.setVisibility(View.GONE);
//                check_btn.setVisibility(View.VISIBLE);
//            }
            if (mSelectedVideos.size() > 0) {
                for (FileFtp file : mSelectedVideos) {
                    if (ftpFile.getName().equals(file.getName())){
                        uncheck_btn.setVisibility(View.GONE);
                        check_btn.setVisibility(View.VISIBLE);
                        break;
                    }else {
                        uncheck_btn.setVisibility(View.VISIBLE);
                        check_btn.setVisibility(View.GONE);
                    }
                }
            } else {
                uncheck_btn.setVisibility(View.VISIBLE);
                check_btn.setVisibility(View.GONE);
            }

            if (mFinish.size() > 0) {
                for (FileFtp file : mFinish) {
                    if (ftpFile.getName().equals(file.getName())) {
                        check_btn.setImageResource(R.mipmap.roundcheckfill_finish);
                        uncheck_btn.setVisibility(View.GONE);
                        check_btn.setVisibility(View.VISIBLE);
                    }
                }
            }
            name_tv.setText(ftpFile.getName());
            long size = ftpFile.getSize();
            size_tv.setText(String.valueOf(CacheUtil.getFormatSize(size)));
        }

    }
}
