package com.cec.zbgl.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceInfo;

//筛选Item Holder
public class FilterItemViewHolder extends RecyclerView.ViewHolder {


    private TextView name_tv;

    public TextView getName_tv() {
        return name_tv;
    }

    public FilterItemViewHolder(View itemView) {
        super(itemView);
        name_tv = (TextView) itemView.findViewById(R.id.filter_text);
    }

    public void bindHolder(String name) {
        name_tv.setText(name);
    }


}
