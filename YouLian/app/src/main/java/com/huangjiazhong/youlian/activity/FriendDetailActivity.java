package com.huangjiazhong.youlian.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.dbhelper.ContactOpenHelper;
import com.huangjiazhong.youlian.dbhelper.SmsOpenHelper;
import com.huangjiazhong.youlian.provider.ContactsProvider;
import com.huangjiazhong.youlian.provider.SmsProvider;
import com.huangjiazhong.youlian.utils.FormatUtils;
import com.huangjiazhong.youlian.utils.ThreadUtils;
import com.huangjiazhong.youlian.utils.ToastUtils;
import com.huangjiazhong.youlian.view.CustomShapeImageView;
import com.huangjiazhong.youlian.xmpp.XmppAdmin;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.huangjiazhong.youlian.xmpp.XmppConnection.SERVICENAME;

public class FriendDetailActivity extends AppCompatActivity {


    @InjectView(R.id.friend_avatar)
    CustomShapeImageView mFriendAvatar;

    @InjectView(R.id.tv_nickname)
    TextView mTvNickname;
    @InjectView(R.id.tv_username)
    TextView mTvUsername;
    @InjectView(R.id.btn_delete_friend)
    Button mBtnDeleteFriend;
    @InjectView(R.id.btn_add_friend)
    Button mBtnAddFriend;
    @InjectView(R.id.activity_friend_detail)
    LinearLayout mActivityFriendDetail;
    @InjectView(R.id.change_note)
    TextView mChangeNote;
    private String mParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        ButterKnife.inject(this);
        init();
        initListener();
    }

    private void init() {
        Bundle bundle = FriendDetailActivity.this.getIntent().getExtras();
        String flag = bundle.getString("flag");//用flag来标志是哪个activity跳过来的
        String userName = bundle.getString("userName");
        String nickName = bundle.getString("nickName");
        byte[] imgs = bundle.getByteArray("avatar");
        mParticipant = bundle.getString("participant");
        if(flag.equals("add")){
            mBtnAddFriend.setVisibility(View.VISIBLE);
            mBtnDeleteFriend.setVisibility(View.GONE);
        }else if(flag.equals("delete")){
            mBtnAddFriend.setVisibility(View.GONE);
            mBtnDeleteFriend.setVisibility(View.VISIBLE);
        }
        initData(userName,nickName,imgs);
    }
    /**=============== 事件监听 ===============*/
    private void initListener() {
        mBtnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        addFriend();
                        finish();
                    }
                });
            }
        });
        mBtnDeleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        deleteFriend();
                        finish();
                    }
                });
            }
        });
    }

    /**=============== 添加好友 ===============*/
    private void addFriend() {
        final String userName = mTvUsername.getText().toString();
        final String nickName = mTvNickname.getText().toString();
        Drawable userImage = mFriendAvatar.getDrawable();
        byte[] imgs = null;
        if(userImage!=null){
            imgs = FormatUtils.getInstance().Drawable2Bytes(userImage);
        }
        boolean b = XmppAdmin.addFriend(FriendDetailActivity.this, userName, nickName, null,imgs);
        if(!b){
            ToastUtils.showToastSafe(FriendDetailActivity.this,"添加好友失败");
        }
    }
    /**=============== 删除好友 ===============*/
    private void deleteFriend() {
        String userName = mTvUsername.getText().toString();
        String account = userName + "@" + SERVICENAME;
        XmppAdmin.deleteFriend(account);
        int deleteCode = getContentResolver().delete(SmsProvider.URI_SESSION,
                SmsOpenHelper.SmsTable.SESSION_ACCOUNT + "=?", new String[]{mParticipant});
//                        getContentResolver().delete(SmsProvider.URI_SMS,
//                                SmsOpenHelper.SmsTable.FROM_ACCOUNT + "=?",new String[]{IMService.mCurAccout});
        getContentResolver().delete(ContactsProvider.URI_CONTACT,
                ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});

//        if(deleteCode>0){
//            ToastUtils.showToastSafe(FriendDetailActivity.this,"删除成功");
//        }else {
//            ToastUtils.showToastSafe(FriendDetailActivity.this,"删除失败");
//        }
    }

    /**=============== 初始化数据 ===============*/
    private void initData(final String userName, final String nickName,byte[] imgs) {//
        mTvUsername.setText(userName);
        mTvNickname.setText(nickName);
        if(imgs!=null){
            Drawable friendAvatar = FormatUtils.getInstance().Bytes2Drawable(imgs);
            mFriendAvatar.setImageDrawable(friendAvatar);
        }else {
            mFriendAvatar.setImageResource(R.mipmap.login);
        }
    }
}
