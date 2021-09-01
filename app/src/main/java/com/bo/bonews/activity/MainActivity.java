package com.bo.bonews.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bo.bonews.AppManager;
import com.bo.bonews.R;
import com.bo.bonews.bean.BottomTab;
import com.bo.bonews.event.NetworkEvent;
import com.bo.bonews.fragment.AboutFragment;
import com.bo.bonews.fragment.NewsFragment;
import com.bo.bonews.fragment.SplashFragment;
import com.bo.bonews.view.FragmentTabHost;
import com.gyf.immersionbar.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.iv_bg)
    ImageView ivBg;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(android.R.id.tabhost)
    FragmentTabHost mTabHost;

    private LayoutInflater mInflater;
    private List<BottomTab> mBottomTabs = new ArrayList<>(5);

    /**
     * splash页面
     */
    private SplashFragment mSplashFragment;

    private long mFirstPressedTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        showSplash();
        initTab();
    }

    // 初始化底部标签栏
    private void initTab() {
        // 新闻标签
        BottomTab bottomTab_news = new BottomTab(NewsFragment.class, R.string.news_fragment, R.drawable.select_icon_news);
        // 我 标签
        BottomTab bottomTab_about = new BottomTab(AboutFragment.class, R.string.about_fragment, R.drawable.select_icon_about);


        mBottomTabs.add(bottomTab_news);
        mBottomTabs.add(bottomTab_about);


        // 设置FragmentTab
        mInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);


        for (BottomTab bottomTab : mBottomTabs) {

            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getString(bottomTab.getTitle()));

            tabSpec.setIndicator(buildIndicator(bottomTab));

            mTabHost.addTab(tabSpec, bottomTab.getFragment(), null);

        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {


            }
        });

        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabHost.setCurrentTab(0);

    }

    // 设置底部tab的图片和文字
    private View buildIndicator(BottomTab bottomTab) {

        View view = mInflater.inflate(R.layout.tab_indicator, null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);

        img.setBackgroundResource(bottomTab.getIcon());
        text.setText(bottomTab.getTitle());

        return view;
    }


    /**
     * 展示Splash
     */
    private void showSplash() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mSplashFragment = (SplashFragment) getSupportFragmentManager().findFragmentByTag(SplashFragment.class.getSimpleName());
        if (mSplashFragment != null) {
            if (mSplashFragment.isAdded()) {
                transaction.show(mSplashFragment).commitAllowingStateLoss();
            } else {
                transaction.remove(mSplashFragment).commitAllowingStateLoss();
                mSplashFragment = SplashFragment.newInstance();
                transaction.add(R.id.fl_content, mSplashFragment, SplashFragment.class.getSimpleName()).commitAllowingStateLoss();
            }
        } else {
            mSplashFragment = SplashFragment.newInstance();
            transaction.add(R.id.fl_content, mSplashFragment, SplashFragment.class.getSimpleName()).commitAllowingStateLoss();
        }
        mSplashFragment.setOnSplashListener((time, totalTime) -> {
            if (time != 0) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).titleBar(R.id.toolbar).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawer.removeDrawerListener(this);
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDrawerSlide(@NonNull View view, float v) {

    }

    @Override
    public void onDrawerOpened(@NonNull View view) {

    }

    @Override
    public void onDrawerClosed(@NonNull View view) {

    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mSplashFragment != null) {
                if (mSplashFragment.isFinish()) {
                    if (System.currentTimeMillis() - mFirstPressedTime < 2000) {
                        super.onBackPressed();
                        AppManager.getInstance().removeAllActivity();
                    } else {
                        Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                        mFirstPressedTime = System.currentTimeMillis();
                    }
                } else {
                    super.onBackPressed();
                    AppManager.getInstance().removeAllActivity();
                }
            } else {
                super.onBackPressed();
                AppManager.getInstance().removeAllActivity();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String tag = mTabHost.getCurrentTabTag();
        if (resultCode == 789) {
            Bundle bundle = data.getExtras();
            int tabPosition = bundle.getInt("NewTabPostion");
            NewsFragment newsFragment = (NewsFragment) getSupportFragmentManager().findFragmentByTag(tag);
            newsFragment.setCurrentChannel(tabPosition);
            newsFragment.notifyChannelChange();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkEvent(NetworkEvent networkEvent) {
        if (networkEvent.isAvailable()) {
            Toast.makeText(this, "网络已经恢复！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "网络貌似出了点问题！", Toast.LENGTH_SHORT).show();
        }
    }

}
