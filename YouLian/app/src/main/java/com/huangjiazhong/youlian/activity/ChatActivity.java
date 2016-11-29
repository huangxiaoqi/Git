package com.huangjiazhong.youlian.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.adapter.ChatAdapter;
import com.huangjiazhong.youlian.adapter.FaceAdapter;
import com.huangjiazhong.youlian.dbhelper.SmsOpenHelper;
import com.huangjiazhong.youlian.fragment.SessionFragment;
import com.huangjiazhong.youlian.provider.SmsProvider;
import com.huangjiazhong.youlian.service.IMService;
import com.huangjiazhong.youlian.utils.FaceScaleUtils;
import com.huangjiazhong.youlian.utils.ThreadUtils;
import com.huangjiazhong.youlian.xmpp.XXApp;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    public static String CLICKACCOUNT = "clickAccount";
    public static String CLICKNICKNAME = "clickNickName";
    public static String CLICKAVATAR = "clickAvatar";
    public static String participant = "";
    @InjectView(R.id.delete_friend)
    ImageView mDeleteFriend;
    @InjectView(R.id.iv_emoji)
    ImageView mIvEmoji;
    @InjectView(R.id.gv_face)
    GridView mGvFace;
    private CursorAdapter mAdapter;
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.listview)
    ListView mListview;
    @InjectView(R.id.et_message)
    EditText mEtMessage;
    @InjectView(R.id.btn_send)
    Button mBtnSend;
    @InjectView(R.id.activity_chat)
    LinearLayout mActivityChat;

    private String mClickAccount;
    private String mClickNickName;

    private IMService mImService;
    private byte[] mImgs;//好友头像信息
    private ArrayList<String> mFaceMapKeys;//表情map的key值

    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SessionFragment.mChatActivityIsTop = true;//标志当前activity处于栈顶
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        init();
        initFaceGridView();
        initData();
        initListener();
    }


    private void init() {
        participant = IMService.mParticipant;
        getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, mMyContentObserver);//注册ContentObserver
        Intent service = new Intent(ChatActivity.this, IMService.class);
        //若服务已经通过startService启动，则自动绑定服务，若服务未创启动，则以绑定的方式启动，进而绑定服务
        bindService(service, mMyServiceConnection, BIND_AUTO_CREATE);

        mClickAccount = getIntent().getStringExtra(ChatActivity.CLICKACCOUNT);
        mClickNickName = getIntent().getStringExtra(ChatActivity.CLICKNICKNAME);
        mImgs = getIntent().getByteArrayExtra(ChatActivity.CLICKAVATAR);

        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);//获得键盘管理器
    }

    /**
     * =============== 初始化表情GridView ===============
     */
    private void initFaceGridView() {
        // 将表情map的key保存在数组中
        Set<String> keySet = XXApp.getInstance().getFaceMap().keySet();
        System.out.println(keySet.size());
        mFaceMapKeys = new ArrayList<>();
        mFaceMapKeys.addAll(keySet);

        mGvFace.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
        mGvFace.setAdapter(new FaceAdapter(this));
        mGvFace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mFaceMapKeys.size() ) {// 删除键的位置
                    int selection = mEtMessage.getSelectionStart();
                    String text = mEtMessage.getText().toString();
                    if (selection > 0) {
                        String text2 = text.substring(selection - 1);
                        if ("]".equals(text2)) {
                            int start = text.lastIndexOf("[");
                            int end = selection;
                            mEtMessage.getText().delete(start,end);
                            return;
                        }
                        mEtMessage.getText().delete(selection - 1, selection);
                    }
                } else {
                    Bitmap bitmap = BitmapFactory.decodeResource(ChatActivity.this.getResources(),
                            (Integer) XXApp.getInstance().getFaceMap().values().toArray()[position]);
                    if (bitmap != null) {//如果位图不为空 在EditText中显示表情
                        //缩放图片  解决内存溢出
                        Bitmap newBitmap = FaceScaleUtils.bitmapScale(bitmap, 50, 50);
                        ImageSpan imageSpan = new ImageSpan(ChatActivity.this, newBitmap);
                        String emojiStr = mFaceMapKeys.get(position);
                        SpannableString spannableString = new SpannableString(emojiStr);
                        spannableString.setSpan(imageSpan,
                                emojiStr.indexOf('['),
                                emojiStr.indexOf(']') + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mEtMessage.append(spannableString);
                    } else {//在EditText中显示字符串
                        String ori = mEtMessage.getText().toString();
                        int index = mEtMessage.getSelectionStart();
                        StringBuilder stringBuilder = new StringBuilder(ori);
                        stringBuilder.insert(index, mFaceMapKeys.get(position));
                        mEtMessage.setText(stringBuilder.toString());
                        mEtMessage.setSelection(index + mFaceMapKeys.get(position).length());
                    }
                }
            }
        });

    }
    private void initData() {
        // 设置title
        mTitle.setText(mClickNickName);
        setAdapterOrNotify();
    }

    /**
     * 设置或者刷新数据
     */
    private void setAdapterOrNotify() {
        // 1.首先判断是否存在adapter
        if (mAdapter != null) {
            // 刷新
            Cursor cursor = mAdapter.getCursor();
            cursor.requery();

            mListview.setSelection(cursor.getCount() - 1);
            return;
        }
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                //final Cursor c = getContentResolver().query(SmsProvider.URI_SMS, null, null, null, null);
                final Cursor c = getContentResolver().query(SmsProvider.URI_SMS, null,//
                        "(from_account = ? and to_account = ?) or ( from_account = ? and to_account = ? )",// where条件
                        new String[]{IMService.mCurAccout, mClickAccount, mClickAccount, IMService.mCurAccout},// where条件的参数
                        SmsOpenHelper.SmsTable.TIME + " ASC"// 根据时间升序排序(ASC前不要忘记有一个空格)
                );
                // 如果没有数据直接返回
                if (c.getCount() < 1) {
                    return;
                }
                ThreadUtils.runInUIThread(new Runnable() {

                    @Override
                    public void run() {
                        //当view为空时执行此方法
                        mAdapter = new ChatAdapter(ChatActivity.this, c);
                        mListview.setAdapter(mAdapter);//显示数据
                        // 滚动到最后一行
                        mListview.setSelection(mAdapter.getCount() - 1);
                    }
                });
            }
        });
    }

    private void initListener() {
        /**=============== 删除好友 ===============*/
        mDeleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mClickAccount.substring(0, mClickAccount.indexOf("@"));
                Intent intent = new Intent(ChatActivity.this, FriendDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag", "delete");
                bundle.putString("userName", userName);
                bundle.putString("nickName", mClickNickName);
                bundle.putString("participant", participant);
                bundle.putByteArray("avatar", mImgs);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        mListview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mGvFace.getVisibility() == View.VISIBLE) {
                    mGvFace.setVisibility(View.GONE);
                }
                return false;
            }
        });
        mIvEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGvFace.getVisibility() == View.VISIBLE) {
                    mGvFace.setVisibility(View.GONE);
                } else {
                    mInputMethodManager.hideSoftInputFromWindow(mEtMessage.getWindowToken(), 0);
                    mGvFace.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick(R.id.btn_send)
    public void send(View view) {
        final String body = mEtMessage.getText().toString();
        // 3.初始化了一个消息
        //必须是asmack包中的Message
        Message msg = new Message();
        msg.setFrom(IMService.mCurAccout);// 当前登录的用户
        msg.setTo(mClickAccount);
        msg.setType(Message.Type.chat);// 类型就是chat
        msg.setBody(body);// 输入框里面的内容
        // TODO 调用服务里面的方法sendMessage发送消息
        mImService.sendMessage(msg);
        // 清空输入框
        mEtMessage.setText("");

//        ThreadUtils.runInThread(new Runnable() {
//
//            @Override
//            public void run() {
//                // 3.初始化了一个消息
//                //必须是asmack包中的Message
//                Message msg = new Message();
//                msg.setFrom(IMService.mCurAccout);// 当前登录的用户
//                msg.setTo(mClickAccount);
//                msg.setType(Message.Type.chat);// 类型就是chat
//                msg.setBody(body);// 输入框里面的内容
//                // TODO 调用服务里面的方法sendMessage发送消息
//                mImService.sendMessage(msg);
//                ThreadUtils.runInUIThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // 清空输入框
//                        mEtMessage.setText("");
//                    }
//                });
//            }
//        });
    }


    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(mMyContentObserver);//解除ContentObserver注册
        SessionFragment.mChatActivityIsTop = false;
        // 解绑服务
        if (mMyServiceConnection != null) {
            unbindService(mMyServiceConnection);
        }
        super.onDestroy();
    }

    /**
     * =============== 使用contentObserver时刻监听记录的改变 ===============
     */
    MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

    class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * =============== 接收到数据记录的改变 ===============
         */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // 设置adapter或者notifyadapter
            setAdapterOrNotify();
            super.onChange(selfChange, uri);
        }
    }

    /**
     * =============== 定义ServiceConnection调用服务里面的方法 ===============
     */
    MyServiceConnection mMyServiceConnection = new MyServiceConnection();//服务连接对象

    class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMService.MyBinder binder = (IMService.MyBinder) service;
            mImService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
