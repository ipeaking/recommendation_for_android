package com.bo.bonews.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.bo.bonews.R;
import com.bo.bonews.activity.ChannelManagerActivity;
import com.bo.bonews.adapter.FixedPagerAdapter;
import com.bo.bonews.bean.ProjectChannelBean;
import com.bo.bonews.i.APPConst;
import com.bo.bonews.utils.CategoryDataUtils;
import com.bo.bonews.utils.IOUtils;
import com.bo.bonews.utils.ListDataSave;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewsFragment extends BaseFragment {

    private TabLayout mTabLayout;
    private ViewPager mNewsViewpager;
    private FixedPagerAdapter fixedPagerAdapter;
    private List<BaseFragment> fragments;
    private List<ProjectChannelBean> myChannelList;
    private List<ProjectChannelBean> moreChannelList;
    private ImageButton mChange_channel;
    private BaseFragment baseFragment;

    private boolean isFirst;

    private SharedPreferences sharedPreferences;

    // 当前新闻频道的位置
    private int tabPosition;

    private ListDataSave listDataSave;

    @Override
    protected int getLayoutId() {
        return R.layout.tablayout_pager;
    }

    @Override
    protected void initView() {
        mTabLayout = (TabLayout) mRootView.findViewById(R.id.tab_layout);
        mNewsViewpager = (ViewPager) mRootView.findViewById(R.id.news_viewpager);
        mChange_channel = (ImageButton) mRootView.findViewById(R.id.change_channel);
        initValidata();
        ImmersionBar.with(this)
                .statusBarView(R.id.view)
                .navigationBarColor(R.color.colorTheme)
                .init();
    }

    /**
     * 在ManiActivty中被调用，当从ChanelActivity返回时设置当前tab的位置
     *
     * @param tabPosition
     */
    public void setCurrentChannel(int tabPosition) {
        mNewsViewpager.setCurrentItem(tabPosition);
        mTabLayout.setScrollPosition(tabPosition, 1, true);
    }

    /**
     * 在myChannelList发生改变的时候更新ui，在MainActivity调用
     */
    public void notifyChannelChange() {
        getDataFromSharedPreference();
        fixedPagerAdapter.setChannelBean(myChannelList);
        fixedPagerAdapter.setFragments(fragments);
        fixedPagerAdapter.notifyDataSetChanged();

    }

    public void initValidata() {
        sharedPreferences = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
        listDataSave = new ListDataSave(getActivity(), "channel");
        fragments = new ArrayList<BaseFragment>();
        fixedPagerAdapter = new FixedPagerAdapter(getChildFragmentManager());
        mTabLayout.setupWithViewPager(mNewsViewpager);
        bindData();
    }

    public void bindData() {
        getDataFromSharedPreference();
        fixedPagerAdapter.setChannelBean(myChannelList);
        fixedPagerAdapter.setFragments(fragments);
        mNewsViewpager.setAdapter(fixedPagerAdapter);
    }

    @Override
    protected void setListener() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mChange_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChannelManagerActivity.class);
                intent.putExtra("TABPOSITION", tabPosition);
                startActivityForResult(intent, 999);
            }
        });
    }


    /**
     * 从Asset目录中读取更多频道
     *
     * @return
     */
    public List<ProjectChannelBean> getMoreChannelFromAsset() {
        String moreChannel = IOUtils.readFromFile("projectChannel.txt");
        List<ProjectChannelBean> projectChannelBeanList = new ArrayList<>();
        JsonArray array = new JsonParser().parse(moreChannel).getAsJsonArray();
        for (final JsonElement elem : array) {
            projectChannelBeanList.add(new Gson().fromJson(elem, ProjectChannelBean.class));
        }
        return projectChannelBeanList;
    }

    /**
     * 判断是否第一次进入程序
     * 如果第一次进入，直接获取设置好的频道
     * 如果不是第一次进入，则从sharedPrefered中获取设置好的频道
     */
    private void getDataFromSharedPreference() {
        isFirst = sharedPreferences.getBoolean("isFirst", true);
        if (isFirst) {
            myChannelList = CategoryDataUtils.getChannelCategoryBeans();
            moreChannelList = getMoreChannelFromAsset();
            myChannelList = setType(myChannelList);
            moreChannelList = setType(moreChannelList);
            listDataSave.setDataList("myChannel", myChannelList);
            listDataSave.setDataList("moreChannel", moreChannelList);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isFirst", false);
            edit.commit();
        } else {
            myChannelList = listDataSave.getDataList("myChannel", ProjectChannelBean.class);
        }
        fragments.clear();
        for (int i = 0; i < myChannelList.size(); i++) {
            baseFragment = NewsListFragment.newInstance(myChannelList.get(i).getTid());

            fragments.add(baseFragment);
        }
        if (myChannelList.size() <= 4) {
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

    }

    private List<ProjectChannelBean> setType(List<ProjectChannelBean> list) {
        Iterator<ProjectChannelBean> iterator = list.iterator();
        while (iterator.hasNext()) {
            ProjectChannelBean channelBean = iterator.next();
            channelBean.setTabType(APPConst.ITEM_EDIT);
        }
        return list;
    }

}
