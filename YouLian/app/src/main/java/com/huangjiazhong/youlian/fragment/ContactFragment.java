package com.huangjiazhong.youlian.fragment;


import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.activity.ChatActivity;
import com.huangjiazhong.youlian.adapter.MyFriendAdapter;
import com.huangjiazhong.youlian.dbhelper.ContactOpenHelper;
import com.huangjiazhong.youlian.provider.ContactsProvider;
import com.huangjiazhong.youlian.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 联系人fragment
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    @InjectView(android.R.id.tabs)
    TabWidget mTabs;
    @InjectView(android.R.id.tabcontent)
    FrameLayout mTabcontent;
    @InjectView(android.R.id.tabhost)
    TabHost mTabhost;
    private CursorAdapter mAdapter;
    public static boolean isOnLine = false;
    private ListView mListViewFriend;
    private ListView mListViewPhone;
    private SimpleAdapter mContactAdapter;
    private ArrayList<HashMap<String, String>> mReadContact;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化fragment
     */
    private void init() {
        //注册内容观察者
        getActivity().getContentResolver()
                .registerContentObserver(ContactsProvider.URI_CONTACT, true, mMyContentObserver);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        initTabhost();//初始化tabhost
        initPhone();//获得系统电话联系人
        setOrUpdateAdapter();//设置或更新好友
    }

    /**
     * =============== 初始化tabhost ===============
     */
    private void initTabhost() {
        mTabhost.setup();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        inflater.inflate(R.layout.myfriend, mTabhost.getTabContentView());
        inflater.inflate(R.layout.phonebook, mTabhost.getTabContentView());

        mTabhost.addTab(mTabhost.newTabSpec("myfriend")
                .setIndicator("我的好友")
                .setContent(R.id.ll_myfrind));
        mTabhost.addTab(mTabhost.newTabSpec("phonebook")
                .setIndicator("电话本")
                .setContent(R.id.ll_phonebook));

        FrameLayout tabContentView = mTabhost.getTabContentView();

        LinearLayout myFriend = (LinearLayout) tabContentView.getChildAt(0);
        mListViewFriend = (ListView) myFriend.getChildAt(0);

        LinearLayout phoneBook = (LinearLayout) tabContentView.getChildAt(1);
        mListViewPhone = (ListView) phoneBook.getChildAt(0);
    }

    /**
     * =============== 获得系统电话联系人 ===============
     */
    private void initPhone() {
        mReadContact = readContact();
        mContactAdapter = new SimpleAdapter(getActivity(), mReadContact,
                R.layout.phone_item, new String[]{"name", "phone"},
                new int[]{R.id.tv_name, R.id.tv_phone});
        mListViewPhone.setAdapter(mContactAdapter);
    }

    private ArrayList<HashMap<String, String>> readContact() {
        // 首先,从raw_contacts中读取联系人的id("contact_id")
        // 其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
        // 然后,根据mimetype来区分哪个是联系人,哪个是电话号码
        Uri rawContactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        // 从raw_contacts中读取联系人的id("contact_id")
        Cursor rawContactsCursor = getActivity().getContentResolver().query(rawContactsUri,
                new String[]{"contact_id"}, null, null, null);
        if (rawContactsCursor != null) {
            while (rawContactsCursor.moveToNext()) {
                String contactId = rawContactsCursor.getString(0);
                // System.out.println(contactId);

                // 根据contact_id从data表中查询出相应的电话号码和联系人名称, 实际上查询的是视图view_data
                Cursor dataCursor = getActivity().getContentResolver().query(dataUri,
                        new String[]{"data1", "mimetype",}, "contact_id=?",
                        new String[]{contactId}, null);

                if (dataCursor != null) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    while (dataCursor.moveToNext()) {
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        // System.out.println(contactId + ";" + data1 + ";"
                        // + mimetype);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            map.put("name", data1);
                        }
                    }
                    list.add(map);
                    dataCursor.close();
                }
            }
            rawContactsCursor.close();
        }

        return list;
    }

    /**
     * 初始化事件
     */
    private void initListener() {
        mListViewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = mAdapter.getCursor();
                c.moveToPosition(position);
                //拿到jid（账号）--->>>发送消息时需要
                String account = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                //拿到昵称
                String nickname = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
                byte[] imgs = c.getBlob(c.getColumnIndex(ContactOpenHelper.ContactTable.AVATAR));
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ChatActivity.CLICKACCOUNT, account);
                intent.putExtra(ChatActivity.CLICKNICKNAME, nickname);
                intent.putExtra(ChatActivity.CLICKAVATAR, imgs);
                startActivity(intent);//点击条目跳到聊天界面  并将账号和昵称传过去
            }
        });
        mListViewPhone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = mReadContact.get(position).get("phone");// 读取当前item的电话号码
                //调用系统方法拨打电话
                phone = phone.replaceAll("-", "").replaceAll(" ", "");// 替换-和空格
                //System.out.println("phone:"+phone);
                Intent dialIntent = new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + phone));

                startActivity(dialIntent);
            }
        });
    }

    /**
     * 设置或更新适配器
     */
    private void setOrUpdateAdapter() {
        // 判断adapter是否存在
        if (mAdapter != null) {
            // 刷新adapter就行了
            mAdapter.getCursor().requery();
            return;
        }
        //开启线程同步花名册
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {

                //对应查询记录
                final Cursor c = getActivity().getContentResolver().query(ContactsProvider.URI_CONTACT, null,
                        null, null, null);
                // 假如没有数据的时候
                if (c.getCount() <= 0) {
                    return;
                }

                //设置适配器
                ThreadUtils.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        //baseAdapter的子类
                        //如果view==null 返回一个具体视图
                        //设置显示数据
                        mAdapter = new MyFriendAdapter(getActivity(), c);
                        mListViewFriend.setAdapter(mAdapter);
                    }
                });
            }
        });
    }

    /**
     * =============== 监听数据库记录的改变 ===============
     */
    MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

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
            // 更新adapter或者刷新adapter
            setOrUpdateAdapter();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        // 按照常理,我们Fragment销毁了.那么我们就不应该去继续去监听
        // 但是,实际,我们是需要一直监听对应roster的改变
        // 所以,我们把联系人的监听和同步操作放到Service去
        getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
        super.onDestroy();
    }
}
