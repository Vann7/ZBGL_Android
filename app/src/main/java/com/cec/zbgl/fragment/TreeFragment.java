package com.cec.zbgl.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.SimpleTreeListViewAdapter;
import com.cec.zbgl.model.SpOrgnization;

import java.util.List;


public class TreeFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.fragment_tree, container, false);
    }


}
