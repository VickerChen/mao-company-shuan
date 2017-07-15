package com.moscase.shouhuan;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.moscase.shouhuan.fragment.XinlvFragment;
import com.moscase.shouhuan.fragment.ZhibiaoFragment;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends FragmentActivity {
    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private View view1;
    private View view2;
    private List<View> viewList;
    private RelativeLayout mRelativeLayout;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private XinlvFragment mXinlvFragment;
    private ZhibiaoFragment mZhibiaoFragment;
    private FrameLayout mFrameLayout;
    private CircleIndicator mIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
        initEvents();


        initBottomBar();


    }

    //底部的三个bar
    private void initBottomBar() {
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("状态", R.drawable.zhuangtai);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("心率", R.drawable.healthy);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("指标", R.drawable.zhibiao, R
                .color.color_tab_1);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                mFragmentTransaction = mFragmentManager.beginTransaction();
                if (position == 0){
                    mFrameLayout.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);
                    mIndicator.setVisibility(View.VISIBLE);
                }else if (position == 1){
                    mFrameLayout.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.GONE);
                    mIndicator.setVisibility(View.GONE);
                    if (mXinlvFragment == null){
                        mXinlvFragment = new XinlvFragment(MainActivity.this);
                    }
                    mFragmentTransaction.replace(R.id.fl_content,mXinlvFragment);
                }else if (position == 2){
                    mFrameLayout.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.GONE);
                    mIndicator.setVisibility(View.GONE);
                    if (mZhibiaoFragment == null){
                        mZhibiaoFragment = new ZhibiaoFragment();
                    }
                    mFragmentTransaction.replace(R.id.fl_content,mZhibiaoFragment);
                }

                mFragmentTransaction.commit();
                return true;
            }
        });
    }

    //打开右侧抽屉
    public void OpenRightMenu(View view) {
        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }


    //初始化抽屉事件
    private void initEvents() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("LEFT")) {
                    float leftScale = 1 - 0.3f * scale;
                    ViewHelper.setScaleX(mMenu, leftScale);
                    ViewHelper.setScaleY(mMenu, leftScale);
                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                } else {
                    ViewHelper.setTranslationX(mContent,
                            -mMenu.getMeasuredWidth() * slideOffset);
                    mContent.invalidate();
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.RIGHT);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.zhujiemian);
        viewList = new ArrayList<>();
        mFragmentManager = getSupportFragmentManager();
        mFrameLayout = (FrameLayout) findViewById(R.id.fl_content);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mXinlvFragment = new XinlvFragment(MainActivity.this);
                mZhibiaoFragment = new ZhibiaoFragment();
            }
        },500);
        mIndicator = (CircleIndicator) findViewById(R.id.indicator);
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.zhuangtai1, null);
        view2 = inflater.inflate(R.layout.zhuangtai2, null);
        viewList.add(view1);
        viewList.add(view2);

        mViewPager.setAdapter(pagerAdapter);
        mIndicator.setViewPager(mViewPager);
    }

    //ViewPager的Adapter
    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    };

    //打开左侧抽屉
    public void OpenLeftMenu(View view) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }


}
