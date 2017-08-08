package com.moscase.shouhuan.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.fragment.XinlvFragment;
import com.moscase.shouhuan.fragment.ZhibiaoFragment;
import com.moscase.shouhuan.fragment.ZhuangtaiFragment;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private DrawerLayout mDrawerLayout;
    private XinlvFragment mXinlvFragment;
    private ZhibiaoFragment mZhibiaoFragment;
    private ZhuangtaiFragment mZhuangtaiFragment;
    private AHBottomNavigation mBottomNavigation;

//    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
        initEvents();
        initBottomBar();
        //这个是ViewPager的监听事件，这里我做了两手准备，本来用的是fragment的来回切换，用add方法
        //后来想想又用了ViewPager的嵌套滑动切换fragment，想换回ViewPager的话那就在代码和布局里面相互注释和反注释掉就好了
//        initDatas();

        setDefaultFragment();

    }

//    private void initDatas() {
//        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int
//                    positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (position == 0) {
//                    mBottomNavigation.setCurrentItem(0);
//                } else if (position == 1) {
//                    mBottomNavigation.setCurrentItem(1);
//                } else if (position == 2) {
//                    mBottomNavigation.setCurrentItem(2);
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }


    //底部的三个bar
    private void initBottomBar() {
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("状态", R.drawable.zhuangtai);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("心率", R.drawable.healthy);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("指标", R.drawable.zhibiao);

        // Add items
        mBottomNavigation.addItem(item1);
        mBottomNavigation.addItem(item2);
        mBottomNavigation.addItem(item3);
        mBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position == 0) {
                    if (mBottomNavigation.getCurrentItem() == 0) {
                        return true;
                    } else {
                        setDefaultFragment();
//                        mViewPager.setCurrentItem(0);
                    }
                } else if (position == 1) {
                    if (mBottomNavigation.getCurrentItem() == 1) {
                        return true;
                    } else {
                        initXinlvFragment();
//                        mViewPager.setCurrentItem(1);
                    }
                } else if (position == 2) {
                    if (mBottomNavigation.getCurrentItem() == 2) {
                        return true;
                    } else {
                        initZhibiaoFragment();
//                        mViewPager.setCurrentItem(2);
                    }
                }
                return true;
            }
        });
    }

    //打开右侧抽屉
    public void OpenRightMenu(View view) {
        mDrawerLayout.openDrawer(Gravity.RIGHT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.RIGHT);
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
                    ViewHelper.setAlpha(mMenu, 1);
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
//                此行代码表示锁定右侧的抽屉，只能通过点击右上角才能滑出抽屉，不能通过侧滑
//                mDrawerLayout.setDrawerLockMode(
//                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }

    private void initView() {
        mBottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
//        此行代码表示锁定右侧的抽屉，只能通过点击右上角才能滑出抽屉，不能通过侧滑
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
//        此行代码使抽屉拉出后屏幕不变暗
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
//        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(mZhuangtaiFragment);
        mFragmentList.add(mXinlvFragment);
        mFragmentList.add(mZhibiaoFragment);
//        mViewPager.setAdapter(mFragmentPagerAdapter);
//        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    //打开左侧抽屉
    public void OpenLeftMenu(View view) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (mZhuangtaiFragment != null) {
            transaction.hide(mZhuangtaiFragment);
        }
        if (mXinlvFragment != null) {
            transaction.hide(mXinlvFragment);
        }
        if (mZhibiaoFragment != null) {
            transaction.hide(mZhibiaoFragment);
        }
    }

    private void setDefaultFragment() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (mZhuangtaiFragment == null) {
            mZhuangtaiFragment = new ZhuangtaiFragment();
            transaction.add(R.id.fl_content, mZhuangtaiFragment);
        }
        hideFragment(transaction);
        transaction.show(mZhuangtaiFragment);
        transaction.commit();
    }

    private void initXinlvFragment() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (mXinlvFragment == null) {
            mXinlvFragment = new XinlvFragment(this);
            transaction.add(R.id.fl_content, mXinlvFragment);
        }
        hideFragment(transaction);
        transaction.show(mXinlvFragment);
        transaction.commit();
    }

    private void initZhibiaoFragment() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (mZhibiaoFragment == null) {
            mZhibiaoFragment = new ZhibiaoFragment(this);
            transaction.add(R.id.fl_content, mZhibiaoFragment);
        }
        hideFragment(transaction);
        transaction.show(mZhibiaoFragment);
        transaction.commit();
    }

    FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter
            (getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    };


}
