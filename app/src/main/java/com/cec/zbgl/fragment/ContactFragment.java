package com.cec.zbgl.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.CourseAdapter;
import com.cec.zbgl.adapter.CourseAdapter2;
import com.cec.zbgl.model.DeviceCourse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ContactFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CourseAdapter2 mAdapter;
    private List<DeviceCourse> courses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab02, container, false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),6);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
//                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                if (type == DeviceCourse.TYPE_ONE) {
                    return 2;
                }else if (type == DeviceCourse.TYPE_TWO){
                    return 3;
                } else {
                    return 6;
                }
             }
        });
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = layoutParams.getSpanSize();
                int spanIndex = layoutParams.getSpanIndex();
                outRect.top = 20;
                if (spanSize != gridLayoutManager.getSpanCount()) {
                    if (spanIndex == 1) {
                        outRect.left = 10;
                    }else {
                        outRect.right = 10;
                    }
                }
            }
        });

        mAdapter = new CourseAdapter2(getContext());
        mRecyclerView.setAdapter(mAdapter);

        initData();

    }

    private void initData() {
        courses = new ArrayList<>();
        for (int i=0; i< 40; i++) {
            String name,typyName;
            int type;
            if (i <15 ) {
                type = 1;
            }else if (15<=i && i<= 31) {
                type = 2;
            } else {
                type = 3;
            }
            if (type == 1){
                name = "图片教程";
                typyName = "图片";
            }else if (type == 2) {
                name = "视频教程";
                typyName = "视频";
            } else {
                name = "文档教程";
                typyName = "文档";
            }
            DeviceCourse course = new DeviceCourse(String.valueOf(i), name+": "+i, type, "教程类别为:"+typyName );
            courses.add(course);
        }
        mAdapter.addList(courses);
        mAdapter.notifyDataSetChanged();
    }

}
