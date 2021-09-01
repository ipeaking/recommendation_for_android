package com.bo.bonews.activity;

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
import com.bo.bonews.bean.RegisterBean;
import com.bo.bonews.i.HttpConfig;
import com.bo.bonews.view.SwitchMultiButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.immersionbar.ImmersionBar;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;

import butterknife.BindView;

/**
 *
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.tv_select_address)
    TextView mAddressView;

    @BindView(R.id.switch_sexy)
    SwitchMultiButton mSwitchButton;

    @BindView(R.id.et_username)
    EditText mEtUserName;

    @BindView(R.id.et_nickname)
    EditText mEtNickName;

    @BindView(R.id.et_password)
    EditText mEtPwd;

    @BindView(R.id.et_password_re)
    EditText mEtPwdre;

    @BindView(R.id.et_age)
    EditText mEtAge;

    @BindView(R.id.btn_register)
    Button mBtnRegister;

    JDCityPicker cityPicker;

    String gender = "male"; //female


    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).titleBar(R.id.toolbar).keyboardEnable(true).init();
    }

    @Override
    protected void initView() {
        RegisterBean registerBean = new RegisterBean();
        mAddressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityPicker.showCityPicker();
            }
        });

        JDCityConfig.ShowType mWheelType = JDCityConfig.ShowType.PRO_CITY;
        JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();
        jdCityConfig.setShowType(mWheelType);
        cityPicker = new JDCityPicker();
        //初始化数据
        cityPicker.init(this);
        //设置JD选择器样式位只显示省份和城市两级
        cityPicker.setConfig(jdCityConfig);
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                String location = province.getName() + "/" + city.getName();
                mAddressView.setText(province.getName() + "-" + city.getName());
                registerBean.setLocation(location);
            }

            @Override
            public void onCancel() {
            }
        });
        registerBean.setGender(gender);
        mSwitchButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                if (position == 0) {
                    gender = "male";
                } else {
                    gender = "female";
                }
                registerBean.setGender(gender);
            }
        });


        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister(registerBean);
            }
        });
    }

    private void doRegister(RegisterBean registerBean) {
        Editable userName = mEtUserName.getText();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "请输入用户名!", Toast.LENGTH_SHORT).show();
            return;
        }
        registerBean.setUserName(userName.toString());
        Editable nickName = mEtNickName.getText();
        if (TextUtils.isEmpty(nickName)) {
            Toast.makeText(this, "请输入昵称!", Toast.LENGTH_SHORT).show();
            return;
        }
        registerBean.setNickName(nickName.toString());
        Editable pwd = mEtPwd.getText();
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "请输入密码!", Toast.LENGTH_SHORT).show();
            return;
        }
        Editable pwd_re = mEtPwdre.getText();
        if (TextUtils.isEmpty(pwd_re)) {
            Toast.makeText(this, "请再次输入密码!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pwd.toString().equals(pwd_re.toString())) {
            Toast.makeText(this, "两次输入密码不一致!", Toast.LENGTH_SHORT).show();
            return;
        }
        registerBean.setPwd(pwd.toString());
        Editable age = mEtAge.getText();
        if (TextUtils.isEmpty(age)) {
            Toast.makeText(this, "请输入年龄!", Toast.LENGTH_SHORT).show();
            return;
        }
        registerBean.setAge(Integer.valueOf(age.toString()));
        String registJson = new Gson().toJson(registerBean);
        ViseHttp.POST(HttpConfig.REGISTER)
                .baseUrl(HttpConfig.BASE_URL)
                .setJson(registJson)
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        JsonObject asJsonObject = new JsonParser().parse(data).getAsJsonObject();
                        JsonElement msg = asJsonObject.get("msg");
                        String asString = msg.getAsString();
                        JsonElement code = asJsonObject.get("code");
                        int asInt = code.getAsInt();
                        if (asInt == 0) {
                            closeActivity();
                        }
                        Toast.makeText(RegisterActivity.this, asString + "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(RegisterActivity.this, errCode + "  " + errMsg + "", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void closeActivity() {
        this.finish();
    }


}
