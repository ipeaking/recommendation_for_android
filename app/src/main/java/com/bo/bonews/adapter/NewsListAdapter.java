package com.bo.bonews.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bo.bonews.R;
import com.bo.bonews.bean.NewsBean;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.BaseViewHolder> {

    private final int TYPE_TEXT = 0;

    private Context mContext;

    private List<NewsBean> mNews;

    public NewsListAdapter(Context context, List<NewsBean> newsList) {
        this.mContext = context;
        this.mNews = newsList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == TYPE_TEXT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_news_item, viewGroup, false);
            return new NewsViewHolder(view);
        }//如果有其他类型需要修改
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int i) {
        NewsBean newsBean = mNews.get(i);
        if (getItemViewType(i) == TYPE_TEXT) {
            NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.mTitleView.setText(newsBean.getTitle());
            newsViewHolder.mTimeView.setText(newsBean.getTime());
        }//如果有其他类型需要修改
    }

    @Override
    public int getItemViewType(int position) {
        NewsBean newsBean = mNews.get(position);
        return newsBean.getType();
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class NewsViewHolder extends BaseViewHolder {

        TextView mTitleView;
        TextView mTimeView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.tvTitle);
            mTimeView = itemView.findViewById(R.id.tvTime);
        }
    }
}
