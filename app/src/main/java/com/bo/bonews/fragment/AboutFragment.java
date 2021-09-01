package com.bo.bonews.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bo.bonews.R;
import com.bo.bonews.activity.CollectsActivity;
import com.bo.bonews.activity.LikesActivity;
import com.bo.bonews.activity.LoginActivity;
import com.bo.bonews.utils.LoginUtils;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;

public class AboutFragment extends BaseFragment {

    @BindView(R.id.user_icon)
    ImageView ivUser;

    @BindView(R.id.tv_status)
    TextView tvState;

    @BindView(R.id.tv_re)
    TextView tvRe;

    @BindView(R.id.llLike)
    LinearLayout llLike;

    @BindView(R.id.llCollect)
    LinearLayout llCollect;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).titleBar(R.id.toolbar)
                .navigationBarColor(R.color.btn3)
                .init();
    }

    @Override
    protected void setListener() {
        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginUtils.getLoginStatus()) {
                    startLogin();
                }
            }
        });
        tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginUtils.getLoginStatus()) {
                    startLogin();
                }
            }
        });

        tvRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtils.setLoginStatus(false);
                LoginUtils.setUserId(-1);
                refreshStatus();
            }
        });
        llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLikeActivity();
            }
        });

        llCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCollectActivity();
            }
        });
    }

    private void goLikeActivity() {
        Intent intent = new Intent(getActivity(), LikesActivity.class);
        startActivity(intent);
    }

    private void goCollectActivity() {
        Intent intent = new Intent(getActivity(), CollectsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        refreshStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        if (LoginUtils.getLoginStatus()) {
            tvRe.setVisibility(View.VISIBLE);
            long userId = LoginUtils.getUserId();
            if (userId != -1) {
                tvState.setText("当前登陆用户id：" + userId);
            }
        } else {
            tvRe.setVisibility(View.GONE);
            tvState.setText("未登陆");
        }
    }

    @Override
    protected void initData() {
        refreshStatus();
    }

    private void startLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        getContext().startActivity(intent);
    }

}
