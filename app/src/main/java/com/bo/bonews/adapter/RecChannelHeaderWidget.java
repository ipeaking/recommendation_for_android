package com.bo.bonews.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bo.bonews.R;
import com.bo.bonews.bean.ProjectChannelBean;
import com.bo.bonews.i.IChannelType;


/**
 * 推荐分类（文字界面）
 */

public class RecChannelHeaderWidget implements IChannelType {
    @Override
    public ChannelAdapter.ChannelViewHolder createViewHolder(LayoutInflater mInflater, ViewGroup parent) {
        return new MyChannelHeaderViewHolder(mInflater.inflate(R.layout.activity_channel_rec_header, parent, false));
    }

    @Override
    public void bindViewHolder(ChannelAdapter.ChannelViewHolder holder, int position, ProjectChannelBean data) {

    }

    public class MyChannelHeaderViewHolder extends ChannelAdapter.ChannelViewHolder {

        public MyChannelHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
