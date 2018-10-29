package com.cec.zbgl.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cec.zbgl.R;


public class TypeItemHolder extends RecyclerView.ViewHolder {

    public TextView mTitle;

    public TypeItemHolder(View viewHolder) {
        super(viewHolder);
        mTitle= (TextView) viewHolder.findViewById(R.id.course_item_tv2);
    }

    public void  bindHolder(String title) {
        mTitle.setText(title);

    }
}
