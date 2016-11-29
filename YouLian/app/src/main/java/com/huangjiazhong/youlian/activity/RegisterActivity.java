package com.huangjiazhong.youlian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.utils.ThreadUtils;
import com.huangjiazhong.youlian.utils.ToastUtils;
import com.huangjiazhong.youlian.xmpp.XmppAdmin;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterActivity extends AppCompatActivity {

    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.et_password)
    EditText mEtPassword;
    @InjectView(R.id.et_password_confirm)
    EditText mEtPasswordConfirm;
    @InjectView(R.id.btn_ok)
    Button mBtnOk;
    @InjectView(R.id.btn_cancel)
    Button mBtnCancel;
    @InjectView(R.id.activity_register)
    LinearLayout mActivityRegister;
    @InjectView(R.id.et_nick_name)
    EditText mEtNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        initListener();
    }
    private void initListener() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = mEtUsername.getText().toString().trim();
                final String nickName = mEtNickName.getText().toString().trim();
                final String password = mEtPassword.getText().toString().trim();
                final String pwdCofirm = mEtPasswordConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    mEtUsername.setError("账号不能为空哦!亲");
                    return;
                }
                if(TextUtils.isEmpty(nickName)){
                    mEtNickName.setError("请输入昵称!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mEtPassword.setError("密码不能为空哦!亲");
                    return;
                }
                if (TextUtils.isEmpty(pwdCofirm)) {
                    mEtPassword.setError("确认密码不能为空哦!亲");
                    return;
                }

                if (pwdCofirm.equals(password)) {
                    ThreadUtils.runInThread( new Runnable() {
                        @Override
                        public void run() {
                            XmppAdmin.register(RegisterActivity.this, userName, password, nickName);
                            Intent intent = new Intent();
                            intent.putExtra("userName", userName);
                            intent.putExtra("password", password);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                } else {
                    ToastUtils.showToastSafe(RegisterActivity.this, "两次输入的密码不一致");
                    return;
                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
