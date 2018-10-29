package com.cec.zbgl.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.support.v4.widget.SwipeRefreshLayout;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.MeRefreshListAdapter;
import com.cec.zbgl.listener.ILoadListener;
import com.cec.zbgl.listener.IReflashListener;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class MeRecyclerView extends LinearLayout implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView refreshList;
    private AppCompatTextView listTip; //没有数据
    private ILoadListener loadListener;
    private IReflashListener reflashListener;
    private MeRefreshListAdapter meRefreshListAdapter;
    private Context mContext;
    private RecyclerView.LayoutManager layoutManager;
    private boolean isShowFooter;
    private boolean isNoData;
    private int lastVisibleItem;

    public MeRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public MeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public MeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MeRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.activity_demo2, this, true);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.list_swipe_refresh);
        listTip = (AppCompatTextView) findViewById(R.id.list_tip_message);
        refreshList = (RecyclerView) findViewById(R.id.list_list);

        refreshLayout.setOnRefreshListener(this);

        refreshList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int totalCount = layoutManager.getItemCount() - 1;
                if (totalCount > 18 && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == totalCount && !isNoData && !isShowFooter) {
                    if (null != loadListener) {
                        setFooter();
                        loadListener.onload();
                    }
                }

//                if (totalCount == lastVisibleItem && newState == SCROLL_STATE_IDLE ) {
//                    if (null != loadListener) {
//                        setFooter();
//                        loadListener.onload();
//                    }
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (layoutManager instanceof LinearLayoutManager) {
                    lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                }
            }
        });

    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        refreshList.setLayoutManager(layoutManager);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        meRefreshListAdapter = new MeRefreshListAdapter(adapter, mContext, isShowFooter, isNoData);
        refreshList.setAdapter(meRefreshListAdapter);
    }

    public void notifyDataSetChanged() {
        meRefreshListAdapter.notifyDataSetChanged();
    }



    public void stopRefresh(int pageCount, boolean isNoData) {
        this.isNoData = isNoData;
        meRefreshListAdapter.setNoData(isNoData);
        showData(meRefreshListAdapter.getItemCount() > 0);

        refreshLayout.setRefreshing(false);

        if (pageCount == 2) {

        } else {
            if (!isNoData) {
                isShowFooter = false;
                meRefreshListAdapter.setShowFooter(isShowFooter);
            }
        }
    }

    private void showData(boolean b) {
        refreshList.setVisibility(b ? VISIBLE : VISIBLE);
        listTip.setVisibility(b ? GONE : VISIBLE);
    }


    private void setFooter() {
        isShowFooter = true;
        meRefreshListAdapter.setShowFooter(true);
    }

    @Override
    public void onRefresh() {
        if (null != loadListener) {
            isNoData = false;
            isShowFooter = false;
            meRefreshListAdapter.setNoData(isNoData);
            meRefreshListAdapter.setShowFooter(isShowFooter);
            reflashListener.onReflash();
        }
    }

    public void startRefresh() {
        refreshLayout.setRefreshing(true);
        onRefresh();
    }

    public void setLoadListener(ILoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public void setReflashListener(IReflashListener reflashListener) {
        this.reflashListener = reflashListener;
    }

    public MeRefreshListAdapter getMeRefreshListAdapter() {
        return meRefreshListAdapter;
    }
}
