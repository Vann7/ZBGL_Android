package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cec.zbgl.R;
import com.cec.zbgl.holder.FilterHeadViewHolder;
import com.cec.zbgl.holder.FilterItemViewHolder;

import java.util.HashMap;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<HashMap<Integer, String>> datas;
    private LayoutInflater mInflater;
    private static final int TYPE_ITEM   = 1;
    private static final int TYPE_HEADER = 0;
    private OnItemClickListener mListener;
    int mSelect = 0;   //选中项

    public FilterAdapter(Context context, List<HashMap<Integer, String>> datas) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            FilterItemViewHolder holder = new FilterItemViewHolder(mInflater.inflate(R.layout.filter_item,parent,false));
            return holder;
        }else {
            FilterHeadViewHolder holder = new FilterHeadViewHolder(mInflater.inflate(R.layout.filter_head,parent,false));
            //绑定点击事件
            holder.itemView.setOnClickListener(v -> {
                mListener.onItemClick(v, holder.getLayoutPosition());

            });
            return holder;
        }

    }

    /**
     * 刷新方法
     * @param positon
     */
    public void changeSelected(int positon){
        if(positon != mSelect){
            mSelect = positon;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (datas.get(position).containsKey(0)) {
            ((FilterItemViewHolder)holder).bindHolder(datas.get(position).get(0));
        }else {
            ((FilterHeadViewHolder)holder).bindHolder(datas.get(position).get(1));
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (datas.get(position).containsKey(0)) { //标题
            return 0;
        } else {
            return 1;
        }
    }

    public void setOnListClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 自定义item点击接口
     */
    public interface OnItemClickListener {
        void onItemClick(View v, int position); //单击事件
    }
}
