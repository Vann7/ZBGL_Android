package com.cec.zbgl.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cec.zbgl.R;

//筛选Head Holder
public class FilterHeadViewHolder extends RecyclerView.ViewHolder {


    TextView name_tv;


    public FilterHeadViewHolder(View itemView) {
        super(itemView);
        name_tv = (TextView) itemView.findViewById(R.id.filter_head);
    }

    public void bindHolder(String name) {
        name_tv.setText(name);
    }


}
