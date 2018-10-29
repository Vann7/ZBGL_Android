package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.holder.TypeOneHolder;
import com.cec.zbgl.holder.TypeThreeHolder;
import com.cec.zbgl.holder.TypeTwoHolder;
import com.cec.zbgl.model.DeviceCourse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseAdapter2 extends RecyclerView.Adapter <RecyclerView.ViewHolder>{

    private LayoutInflater mLayoutInflater;
    private List<DeviceCourse> courses;

    private static final int VIEW_TYPE_TITLE= 101;
    private static final int VIEW_TYPE_ITEM = 100;
    int IS_TITLE_OR_NOT =1;
    int MESSAGE = 2;
    int ColumnNum = 6;
    List<Map<Integer, String>> mData;

    public CourseAdapter2(Context context, List<Map<Integer, String>> mData) {
        mLayoutInflater = LayoutInflater.from(context);
        courses = new ArrayList<>();
        this.mData = mData;

    }

    public CourseAdapter2(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        courses = new ArrayList<>();
    }

    public void addList(List<DeviceCourse> list) {
        courses.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //判断viewtype类型返回不同Viewholder
        switch (viewType) {
            case  DeviceCourse.TYPE_ONE :
                return new TypeOneHolder(mLayoutInflater.inflate(R.layout.contact_item01,parent,false));
            case   DeviceCourse.TYPE_TWO:
                return new TypeTwoHolder(mLayoutInflater.inflate(R.layout.contact_item02,parent,false));
            case   DeviceCourse.TYPE_THREE:
                return new TypeThreeHolder(mLayoutInflater.inflate(R.layout.contact_item03,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


            int viewType = getItemViewType(position);
//        ((TypeOneHolder)holder).bindHolder(courses.get(position));
            switch (viewType) {
                case   DeviceCourse.TYPE_ONE :
                    ((TypeOneHolder)holder).bindHolder(courses.get(position));
                    break;
                case DeviceCourse.TYPE_TWO:
                    ((TypeTwoHolder)holder).bindHolder(courses.get(position));
                    break;
                case DeviceCourse.TYPE_THREE:
                    ((TypeThreeHolder)holder).bindHolder(courses.get(position));
                    break;
            }






    }

    //判断RecyclerView的子项样式，返回一个int值表示
    @Override
    public int getItemViewType(int position) {
            return courses.get(position).getDeviceType();
    }




    @Override
    public int getItemCount() {
        return courses.size();
    }


    public class HolderItem extends RecyclerView.ViewHolder {
        public TextView mTitle;

        public HolderItem(View viewHolder) {
            super(viewHolder);
            mTitle= (TextView) viewHolder.findViewById(R.id.course_item_tv2);
        }

        public void  bindHolder(String title) {
            mTitle.setText(title);

        }
    }



}
