package com.cec.zbgl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cec.zbgl.R;
import com.cec.zbgl.holder.FilterHeadViewHolder;
import com.cec.zbgl.holder.FilterItemViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<HashMap<Integer, String>> datas;
    private LayoutInflater mInflater;
    private static final int TYPE_ITEM   = 1;
    private static final int TYPE_HEADER = 0;
    private OnItemClickListener mListener;
    int mSelect = -1;   //选中项
    String itemName = "";
    private Map<String, Integer> selectedMaps = new HashMap<>();
    private int type_one;
    private int type_two;
    private int type_three;


    public FilterAdapter(Context context, List<HashMap<Integer, String>> datas) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.datas = datas;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_HEADER) {
            viewHolder = new FilterHeadViewHolder(mInflater.inflate(R.layout.filter_head,parent,false));
            return viewHolder;
        }else {
            viewHolder = new FilterItemViewHolder(mInflater.inflate(R.layout.filter_item,parent,false));
//            //绑定点击事件
//            viewHolder.itemView.setOnClickListener(v -> {
//                mListener.onItemClick(v, viewHolder.getLayoutPosition());
//
//            });
            return viewHolder;
        }

    }

    /**
     * 刷新方法
     * @param positon
     */
    public void changeSelected(int positon,Map<String, Integer> selectedMaps,String itemName){
        mSelect = positon;
        this.selectedMaps = selectedMaps;
        this.itemName = itemName;
        notifyDataSetChanged();
    }

    public void clearSelected(Map<String, Integer> selectedMaps ) {
        this.selectedMaps = selectedMaps;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (datas.get(position).containsKey(TYPE_HEADER)) {
            FilterHeadViewHolder headViewHolder = (FilterHeadViewHolder) holder;
            headViewHolder.bindHolder(datas.get(position).get(TYPE_HEADER));
        }else {
            FilterItemViewHolder itemViewHolder = (FilterItemViewHolder) holder;
            //绑定点击事件
            itemViewHolder.itemView.setOnClickListener(v -> {

                mListener.onItemClick(v, itemViewHolder.getLayoutPosition());
                mSelect = position;
//                notifyDataSetChanged();

            });
            itemViewHolder.bindHolder(datas.get(position).get(TYPE_ITEM));
            if (selectedMaps.size() == 0) {
                itemViewHolder.name_tv.setBackgroundColor(Color.parseColor("#ffffff"));
                itemViewHolder.name_tv.setTextColor(Color.BLACK);
            } else {
                if (mSelect == position) {
                    if (selectedMaps.get(datas.get(mSelect).get(TYPE_ITEM)) !=null) {
                        itemViewHolder.name_tv.setBackgroundColor(Color.rgb(23,130,210));
                        itemViewHolder.name_tv.setTextColor(Color.WHITE);
                    }else {
                        itemViewHolder.name_tv.setBackgroundColor(Color.parseColor("#ffffff"));
                        itemViewHolder.name_tv.setTextColor(Color.BLACK);
                    }
                }else {
                    if (selectedMaps.get(datas.get(position).get(TYPE_ITEM)) != null){
                        itemViewHolder.name_tv.setBackgroundColor(Color.rgb(23,130,210));
                        itemViewHolder.name_tv.setTextColor(Color.WHITE);
                    }else {
                        itemViewHolder.name_tv.setBackgroundColor(Color.parseColor("#ffffff"));
                        itemViewHolder.name_tv.setTextColor(Color.BLACK);
                    }
                }
            }

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
