package com.moscase.shouhuan.utils;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 陈航 on 2017/3/4.
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    /**
     * item 布局 id
     */
    private int mLayoutId;

    /**
     * 数据源
     */
    private List<T> mDatas;

    public CommonAdapter(int layoutId, List<T> datas) {
        mLayoutId = layoutId;
        mDatas = datas;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.get(parent.getContext(), parent, mLayoutId);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int  position) {
        convert(holder, mDatas.get(position));
    }

    public abstract void convert(ViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
