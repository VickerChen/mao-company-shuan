package com.moscase.shouhuan.fragment;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.bean.HeartTimes;
import com.moscase.shouhuan.utils.GuideView;
import com.moscase.shouhuan.view.DigitalGroupView;
import com.moscase.shouhuan.view.DividerItemDecoration;
import com.moscase.shouhuan.view.HeartbeatView;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by 陈航 on 2017/7/20.
 *
 * 少年一事能狂  敢骂天地不仁
 */
@SuppressLint("ValidFragment")
public class XinlvFragment extends Fragment {
    private HeartbeatView mHeartbeatView;
    private RecyclerView mHeartbeatRecycler;
    private List<HeartbeatEntity> mData = new ArrayList<>();
    private Adapter mAdapter;
    private DigitalGroupView mDigiResult;
    private TextView mTextUnit;
    private Calendar mC;
    private List<HeartTimes> mHeartTimesList = new ArrayList<>();
    private int HeartTimeId = 0;
    private GuideView guideView;
    private Context mContext;

    @SuppressLint("ValidFragment")
    public XinlvFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_xinlv, container, false);

        initView(view);

        initDatas();

        return view;
    }

    private void initView(View view) {
        SQLiteDatabase db = Connector.getDatabase();
        mC = Calendar.getInstance();
        mHeartbeatView = (HeartbeatView) view.findViewById(R.id.heartbeat);
        mDigiResult = (DigitalGroupView) view.findViewById(R.id.digi_heartbeat_result);
        mTextUnit = (TextView) view.findViewById(R.id.text_unit);
        mHeartbeatRecycler = (RecyclerView) view.findViewById(R.id.recycler_heartbeat);
    }

    private void initDatas() {


        mHeartbeatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeartbeatView.startAnim();
                hideResult();
            }
        });

        mHeartbeatView.setHeartBeatAnimListener(new HeartbeatView.HeartBeatAnimImpl() {
            @Override
            public void onAnimFinished() {
                if (mHeartbeatView.isAutoEnd()){
                    showResult();
                    Log.d("koma","到了这一步");
                }else {
                    int randomNum = (int) (50 + Math.random() * 50);
                    HeartbeatEntity e = new HeartbeatEntity();
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date = sDateFormat.format(new java.util.Date());
                    e.date = date;
                    e.datum = String.valueOf(randomNum);
                    HeartTimes mHeartTimes = new HeartTimes();
                    mHeartTimes.setTime(e.date);
                    mHeartTimes.setTimes(e.datum);
                    mHeartTimes.setDate(new Date());
                    mHeartTimes.setId(HeartTimeId);
                    HeartTimeId += 1;
                    mHeartTimes.save();
                    mHeartTimesList.add(0, mHeartTimes);

                    mAdapter.notifyItemInserted(0);
                    mHeartbeatRecycler.scrollToPosition(0);
                    showResult();
                    mDigiResult.setDigits(randomNum);
                }
            }
        });

        List<HeartTimes> heartTimes = DataSupport.where("times > ?", "0").order("mDate desc")
                .find(HeartTimes.class);
        for (HeartTimes heartTime : heartTimes) {
            HeartTimes e = new HeartTimes();
            e.setTime(heartTime.getTime());
            e.setTimes(heartTime.getTimes());
            e.setId(heartTime.getId());
            mHeartTimesList.add(e);
        }

        //这里使用了自定义的WrapContentLinearLayoutManager是因为我用的第三方的adapter，在设置emptyview的时候在底层
        //直接就报错了
        mHeartbeatRecycler.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mHeartbeatRecycler.addItemDecoration(new DividerItemDecoration(mContext,
                LinearLayoutManager.VERTICAL));
        mHeartbeatRecycler.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new Adapter(R.layout.item_heartbeat, mHeartTimesList);
        mAdapter.setEmptyView(R.layout.activity_splash, (ViewGroup) mHeartbeatRecycler.getParent());
        mHeartbeatRecycler.setAdapter(mAdapter);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int
                    position) {
                new AlertDialog.Builder(mContext).setMessage("是否删除此数据").setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DataSupport.delete(HeartTimes.class, DataSupport.find(HeartTimes.class,
                                        mHeartTimesList.get(position).getId()).getId());
                                mHeartTimesList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                mDigiResult.setDigits(0);
                            }
                        }).setNegativeButton("否", null).show();
                return true;
            }
        });

    }

    private void hideResult() {
        AlphaAnimation mHiddenAction = new AlphaAnimation(1f, 0f);
        mHiddenAction.setDuration(400);

        mTextUnit.setAnimation(mHiddenAction);
        mDigiResult.setAnimation(mHiddenAction);

        mTextUnit.setVisibility(View.GONE);
        mDigiResult.setVisibility(View.GONE);
    }

    private void showResult() {
        AlphaAnimation mHiddenAction = new AlphaAnimation(1f, 0f);
        mHiddenAction.setDuration(400);

        mTextUnit.setAnimation(mHiddenAction);
        mDigiResult.setAnimation(mHiddenAction);

        mTextUnit.setVisibility(View.VISIBLE);
        mDigiResult.setVisibility(View.VISIBLE);
    }

    class HeartbeatEntity {
        String date;
        String datum;
    }


//    private void setGuideView() {
//        ImageView iv = new ImageView(mContext);
//        iv.setImageResource(R.drawable.img_new_task_guide1);
////        TextView textView = new TextView(mContext);
////        textView.setText("点击开始测量心跳");
//
//        guideView = GuideView.Builder
//                .newInstance(mContext)
//                .setTargetView(mDigiResult)//设置目标
//                .setCustomGuideView(iv)
//                .setDirction(GuideView.Direction.LEFT_BOTTOM)
//                .setShape(GuideView.MyShape.CIRCULAR)   // 设置圆形显示区域，
//                .setBgColor(getResources().getColor(R.color.shadow))
//                .setOnclickListener(new GuideView.OnClickCallback() {
//                    @Override
//                    public void onClickedGuideView() {
//                        guideView.hide();
//                    }
//                }).build();
//        guideView.show();
//    }

    @Override
    public void onResume() {
        super.onResume();
//        setGuideView();
    }

    public class Adapter extends BaseQuickAdapter<HeartTimes, BaseViewHolder> {

        @Override
        protected void startAnim(Animator anim, int index) {
            super.startAnim(anim, index);
            if (index < mHeartTimesList.size())
                anim.setStartDelay(index * 150);
        }

        public Adapter(@LayoutRes int layoutResId, @Nullable List<HeartTimes> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, HeartTimes item) {
            helper.setText(R.id.date, item.getTime())
                    .setText(R.id.datum, item.getTimes());
        }
    }


    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden){
            mHeartbeatView.stopAnim();
        }
    }

}
