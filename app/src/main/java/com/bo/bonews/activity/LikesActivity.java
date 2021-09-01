
package com.bo.bonews.activity;


import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.andview.refreshview.XRefreshView;
import com.bo.bonews.R;
import com.bo.bonews.adapter.NewsListAdapter;
import com.bo.bonews.base.http.ViseHttp;
import com.bo.bonews.base.http.callback.ACallback;
import com.bo.bonews.bean.NewsBean;
import com.bo.bonews.i.HttpConfig;
import com.bo.bonews.utils.LoginUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

public class LikesActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.iRecyclerView)
    RecyclerView mIRecyclerView;


    @BindView(R.id.xRefreshView)
    XRefreshView mXRefreshView;

    private NewsListAdapter mNewsAdapter;

    private List<NewsBean> mNews = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_likes;
    }

    @Override
    protected void initView() {
        mToolBar.setTitle("点赞列表");
        mIRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mIRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //允许加载更多
        mXRefreshView.setPullLoadEnable(true);
        //允许下拉刷新
        mXRefreshView.setPullRefreshEnable(true);
        //滑动到底部自动加载更多
        mXRefreshView.setAutoLoadMore(false);
        mNewsAdapter = new NewsListAdapter(this, mNews);
        mIRecyclerView.setAdapter(mNewsAdapter);
        requestData();
    }

    private List<String> getSubUtil(String soap, String rgex) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;
    }

    private void requestData() {
        if (!LoginUtils.getLoginStatus()) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", LoginUtils.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViseHttp.POST(HttpConfig.GET_LIKES)
                .baseUrl(HttpConfig.BASE_URL)
                .setJson(jsonObject)
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ArrayList<NewsBean> newsBeans = new ArrayList<>();
                        JsonObject asJsonObject = new JsonParser().parse(data).getAsJsonObject();
                        JsonElement code = asJsonObject.get("code");
                        int asInt = code.getAsInt();
                        if (asInt == 0) {
                            // 返回的不是json数组是字符串再特殊处理下
                            JsonElement infoJE = asJsonObject.get("data");
                            String jsonS = infoJE.getAsString();
                            String jsonStr = jsonS.substring(1, jsonS.length() - 1);
                            String reg = "\\{(.*?)\\}";
                            List<String> infos = getSubUtil(jsonStr, reg);
                            for (int i = 0; i < infos.size(); i++) {
                                String infoS = infos.get(i);
//                                     返回的不是json格式特殊处理下
                                String rTitle = "', 'title': '";
                                String rContent = "'content_id': '";
                                String rTime = "', 'date': datetime.datetime\\(";
                                String rReplace = "&&&&&&&&!!!!@@@@###";
                                String temp = infoS.replaceAll(rTitle, rReplace)
                                        .replaceAll(rContent, rReplace)
                                        .replaceAll(rTime, rReplace);
                                String info = temp.substring(0, temp.length() - 2);
                                String[] split = info.split(rReplace);
                                NewsBean newsBean = new NewsBean();
                                newsBean.setContentId(split[1]);
                                newsBean.setTitle(split[2]);
                                String tS = split[3];
                                String[] tstr = tS.split(", ");
                                String time = tstr[0] + "-" + tstr[1] + "-" + tstr[2] + " " + tstr[3] + ":" + tstr[4];
                                newsBean.setTime(time);
                                newsBeans.add(newsBean);
                            }
                            mNews.clear();
                            mNews.addAll(newsBeans);
                            mNewsAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {

                    }
                });
    }


    @Override
    protected void setListener() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        mXRefreshView.setXRefreshViewListener(new XRefreshView.XRefreshViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onRefresh(boolean isPullDown) {
                requestData();
                mXRefreshView.stopRefresh();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                mXRefreshView.stopLoadMore();
            }

            @Override
            public void onRelease(float direction) {

            }

            @Override
            public void onHeaderMove(double headerMovePercent, int offsetY) {

            }
        });
    }

    private void finishActivity() {
        LikesActivity.this.finish();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).titleBar(R.id.toolbar).keyboardEnable(true).init();
    }
}
