package com.huangjiazhong.youlian.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.dbhelper.ContactOpenHelper;
import com.huangjiazhong.youlian.dbhelper.SmsOpenHelper;
import com.huangjiazhong.youlian.provider.ContactsProvider;
import com.huangjiazhong.youlian.provider.SmsProvider;
import com.huangjiazhong.youlian.utils.FormatUtils;
import com.huangjiazhong.youlian.utils.PinyinUtils;
import com.huangjiazhong.youlian.utils.ThreadUtils;
import com.huangjiazhong.youlian.xmpp.XmppAdmin;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.huangjiazhong.youlian.xmpp.XmppConnection.SERVICENAME;

/**
 * Created by Administrator on 2016/10/17.
 */

public class IMService extends Service {
    public static XMPPConnection conn;
    private Vibrator mVibrator;
    private SoundPool mPool;

    public static String mCurAccout;                    // 当前登录用户的jid
    public static String mCurPwd;                       // 当前登录用户的密码

    public static Roster mRoster;
    public static Collection<RosterEntry> mEntries;//登录者好友的集合
    private MyRosterListener mRosterListener;

    private Chat mCurChat;
    private Map<String, Chat> mChatMap	= new HashMap<>();

    private ChatManager mChatManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
    public class MyBinder extends Binder{
        public IMService getService(){
            return IMService.this;
        }
    }
    @Override
    public void onCreate() {
        mPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        mPool.load(this, R.raw.tixing, 1);
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                /**=============== 同步花名册 begin ===============*/
                // 需要连接对象
                // 得到花名册对象
                mRoster = IMService.conn.getRoster();
                // 得到所有好友的集合
                mEntries = mRoster.getEntries();

                // 监听联系人的改变
                mRosterListener = new MyRosterListener();
                mRoster.addRosterListener(mRosterListener);
                for (RosterEntry entry : mEntries) {
                    saveOrUpdateEntry(entry);
                }
				/**=============== 同步花名册 end ===============*/
                /**=============== 创建消息的管理者  注册监听 begin ===============*/
                //获得消息管理者
                if (mChatManager == null) {
                    mChatManager = IMService.conn.getChatManager();//消息管理器
                }
                mChatManager.addChatListener(mMyChatManagerListener);//别人主动向自己发消息时走到此
				/**=============== 创建消息的管理者  注册监听 end ===============*/
            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // 移除rosterListener
        if (mRoster != null && mRosterListener != null) {
            mRoster.removeRosterListener(mRosterListener);
        }

        // 移除messageListener
        if (mCurChat != null && mMyMessageListener != null) {
            mCurChat.removeMessageListener(mMyMessageListener);
        }
        super.onDestroy();
    }

