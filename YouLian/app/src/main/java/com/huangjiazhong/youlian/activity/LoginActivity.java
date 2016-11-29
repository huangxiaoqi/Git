package com.huangjiazhong.youlian.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.service.IMService;
import com.huangjiazhong.youlian.utils.NetUtils;
import com.huangjiazhong.youlian.utils.ThreadUtils;
import com.huangjiazhong.youlian.utils.ToastUtils;
import com.huangjiazhong.youlian.xmpp.XmppAdmin;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.huangjiazhong.youlian.xmpp.XmppConnection.SERVICENAME;


public class LoginActivity extends AppCompatActivity {
    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.et_password)
    EditText mEtPassword;
    @InjectView(R.id.remenber)
    CheckBox mRemenber;
    @InjectView(R.id.auto_login)
    CheckBox mAutoLogin;
    @InjectView(R.id.btn_login)
    Button mBtnLogin;
    @InjectView(R.id.linearLayout)
    LinearLayout mLinearLayout;
    @InjectView(R.id.register)
    TextView mRegister;
    @InjectView(R.id.activity_login)
    LinearLayout mActivityLogin;
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSp = getSharedPreferences("YOULIAN", MODE_PRIVATE);//用来确定登录方式
        ButterKnife.inject(this);
        init();
        initListener();
    }

    private void init() {
        if (mSp.getBoolean("remenber_ischeck", false)) {//记住密码
            mRemenber.setChecked(true);
            final String username = mSp.getString("username", "");
            final String password = mSp.getString("password", "");
            mEtUsername.setText(username);
            mEtPassword.setText(password);
            if (NetUtils.isNetworkAvailable(LoginActivity.this)) {
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mSp.getBoolean("auto_ischeck", false) && !TextUtils.isEmpty(username)
                                && !TextUtils.isEmpty(password)) {//自动登录
                            mAutoLogin.setChecked(true);//将自动登录勾上
                            boolean login = XmppAdmin.login(username, password);
                            if(login){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                                String account = username + "@" + SERVICENAME;// 保存当前登录的账户
                                IMService.mCurAccout = account;
                                IMService.mCurPwd = password;
                                /**==============启动Service============*/
                                Intent IMService = new Intent(LoginActivity.this, IMService.class);
                                startService(IMService);
                            }
                        }
                    }
                });
            }else {
                Toast.makeText(LoginActivity.this, "网络不可用!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void initListener() {
        login();
        setCheckBoxStatus();
        userRegister();
    }

    /**
     * 登录
     */
    private void login() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获得账号和密码
                final String username = mEtUsername.getText().toString();
                final String password = mEtPassword.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    mEtUsername.setError("账号不能为空哦!亲");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mEtUsername.setError("密码不能为空哦!亲");
                    return;
                }
                //在子线程中执行登录操作
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!NetUtils.isNetworkAvailable(LoginActivity.this)) {//如果没有网络
                            ToastUtils.showToastSafe(LoginActivity.this, "网络不可用哦!亲");
                            return;
                        }
                        boolean login = XmppAdmin.login(username, password);
                        if (!login) {
                            ToastUtils.showToastSafe(LoginActivity.this, "登录失败!");
                            return;
                        }
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        //登录成功和记住密码框为选中状态才保存用户信息
                        if (mRemenber.isChecked()) {
                            //记住用户名、密码、
                            mSp.edit().putString("username", username).commit();
                            mSp.edit().putString("password", password).commit();
                        }
                        // 保存当前登录的账户
                        String account = username + "@" + SERVICENAME;
                        IMService.mCurAccout = account;
                        IMService.mCurPwd = password;
                        /**==============启动Service============*/
                        Intent IMService = new Intent(LoginActivity.this, IMService.class);
                        startService(IMService);
                    }
                });
            }
        });
    }

    /**
     * 记住密码是否勾选
     *
     * @return
     */
    public void setCheckBoxStatus() {
        mRemenber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRemenber.isChecked()) {
                    mSp.edit().putBoolean("remenber_ischeck", true).commit();
                } else {
                    mSp.edit().putBoolean("remenber_ischeck", false).commit();
                }
            }
        });
        mAutoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAutoLogin.isChecked()) {
                    mSp.edit().putBoolean("auto_ischeck", true).commit();
                } else {
                    mSp.edit().putBoolean("auto_ischeck", false).commit();
                }
            }
        });
    }

    /**
     * 注册
     */
    public static final int CODE = 7;
    private void userRegister() {
        mRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, CODE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE && resultCode == RESULT_OK && null != data) {
            String userName = data.getStringExtra("userName");
            String password = data.getStringExtra("password");
            mEtUsername.setText(userName);
            mEtPassword.setText(password);
        }
    }
}
