package com.huangjiazhong.youlian.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.service.IMService;

import org.jivesoftware.smack.XMPPException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SetPwdActivity extends AppCompatActivity {

    @InjectView(R.id.et_old_pwd)
    EditText mEtOldPwd;
    @InjectView(R.id.et_new_pwd)
    EditText mEtNewPwd;
    @InjectView(R.id.et_confirm_pwd)
    EditText mEtConfirmPwd;
    @InjectView(R.id.btn_ok)
    Button mBtnOk;
    @InjectView(R.id.activity_set_pwd)
    LinearLayout mActivitySetPwd;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        ButterKnife.inject(this);
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void init() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = mEtOldPwd.getText().toString().trim();
                String newPwd = mEtNewPwd.getText().toString().trim();
                String confirmPwd = mEtConfirmPwd.getText().toString().trim();
                if (TextUtils.isEmpty(oldPwd)) {
                    mEtOldPwd.setError("请输入旧密码!亲");
                    return;
                }
                if (TextUtils.isEmpty(newPwd)) {
                    mEtNewPwd.setError("新密码不能为空哦!亲");
                    return;
                }
                if (TextUtils.isEmpty(confirmPwd)) {
                    mEtConfirmPwd.setError("确认密码不能为空哦!亲");
                    return;
                }
                if (!IMService.mCurPwd.equals(oldPwd)) {
                    mEtOldPwd.setError("请输入正确的旧密码");
                    mEtOldPwd.setText("");
                    return;
                }
                if (!newPwd.equals(confirmPwd)) {
                    Toast.makeText(SetPwdActivity.this, "新密码和确认密码不相同", Toast.LENGTH_SHORT).show();
                    mEtNewPwd.setText("");
                    mEtOldPwd.setText("");
                    mEtConfirmPwd.setText("");
                    return;
                }
                try {
                    IMService.conn.getAccountManager().changePassword(newPwd);
                    setResult(RESULT_OK);
                    SharedPreferences sp = getSharedPreferences("YOULIAN", MODE_PRIVATE);
                    sp.edit().putBoolean("auto_ischeck", false).commit();//设置为手动登录
                    sp.edit().putString("password", newPwd).commit();
                    Toast.makeText(SetPwdActivity.this, "设置密码成功，请用新密码重新登录", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Toast.makeText(SetPwdActivity.this, "设置密码失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SetPwd Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        mClient.disconnect();
    }
}