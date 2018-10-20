package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cec.zbgl.R;
import com.cec.zbgl.holder.TypeOneHolder;
import com.cec.zbgl.holder.TypeThreeHolder;
import com.cec.zbgl.holder.TypeTwoHolder;
import com.cec.zbgl.model.DeviceCourse;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{

    private LayoutInflater mLayoutInflater;
    private List<DeviceCourse> courses;

    public CourseAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        courses = new ArrayList<>();
    }

    public void addList(List<DeviceCourse> list) {
        courses.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    @Override
    public int getItemViewType(int position) {
        return courses.get(position).getDeviceType();
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
