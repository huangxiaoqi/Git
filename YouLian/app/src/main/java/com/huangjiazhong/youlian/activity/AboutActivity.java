package com.huangjiazhong.youlian.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huangjiazhong.youlian.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutActivity extends AppCompatActivity {

    @InjectView(R.id.iv_icon)
    ImageView mIvIcon;
    @InjectView(R.id.tv_version)
    TextView mTvVersion;
    @InjectView(R.id.introduction)
    TextView mIntroduction;
    @InjectView(R.id.help_feedback)
    TextView mHelpFeedback;
    @InjectView(R.id.version_update)
    TextView mVersionUpdate;
    @InjectView(R.id.activity_about)
    LinearLayout mActivityAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        PackageInfo info = null;
        PackageManager packageManager = this.getPackageManager();
        try {
            info = packageManager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(info != null){
            String versionName = info.versionName;
            mTvVersion.setText("友联 "+versionName);
        }
    }
}
