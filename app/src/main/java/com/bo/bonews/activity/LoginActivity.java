package com.bo.bonews.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bo.bonews.R;
import com.bo.bonews.base.http.ViseHttp;
import com.bo.bonews.base.http.callback.ACallback;
import com.bo.bonews.i.HttpConfig;
import com.bo.bonews.utils.LoginUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 *
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_register)
    TextView tvRegister;

    @BindView(R.id.et_username)
    EditText etUser;

    @BindView(R.id.et_password)
    EditText etPwd;

    @BindView(R.id.btn_login)
    Button btnLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "你点我干啥", Toast.LENGTH_SHORT).show();
                login();
            }
        });
    }

    private void login() {
        Editable user = etUser.getText();
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(this, "请输入用户名!", Toast.LENGTH_SHORT).show();
            return;
        }
        Editable pwd = etPwd.getText();
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "请输入密码!", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.toString());
            jsonObject.put("password", pwd.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViseHttp.POST(HttpConfig.LONGIN)
                .baseUrl(HttpConfig.BASE_URL)
                .setJson(jsonObject)
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        JsonObject asJsonObject = new JsonParser().parse(data).getAsJsonObject();
                        JsonElement msg = asJsonObject.get("msg");
                        String asString = msg.getAsString();
                        JsonElement code = asJsonObject.get("code");
                        int asInt = code.getAsInt();
                        if (asInt == 0) {
                            JsonElement userIdE = asJsonObject.get("data");
                            JsonObject uid = userIdE.getAsJsonObject();
                            JsonElement userid = uid.get("userid");
                            LoginUtils.setUserId(userid.getAsInt());
                            LoginUtils.setLoginStatus(true);
                            // 登陆成功
                            closeActivity();
                        }
                        Toast.makeText(LoginActivity.this, asString + "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(LoginActivity.this, errCode + "  " + errMsg + "", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void closeActivity() {
        this.finish();
    }

    private void startRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).titleBar(R.id.toolbar).keyboardEnable(true).init();
    }

}
