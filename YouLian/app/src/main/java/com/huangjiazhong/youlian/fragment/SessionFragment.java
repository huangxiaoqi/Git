package com.huangjiazhong.youlian.fragment;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.activity.ChatActivity;
import com.huangjiazhong.youlian.adapter.SessionAdapter;
import com.huangjiazhong.youlian.dbhelper.SmsOpenHelper;
import com.huangjiazhong.youlian.provider.SmsProvider;
import com.huangjiazhong.youlian.service.IMService;
import com.huangjiazhong.youlian.utils.NickNameUtils;
import com.huangjiazhong.youlian.utils.ThreadUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SessionFragment extends Fragment {

    @InjectView(R.id.listView)
    ListView mListView;
    private CursorAdapter mAdapter;
    public static int NOTIFYCODE = 0;//未读消息数
    private String mClassName;
    public static boolean mChatActivityIsTop = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }
    private void init() {
        mClassName = getActivity().getPackageName() + ".activity.ChatActivity";
        mChatActivityIsTop = isForeground(getActivity(), mClassName);
        registerContentObserver();
    }

    /**
     * 初始化会话数据
     */
    private void initData() {
        setOrNotifyAdapter();
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.getCursor().requery();//刷新适配器
                NOTIFYCODE = 0;//将未读消息数量重置为0

                Cursor c = mAdapter.getCursor();
                c.moveToPosition(position);
                // 拿到jid(账号)-->发送消息的时候需要
                String account = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
                // 拿到nickName-->显示效果
                String nickname = NickNameUtils.getNickNameByAccount(getActivity(),account);
                byte[] imgs = c.getBlob(c.getColumnIndex(SmsOpenHelper.SmsTable.AVATAR));
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ChatActivity.CLICKACCOUNT, account);
                intent.putExtra(ChatActivity.CLICKNICKNAME, nickname);
                intent.putExtra(ChatActivity.CLICKAVATAR,imgs);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置或者刷新Adapter
     */
    private void setOrNotifyAdapter() {
        // 判断adapter是否存在
        if (mAdapter != null) {
            // 刷新adapter就行了
            mAdapter.getCursor().requery();
            return;
        }
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                // 对应查询记录
                final Cursor c =
                        getActivity().getContentResolver().query(SmsProvider.URI_SESSION, null, null,
                                new String[]{IMService.mCurAccout, IMService.mCurAccout}, null);

                // 假如没有数据的时候
                if (c.getCount() <= 0) {
                    return;
                }
                // 设置adapter,然后显示数据
                ThreadUtils.runInUIThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapter = new SessionAdapter(getActivity(),c);//创建适配器
                        mListView.setAdapter(mAdapter);//设置适配器
                    }
                });
            }
        });
    }
    @Override
    public void onDestroy() {
        unRegisterContentObserver();
        super.onDestroy();
    }

	/*=============== 监听数据库记录的改变 ===============*/

    MyContentObserver	mMyContentObserver	= new MyContentObserver(new Handler());

    /**
     * 注册监听
     */
    public void registerContentObserver() {
        // content://xxxx/contact
        // content://xxxx/contact/i
        getActivity().getContentResolver().registerContentObserver(SmsProvider.URI_SESSION, true, mMyContentObserver);
        getActivity().getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, mMyContentObserver);
    }

    public void unRegisterContentObserver() {
        getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
    }

    /**
     * 反注册监听
     */

    class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * 如果数据库数据改变会在这个方法收到通知
         */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if(!mChatActivityIsTop){
                NOTIFYCODE = NOTIFYCODE + 1;
            }

            // 更新adapter或者刷新adapter
            setOrNotifyAdapter();
        }
    }

    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
