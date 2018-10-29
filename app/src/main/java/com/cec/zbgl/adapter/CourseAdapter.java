package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.holder.TypeItemHolder;
import com.cec.zbgl.holder.TypeOneHolder;
import com.cec.zbgl.holder.TypeThreeHolder;
import com.cec.zbgl.holder.TypeTwoHolder;
import com.cec.zbgl.model.DeviceCourse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{

    private LayoutInflater mLayoutInflater;
    private List<DeviceCourse> mData;

    private static final int VIEW_TYPE_TITLE= 101;
    private static final int VIEW_TYPE_ITEM = 100;
    int IS_TITLE_OR_NOT =1;
    int MESSAGE = 2;
    int ColumnNum = 6;
//    List<Map<Integer, DeviceCourse>> mData;


    public CourseAdapter(Context context, List<DeviceCourse> mData) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mData = mData;

    }

    public CourseAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void addList(List<DeviceCourse> list) {
        mData.addAll(list);
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
        return new TypeItemHolder(mLayoutInflater.inflate(R.layout.course_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DeviceCourse course = mData.get(position);
        if("true".equals(course.getIsTitle())){
            ((TypeItemHolder)holder).bindHolder(course.getMessage());
        }else {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case   DeviceCourse.TYPE_ONE :
                    ((TypeOneHolder)holder).bindHolder(course);
                    break;
                case DeviceCourse.TYPE_TWO:
                    ((TypeTwoHolder)holder).bindHolder(course);
                    break;
                case DeviceCourse.TYPE_THREE:
                    ((TypeThreeHolder)holder).bindHolder(course);
                    break;
                case DeviceCourse.TYPE_ITEM:
                    ((TypeItemHolder)holder).bindHolder(course.getMessage());
                    break;
            }
        }





    }

    //判断RecyclerView的子项样式，返回一个int值表示
    @Override
    public int getItemViewType(int position) {
        DeviceCourse course = mData.get(position);
        if ("true".equals(course.getIsTitle())) {
            return VIEW_TYPE_TITLE;
        }else {

            return course.getDeviceType();
        }
    }

    //判断是否是title，如果是，title占满一行的所有子项，则是ColumnNum个，如果是item，占满一个子项
  /*  @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        //如果是title就占据2个单元格(重点)
        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if("false".equals(mData.get(position).getIsTitle())){
                    return 1;
                }else {
                    return ColumnNum;
                }
            }
        });
    }*/


    @Override
    public int getItemCount() {
        return mData.size();
    }






}
