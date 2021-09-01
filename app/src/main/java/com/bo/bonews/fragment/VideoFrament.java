package com.bo.bonews.fragment;

import com.bo.bonews.R;
import com.gyf.immersionbar.ImmersionBar;

public class VideoFrament extends  BaseFragment{
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this)
                .statusBarView(R.id.view)
                .navigationBarColor(R.color.colorTheme)
                .init();
    }
}
