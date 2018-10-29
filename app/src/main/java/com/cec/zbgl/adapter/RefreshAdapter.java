package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cec.zbgl.R;
import com.cec.zbgl.holder.DeviceViewHolder;
import com.cec.zbgl.holder.FooterViewHolder;
import com.cec.zbgl.model.DeviceInfo;

import java.util.List;



public class RefreshAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context        mContext;
    LayoutInflater mInflater;
    List<DeviceInfo>   mDatas;
    private static final int TYPE_ITEM   = 0;
    private static final int TYPE_FOOTER = 1;

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE     = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE     = 2;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;
    private OnItemClickListener mListener;


    public RefreshAdapter(Context context, List<DeviceInfo> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            //绑定viewHolder
            DeviceViewHolder viewHolder = new DeviceViewHolder(mInflater.
                    inflate(R.layout.contact_item01,parent,false));
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
        } else if (viewType == TYPE_FOOTER) {
            View itemView = mInflater.inflate(R.layout.load_more_footview_layout, parent, false);

            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DeviceViewHolder) {
            if (mDatas != null){
                ((DeviceViewHolder)holder).bindHolder(mDatas.get(position));
            }

        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    footerViewHolder.mTvLoadText.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerViewHolder.mTvLoadText.setText("正加载更多...");
                    break;
                case NO_LOAD_MORE:
                    //隐藏加载更多
                    footerViewHolder.mLoadLayout.setVisibility(View.GONE);
                    break;

            }
        }

    }

    @Override
    public int getItemCount() {
        //RecyclerView的count设置为数据总条数+ 1（footerView）
        return mDatas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }




    public void AddHeaderItem(List<DeviceInfo> items) {
        mDatas.addAll(0, items);
        notifyDataSetChanged();
    }

    public void AddFooterItem(List<DeviceInfo> items) {
        mDatas.addAll(items);
        notifyDataSetChanged();
    }

    public void checkTreeItem(List<DeviceInfo> items) {
        mDatas = items;
        notifyDataSetChanged();
    }

    /**
     * 更新加载更多状态
     * @param status
     */
    public void changeMoreStatus(int status){
        mLoadMoreStatus=status;
        notifyDataSetChanged();
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