    /**
     * RosterListener
     * 联系人状态监听
     */
    class MyRosterListener implements RosterListener {
        @Override
        public void entriesAdded(Collection<String> addresses) {// 联系人添加了
            System.out.println("--------------联系人添加了--------------");
            // 对应更新数据库
            for (String address : addresses) {
                RosterEntry entry = mRoster.getEntry(address);
                // 要么更新,要么插入
                saveOrUpdateEntry(entry);
            }
        }
        // 对应更新数据库
        @Override
        public void entriesUpdated(Collection<String> addresses) {// 联系人修改了
            System.out.println("--------------联系人修改了--------------");
            // 对应更新数据库
            for (String address : addresses) {
                RosterEntry entry = mRoster.getEntry(address);
                // 要么更新,要么插入
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {// 联系人删除了
            System.out.println("--------------联系人删除了--------------");
            // 对应更新数据库
            for (String account : addresses) {
                // 执行删除操作
                getContentResolver().delete(ContactsProvider.URI_CONTACT,
                        ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
            }

        }

        @Override
        public void presenceChanged(Presence presence) {// 联系人状态改变
            String from = presence.getFrom();
            System.out.println("from:"+from);
            /**=============== Presence.Type状态 ===============*/
            if (presence.getType().equals(Presence.Type.available)) {//好友mFroms[0]在线
                if (presence.getMode() == Presence.Mode.chat) {//Q我吧
                    System.out.println("Q我吧:");
                } else if (presence.getMode() == Presence.Mode.dnd) {//正忙
                    System.out.println("正忙:");
                } else if (presence.getMode() == Presence.Mode.xa) {//忙碌
                    System.out.println("忙碌:");
                } else if (presence.getMode() == Presence.Mode.away) {//离开
                    System.out.println("离开:");
                } else {
                    System.out.println("上线");
                }
            }else {//好友mFroms[0]不在线
                System.out.println("下线");
            }
        }
    }

    /**
     * 监听其他账号发送过来的消息
     */
    MyMessageListener	mMyMessageListener	= new MyMessageListener();
    class MyMessageListener implements MessageListener {
        @Override
        public void processMessage(Chat chat, Message message) {
            SharedPreferences sp = getSharedPreferences("YOULIAN", MODE_PRIVATE);
            if (mPool != null && sp.getBoolean("music",true)) {
                mPool.play(1, 1, 1, 0, 0, 1);
            }
            if (mVibrator != null && sp.getBoolean("vibrate",false)) {
                mVibrator.vibrate(500);
            }
            //String body = message.getBody();
            String participant = chat.getParticipant();//获得会话参与者
            saveMessage(participant, message);//保存会话信息
        }
    }

    /**
     * 用于监听别人主动与自己创建的会话的Listener
     */

    MyChatManagerListener mMyChatManagerListener = new MyChatManagerListener();
    public static String mParticipant;
    public class MyChatManagerListener implements ChatManagerListener {

        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {//createdLocally=true表示是自己创建的chat
            // 判断chat是否存在map里面
            // 和我聊天的那个人
            mParticipant = chat.getParticipant();
            // 因为别人创建和我自己创建,参与者(和我聊天的人)对应的jid不同.所以需要统一处理
            mParticipant = filterAccount(mParticipant);
            if (!mChatMap.containsKey(mParticipant)) {
                // 保存chat
                mChatMap.put(mParticipant, chat);
                chat.addMessageListener(mMyMessageListener);
            }
        }
    }
    /**=============== 发送消息 ===============*/
    public void sendMessage(final Message msg) {
        try {

            String toAccount = msg.getTo();

            if (mChatMap.containsKey(toAccount)) {
                mCurChat = mChatMap.get(toAccount);
            } else {
                mCurChat = mChatManager.createChat(toAccount, mMyMessageListener);
                mChatMap.put(toAccount, mCurChat);
            }
            // 发送消息
            mCurChat.sendMessage(msg);
            //保存消息
            saveMessage(toAccount, msg);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    /**=============== 保存或更新联系人 ===============*/
    private void saveOrUpdateEntry(RosterEntry entry) {
        ContentValues values = new ContentValues();
        String account = entry.getUser();
        //获取用户头像并转换成字节数组保存到数据库
        String jid = filterAccount(account);
        if (jid.equals(mCurAccout)){
            return;
        }
        Drawable userImage = XmppAdmin.getUserImage(jid);
        byte[] imgs = null;
        if(userImage!=null){
            imgs = FormatUtils.getInstance().Drawable2Bytes(userImage);
        }

        // 处理昵称
        String nickname = entry.getName();
        if (TextUtils.isEmpty(nickname)) {
            nickname = account.substring(0, account.indexOf("@"));// xiaoqi@jiazhong.com-->billy
        }

        values.put(ContactOpenHelper.ContactTable.ACCOUNT, account);
        values.put(ContactOpenHelper.ContactTable.NICKNAME, nickname);
        values.put(ContactOpenHelper.ContactTable.AVATAR, imgs);
        values.put(ContactOpenHelper.ContactTable.PINYIN, PinyinUtils.getPinyin(account));

        // 先update,后插入-->重点
        int updateCount =
                getContentResolver().update(ContactsProvider.URI_CONTACT, values,
                        ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
        if (updateCount <= 0) {// 没有更新到任何记录
            getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
        }
    }

    /**
     * 保存会话信息
     *
     * @param sessionAccount 会话id
     * @param msg
     */
    private void saveMessage(String sessionAccount, Message msg) {
        ContentValues values = new ContentValues();

        sessionAccount = filterAccount(sessionAccount);
        //获取用户头像
        Drawable userImage = XmppAdmin.getUserImage(sessionAccount);
        byte[] imgs = null;
        if(userImage!=null){
            imgs = FormatUtils.getInstance().Drawable2Bytes(userImage);
        }
        String from = msg.getFrom();
        String to = msg.getTo();
        from = filterAccount(from);
        to = filterAccount(to);
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, from);
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, to);
        values.put(SmsOpenHelper.SmsTable.BODY, msg.getBody());
        values.put(SmsOpenHelper.SmsTable.STATUS, "offline");
        values.put(SmsOpenHelper.SmsTable.TYPE, msg.getType().name());
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.AVATAR, imgs);
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, sessionAccount);
        getContentResolver().insert(SmsProvider.URI_SMS, values);
    }

    private String filterAccount(String accout) {
        return accout.substring(0, accout.indexOf("@")) + "@" + SERVICENAME;
    }
}
