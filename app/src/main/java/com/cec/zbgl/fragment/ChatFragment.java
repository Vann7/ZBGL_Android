package com.cec.zbgl.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cec.zbgl.BaseApplication;
import com.cec.zbgl.R;
import com.cec.zbgl.adapter.DeviceAdapter;
import com.cec.zbgl.adapter.SimpleTreeListViewAdapter;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.utils.ToastUtils;
import com.cec.zbgl.utils.tree.Node;
import com.cec.zbgl.utils.tree.adapter.TreeListViewAdapter;

import org.litepal.parser.LitePalAttr;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private ListView mTree;
    private ListView cList;
    private List<SpOrgnization> orgs;
    private List<DeviceInfo> devices;
    private SimpleTreeListViewAdapter<SpOrgnization> mAdapter;
    private DeviceAdapter dAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab01, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTree = (ListView) getView().findViewById(R.id.id_lv_tree);
        cList = (ListView) getView().findViewById(R.id.id_lv_content);
        initDatas();
        try
        {
            mAdapter = new SimpleTreeListViewAdapter<SpOrgnization>(mTree, getContext(),
                    orgs, 0);
            mTree.setAdapter(mAdapter);

            dAdapter = new DeviceAdapter(getContext(),R.layout.content_wx,devices);
            cList.setAdapter(dAdapter);

        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        initEvent();
    }


    private void initEvent() {
        mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener()
        {
            @Override
            public void onClick(Node node, int position)
            {
                if (node.isLeaf())
                {
                    ToastUtils.showShort(node.getName());
                }
            }
        });

    }

    /**
     * 初始化模拟数据
     */
    private void initDatas() {
        orgs = new ArrayList<>();
        SpOrgnization org = new SpOrgnization("1","0","根目录1");
        orgs.add(org);
        org = new SpOrgnization("2","0","根目录2");
        orgs.add(org);
        org = new SpOrgnization("3","0","根目录3");
        orgs.add(org);
        org = new SpOrgnization("4","1","根目录1-1");
        orgs.add(org);
        org = new SpOrgnization("5","1","根目录1-2");
        orgs.add(org);

        devices = new ArrayList<>();
        DeviceInfo device;
        for (int i=0; i< 80; i++) {
            device = new DeviceInfo("tony"+i,i,"Beijing"+i);
            devices.add(device);
        }

    }



}
