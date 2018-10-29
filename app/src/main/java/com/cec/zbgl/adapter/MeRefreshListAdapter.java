package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cec.zbgl.R;

public class MeRefreshListAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{

    private static final int TYPE_FOOTER = -1;
    private RecyclerView.Adapter adapter;
    private LayoutInflater inflater;
    private boolean isShowFooter;
    private boolean isNoData;

    public MeRefreshListAdapter(RecyclerView.Adapter adapter, Context context, boolean isShowFooter, boolean isNoData) {
        this.adapter = adapter;
        this.isShowFooter = isShowFooter;
        this.isNoData = isNoData;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_FOOTER:
                View footer = inflater.inflate(R.layout.footer_layout, parent, false);
                viewHolder = new FooterHolder(footer);
                break;
            default:
                viewHolder = adapter.onCreateViewHolder(parent, viewType);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterHolder) {
            FooterHolder footerHolder = (FooterHolder) holder;
            footerHolder.progressBar.setVisibility(isNoData ? View.GONE : View.VISIBLE);
            footerHolder.message.setText(isNoData ? "--- 没有数据 ---" : "加载中……");
        }else{
            adapter.onBindViewHolder(holder,position);
        }
    }

    @Override
    public int getItemCount() {
        return isShowFooter ? adapter.getItemCount() + 1 : adapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowFooter && position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return adapter.getItemViewType(position);
        }
    }

    static class FooterHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView message;

        FooterHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.item_footer_progress);
            message = (TextView) itemView.findViewById(R.id.item_footer_message);
        }
    }

    public void setShowFooter(boolean flag) {
        this.isShowFooter = flag;
        this.notifyDataSetChanged();
    }

    public void setNoData(boolean flag) {
        this.isNoData = flag;
        this.notifyDataSetChanged();
    }

}
