package com.huangjiazhong.youlian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.provider.ContactsProvider;
import com.huangjiazhong.youlian.utils.ThreadUtils;
import com.huangjiazhong.youlian.xmpp.XmppConnection;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //延时三秒进入登录界面
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                XmppConnection.openConnection();//与openfire服务器建立连接
                SystemClock.sleep(2000);
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
                //deleteContactCache();//清空联系人表
            }
        });
    }
    /**=============== 删除历史登录者存入本地数据库中的好友 ===============*/
    private void deleteContactCache() {
        getContentResolver().delete(ContactsProvider.URI_CONTACT,null,null);
    }
}
