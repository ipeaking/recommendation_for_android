package com.bo.bonews.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bo.bonews.R;
import com.bo.bonews.base.cache.SpCache;
import com.bo.bonews.base.http.ViseHttp;
import com.bo.bonews.base.http.callback.ACallback;
import com.bo.bonews.bean.NewsBean;
import com.bo.bonews.i.HttpConfig;
import com.bo.bonews.utils.LoginUtils;
import com.bo.bonews.view.GoodView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.immersionbar.ImmersionBar;
import com.zzhoujay.richtext.RichText;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import butterknife.BindView;

public class DetailActivity extends BaseActivity {

    @BindView(R.id.text)
    TextView ctView;

    GoodView mGoodView;

    @BindView(R.id.btnLike)
    ImageView ivLike;

    @BindView(R.id.btnCollect)
    ImageView ivCollect;

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    NewsBean newsBean;
    private SpCache mLCCache;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initView() {
        RichText.initCacheDir(this);
        Intent intent = getIntent();
        newsBean = intent.getParcelableExtra("news");
        mGoodView = new GoodView(this);
        mToolBar.setTitle(newsBean.getTitle());
        requestData();
        addRead();
        setMarqueeForToolbarTitleView(mToolBar);
        mLCCache = new SpCache(this);
        boolean likesStatus = getLikesStatus(newsBean.getContentId());
        if (likesStatus) {
            ivLike.setImageResource(R.mipmap.good_checked);
        }
        boolean collectStatus = getCollectStatus(newsBean.getContentId());
        if (collectStatus) {
            ivCollect.setImageResource(R.mipmap.collection_checked);
        }
    }

    private boolean getCollectStatus(String id) {
        if (!LoginUtils.getLoginStatus()) {
            return false;
        }
        return mLCCache.get("collect_status_" + id, false);
    }

    private boolean getLikesStatus(String id) {
        if (!LoginUtils.getLoginStatus()) {
            return false;
        }
        return mLCCache.get("likes_status_" + id, false);
    }

    private void setLikesStatus(String id, boolean status) {
        mLCCache.put("likes_status_" + id, status);
    }

    private void setCollectStatus(String id, boolean status) {
        mLCCache.put("collect_status_" + id, status);
    }

    @Override
    protected void setListener() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCollect();
            }
        });
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLike();
            }
        });
    }

    private TextView getToolbarTitleView(Toolbar toolbar) {
        try {
            Field field = toolbar.getClass().getDeclaredField("mTitleTextView");
            field.setAccessible(true);

            Object object = field.get(toolbar);
            if (object != null) {
                TextView mTitleTextView = (TextView) object;
                return mTitleTextView;
            }
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        } catch (Exception e) {
        }
        return null;
    }

    private void setMarqueeForToolbarTitleView(final Toolbar toolbar) {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                TextView mTitleTextView = getToolbarTitleView(toolbar);
                if (mTitleTextView == null) {
                    return;
                }
                mTitleTextView.setHorizontallyScrolling(true);
                mTitleTextView.setMarqueeRepeatLimit(-1);
                mTitleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                mTitleTextView.setSelected(true);
            }
        });

    }

    private void finishActivity() {
        DetailActivity.this.finish();
    }

    private void addCollect() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content_id", newsBean.getContentId());
            jsonObject.put("user_id", LoginUtils.getUserId());
            jsonObject.put("title", newsBean.getTitle());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViseHttp.POST(HttpConfig.COLLECTIONS)
                .baseUrl(HttpConfig.BASE_URL)
                .setJson(jsonObject)
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        JsonObject asJsonObject = new JsonParser().parse(data).getAsJsonObject();
                        JsonElement msg = asJsonObject.get("msg");
                        JsonElement code = asJsonObject.get("code");
                        int asInt = code.getAsInt();
                        if (asInt == 0) {
                            setCollectStatus(newsBean.getContentId(), true);
                            ivCollect.setImageResource(R.mipmap.collection_checked);
                            mGoodView.setTextInfo("收藏成功", Color.parseColor("#f66467"), 12);
                            mGoodView.show(ivCollect);
                        } else {
                            Toast.makeText(DetailActivity.this, msg.getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(DetailActivity.this, errCode + "  " + errMsg + "", Toast.LENGTH_SHORT).show();
                    }
                });
    }

     private void addRead() {
         JSONObject jsonObject = new JSONObject();
         try {
             jsonObject.put("content_id", newsBean.getContentId());
             jsonObject.put("user_id", LoginUtils.getUserId());
             jsonObject.put("title", newsBean.getTitle());
         } catch (JSONException e) {
             e.printStackTrace();
         }
         ViseHttp.POST(HttpConfig.READ)
                 .baseUrl(HttpConfig.BASE_URL)
                 .setJson(jsonObject)
                 .request(new ACallback<String>() {
                     @Override
                     public void onSuccess(String data) {
                         JsonObject asJsonObject = new JsonParser().parse(data).getAsJsonObject();
                         JsonElement msg = asJsonObject.get("msg");
                         JsonElement code = asJsonObject.get("code");
                         int asInt = code.getAsInt();
                         if (asInt == 0) {
                             Toast.makeText(DetailActivity.this, "reading", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(DetailActivity.this, msg.getAsString(), Toast.LENGTH_SHORT).show();
                         }
                     }

                     @Override
                     public void onFail(int errCode, String errMsg) {
                         Toast.makeText(DetailActivity.this, errCode + "  " + errMsg + "", Toast.LENGTH_SHORT).show();
                     }
                 });
     }

    private void addLike() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content_id", newsBean.getContentId());
            jsonObject.put("user_id", LoginUtils.getUserId());
            jsonObject.put("title", newsBean.getTitle());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViseHttp.POST(HttpConfig.LIKES)
                .baseUrl(HttpConfig.BASE_URL)
                .setJson(jsonObject)
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        JsonObject asJsonObject = new JsonParser().parse(data).getAsJsonObject();
                        JsonElement msg = asJsonObject.get("msg");
                        JsonElement code = asJsonObject.get("code");
                        int asInt = code.getAsInt();
                        if (asInt == 0) {
                            setLikesStatus(newsBean.getContentId(), true);
                            mGoodView.setImage(getResources().getDrawable(R.mipmap.good_checked));
                            mGoodView.show(ivLike);
                            ivLike.setImageResource(R.mipmap.good_checked);
                        } else {
                            Toast.makeText(DetailActivity.this, msg.getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(DetailActivity.this, errCode + "  " + errMsg + "", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void requestData() {
        RichText.from(newsBean.getContent())
                .into(ctView);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).titleBar(R.id.toolbar).keyboardEnable(true).init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RichText.recycle();
    }

}
