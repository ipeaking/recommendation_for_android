package com.bo.bonews.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.bo.bonews.R;
import com.bo.bonews.activity.DetailActivity;
import com.bo.bonews.adapter.NewsListAdapter;
import com.bo.bonews.adapter.OnRecyclerItemClickListener;
import com.bo.bonews.base.http.ViseHttp;
import com.bo.bonews.base.http.callback.ACallback;
import com.bo.bonews.bean.NewsBean;
import com.bo.bonews.i.APPConst;
import com.bo.bonews.i.HttpConfig;
import com.bo.bonews.utils.LoginUtils;
import com.bo.bonews.view.LoadingPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;

public class NewsListFragment extends BaseFragment {

    private static final String KEY = "TID";

    private String mTid = "";

    @BindView(R.id.iRecyclerView)
    RecyclerView mIRecyclerView;


    @BindView(R.id.xRefreshView)
    XRefreshView mXRefreshView;

    private NewsListAdapter mNewsAdapter;

    private List<NewsBean> mNews = new ArrayList<>();

    @BindView(R.id.loading_page)
    LoadingPage mLoadingPage;

    /**
     * 当前页数
     */
    private int mCurrentPage = 1;

    /**
     * 当前栏目
     */
    private int mColTypes = 0;


    /**
     * 是否正在刷新数据
     */
    private AtomicBoolean isRequesting = new AtomicBoolean(false);


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_list;
    }

    /**
     * 从外部往Fragment中传参数的方法
     *
     * @param tid 频道id
     * @return
     */
    public static NewsListFragment newInstance(String tid) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY, tid);
        NewsListFragment fragment = new NewsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            //取出保存的频道TID
            mTid = getArguments().getString("TID");
        }
        if (APPConst.TID_GUONEI.equals(mTid)) {
            mColTypes = 0;
        } else if (APPConst.TID_TUIJIAN.equals(mTid)) {
            mColTypes = 1;
        } else if (APPConst.TID_DIANYIN.equals(mTid)) {
            mColTypes = 2;
        } else if (APPConst.TID_ZONGYI.equals(mTid)) {
            mColTypes = 3;
        } else {
            mColTypes = -1;
        }
    }

    @Override
    protected void initView() {
        mIRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mIRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        //允许加载更多
        mXRefreshView.setPullLoadEnable(true);
        //允许下拉刷新
        mXRefreshView.setPullRefreshEnable(true);
        //滑动到底部自动加载更多
        mXRefreshView.setAutoLoadMore(false);
        mNewsAdapter = new NewsListAdapter(getActivity(), mNews);
        mIRecyclerView.setAdapter(mNewsAdapter);
        showLoadingPage();
        requestData(true);
    }

    private void requestData(boolean refresh) {
        // 如果正在请求数据则直接返回
        if (isRequesting.get()) {
            return;
        }
        if (refresh) {
            mCurrentPage = 1;
            request(mCurrentPage, new IOnDataLoad() {
                @Override
                public void onSuccess(List<NewsBean> news) {
                    mNews.clear();
                    mNews.addAll(news);
                    mNewsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError() {

                }

                @Override
                public void onAll() {
                    if (mXRefreshView != null) {
                        mXRefreshView.stopRefresh();
                    }
                }
            });
        } else {
            mCurrentPage++;
            request(mCurrentPage, new IOnDataLoad() {
                @Override
                public void onSuccess(List<NewsBean> news) {
                    mNews.addAll(news);
                    mNewsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError() {
                }

                @Override
                public void onAll() {
                    mXRefreshView.stopLoadMore();
                }
            });

        }
        showNewsPage();
    }


    private void request(int pageNum, IOnDataLoad iData) {
        int pageSize = 20;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("page_num", pageNum);
            jsonObject.put("page_size", pageSize);
            jsonObject.put("types", mColTypes);
            jsonObject.put("user_id", LoginUtils.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isRequesting.set(true);
        ViseHttp.POST(HttpConfig.GET_REC_LIST)
                .baseUrl(HttpConfig.BASE_URL)
                .setJson(jsonObject)
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ArrayList<NewsBean> newsBeans = new ArrayList<>();
                        JsonObject asJsonObject = new JsonParser().parse(data).getAsJsonObject();
                        JsonElement msg = asJsonObject.get("msg");
                        String msgTip = msg.getAsString();
                        JsonElement code = asJsonObject.get("code");
                        int asInt = code.getAsInt();
                        if (asInt == 0) {
                            JsonArray newsList = asJsonObject.getAsJsonArray("data");
                            if (newsList != null) {
                                for (int i = 0; i < newsList.size(); i++) {
                                    JsonElement jsonElement = newsList.get(i);
                                    String infoS = jsonElement.getAsString();
                                    // 返回的不是json用JackJson可以解析
                                    ObjectMapper mapper = new ObjectMapper();
                                    mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                                    try {
                                        JsonNode dataNode = mapper.readValue(infoS, JsonNode.class);
                                        String content = dataNode.get("describe").asText();
                                        String time = dataNode.get("news_date").asText();
                                        String type = dataNode.get("type").asText();
                                        String title = dataNode.get("title").asText();
                                        String contentId = dataNode.get("content_id").asText();
//                                    Log.e("tagsss", "===============");
//                                    Log.e("tagsss",contentId);
//                                    Log.e("tagsss",content);
//                                    Log.e("tagsss",type);
//                                    Log.e("tagsss",time);
//                                    Log.e("tagsss",title);
//                                    Log.e("tagsss", "===============");
                                        NewsBean newsBean = new NewsBean();
                                        newsBean.setContent(content);
                                        newsBean.setStype(type);
                                        newsBean.setTime(time);
                                        newsBean.setTitle(title);
                                        newsBean.setContentId(contentId);
                                        newsBeans.add(newsBean);
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                        if (iData != null) {
                            iData.onSuccess(newsBeans);
                            iData.onAll();
                        }
                        isRequesting.set(false);
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(getActivity(), errCode + "  " + errMsg + "", Toast.LENGTH_SHORT).show();
                        if (iData != null) {
                            iData.onError();
                            iData.onAll();
                        }
                        isRequesting.set(false);
                    }
                });
    }

    @Override
    protected void setListener() {
        mXRefreshView.setXRefreshViewListener(new XRefreshView.XRefreshViewListener() {
            @Override
            public void onRefresh() {
                requestData(true);
            }

            @Override
            public void onRefresh(boolean isPullDown) {

            }

            @Override
            public void onLoadMore(boolean isSilence) {
                requestData(false);
            }

            @Override
            public void onRelease(float direction) {

            }

            @Override
            public void onHeaderMove(double headerMovePercent, int offsetY) {

            }
        });

        mIRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mIRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                goDetailActivity(position);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });
    }

    private void goDetailActivity(int position) {
        NewsBean newsBean = mNews.get(position);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("news", newsBean);
        startActivity(intent);
    }

    /**
     * 如果有新闻就展示新闻页面
     */
    private void showNewsPage() {
        mIRecyclerView.setVisibility(View.VISIBLE);
        mLoadingPage.setSuccessView();

    }

    /**
     * 展示加载页面
     */
    private void showLoadingPage() {
        mIRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingPage.setLoadingView();
    }

    /**
     * 如果没有网络就展示空消息页面
     */
    private void showEmptyPage() {
        mIRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingPage.setEmptyView();
    }

    private void showErroPage() {
        mIRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingPage.setErrorView();
        mLoadingPage.setLoadingClickListener(new LoadingPage.LoadingClickListener() {
            @Override
            public void clickListener() {
                requestData(true);
            }
        });
    }

    interface IOnDataLoad {
        void onSuccess(List<NewsBean> news);

        void onError();

        void onAll();
    }
}
