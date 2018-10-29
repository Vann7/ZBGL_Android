package com.cec.zbgl.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cec.zbgl.R;

public class FooterViewHolder extends RecyclerView.ViewHolder {

   public ProgressBar mPbLoad;
   public TextView mTvLoadText;
   public LinearLayout mLoadLayout;
    public FooterViewHolder(View itemView) {
        super(itemView);
        mPbLoad = (ProgressBar) itemView.findViewById(R.id.pbLoad);
        mTvLoadText = (TextView) itemView.findViewById(R.id.tvLoadText);
        mLoadLayout = (LinearLayout) itemView.findViewById(R.id.loadLayout);
    }


}
