package com.cec.zbgl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;
import me.nereo.multi_image_selector.bean.Video;

public class UploadAdapter extends BaseAdapter {

    private List<Video> mList = new ArrayList<>();
    private List<Video> mSelectedVideos = new ArrayList<>();
    private List<Video> mFinish = new ArrayList<>();

    private LayoutInflater mInflater; //布局装载器对象
    private Context mContext;
    private ViewHolder viewHolder;
    private ListView mListView;
    private OnListClickListener mListener;

    public UploadAdapter(List<Video> mList,  Context mContext, ListView listView) {
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


    public void upload(List<Video> videos) {
        mSelectedVideos = videos;
        notifyDataSetChanged();
    }

    public void uploadFinish(List<Video> list, List<Video> upList) {
        mFinish.addAll(upList);
        mList = list;
        notifyDataSetChanged();
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
            convertView = mInflater.inflate(R.layout.content_upload, null);
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


    public void onDateChange(List<Video> videos) {
        this.mList = videos;
        this.notifyDataSetChanged();
    }

    /**
     * listView点击Listener接口
     */
    public interface OnListClickListener {
        void onClick(Video video, int position);
    }



    /**
     * 自定义装备信息view 缓存控件
     */
    class ViewHolder {
        private ImageView image_iv;
        private TextView name_tv;
        private ImageView uncheck_btn;
        private ImageView check_btn;

        ViewHolder(View view) {
            //对viewHolder的属性进行赋值
            image_iv = (ImageView) view.findViewById(R.id.upload_image);
            name_tv = (TextView) view.findViewById(R.id.upload_name);
            uncheck_btn = (ImageView) view.findViewById(R.id.upload_uncheck_btn);
            check_btn = (ImageView) view.findViewById(R.id.upload_check_btn);
            view.setTag(this);
        }

        public void  bindData(final Video video) {
            if (video == null) return;

            if (mSelectedVideos.contains(video)) {
                check_btn.setImageResource(R.mipmap.roundcheckfill);
                uncheck_btn.setVisibility(View.GONE);
                check_btn.setVisibility(View.VISIBLE);
            }else {
                uncheck_btn.setVisibility(View.VISIBLE);
                check_btn.setVisibility(View.GONE);
            }
            if (mFinish.contains(video)) {
                check_btn.setImageResource(R.mipmap.roundcheckfill_finish);
                uncheck_btn.setVisibility(View.GONE);
                check_btn.setVisibility(View.VISIBLE);
            }
            name_tv.setText(video.getName());
            image_iv.setImageBitmap(video.getBitmap());
            image_iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            if (video.getBitmap() != null) {
//                File imageFile = FileUtils.bitmap2File(video.getBitmap(), mContext);
//                // 显示图片
//                Picasso.with(mContext)
//                        .load(imageFile)
//                        .placeholder(me.nereo.multi_image_selector.R.drawable.mis_default_error)
//                        .tag(MultiImageSelectorFragment.TAG)
//                        .resize(60, 60)
//                        .centerCrop()
//                        .into(viewHolder.image_iv);
//
//            }
        }

    }
}
