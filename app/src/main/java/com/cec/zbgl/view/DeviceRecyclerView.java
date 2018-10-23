package com.cec.zbgl.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.cec.zbgl.R;
import com.cec.zbgl.listener.ILoadListener;
import com.cec.zbgl.listener.IReflashListener;

public class DeviceRecyclerView  extends RecyclerView implements AbsListView.OnScrollListener {

    private View header;// 顶部布局文件；
    private View footer;//底部布局文件
    private int headerHeight;// 顶部布局文件的高度；
    private int firstVisibleItem;// 当前第一个可见的item的位置；
    private int lastVisibleItem;// 最后一个可见的item的位置；
    private int totalItemCount;// 总数量；
    private int scrollState;// listview 当前滚动状态；
    private boolean isRemark;// 标记，当前是在listview最顶端摁下的；
    private int startY;// 摁下时的Y值；
    private boolean isLoading;// 正在加载；
    private int state;// 当前的状态；
    private final int NONE = 0;// 正常状态；
    private final int PULL = 1;// 提示下拉状态；
    private final int RELESE = 2;// 提示释放状态；
    private final int REFLASHING = 3;// 刷新状态；

    IReflashListener iReflashListener;//刷新数据的接口
    ILoadListener iLoadListener; //加载更多数据的接口

    public DeviceRecyclerView(Context context) {
        super(context);
        initView(context);
    }


    /**
     * 初始化界面，添加顶部布局文件到 listview
     *
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //添加header
        header = inflater.inflate(R.layout.header_layout, null);
        measureView(header);
        headerHeight = header.getMeasuredHeight();
        topPadding(-headerHeight);
//        this.addHeaderView(header);

        //添加footer
        footer = inflater.inflate(R.layout.footer_layout, null);
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
//        this.addFooterView(footer);

//        this.setOnScrollListener(this);
    }

    /**
     * 通知父布局，占用的宽，高；
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight,
                    MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * 设置header 布局 上边距；
     *
     * @param topPadding
     */
    private void topPadding(int topPadding) {
        header.setPadding(header.getPaddingLeft(), topPadding,
                header.getPaddingRight(), header.getPaddingBottom());
        header.invalidate();
    }

    public DeviceRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public DeviceRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
