package com.huangjiazhong.youlian.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.huangjiazhong.youlian.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MessageRemindActivity extends AppCompatActivity {

    @InjectView(R.id.cb_music)
    CheckBox mCbMusic;
    @InjectView(R.id.cb_vibrator)
    CheckBox mCbVibrator;
    @InjectView(R.id.btn_back)
    Button mBtnBack;
    @InjectView(R.id.activity_message_remind)
    LinearLayout mActivityMessageRemind;
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_remind);
        ButterKnife.inject(this);
        mSp = getSharedPreferences("YOULIAN", MODE_PRIVATE);
        init();
    }

    private void init() {
        if(mSp.getBoolean("music",true)){
            mCbMusic.setChecked(true);
        }
        if(mSp.getBoolean("vibrate",false)){
            mCbVibrator.setChecked(true);
        }
        mCbMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCbMusic.isChecked()) {
                    mSp.edit().putBoolean("music", true).commit();
                } else {
                    mSp.edit().putBoolean("music", false).commit();
                }
            }
        });
        mCbVibrator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCbVibrator.isChecked()) {
                    mSp.edit().putBoolean("vibrate", true).commit();
                } else {
                    mSp.edit().putBoolean("vibrate", false).commit();
                }
            }
        });
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
