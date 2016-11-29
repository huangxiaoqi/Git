package com.huangjiazhong.youlian.xmpp;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.huangjiazhong.youlian.dbhelper.ContactOpenHelper;
import com.huangjiazhong.youlian.entity.XmppUser;
import com.huangjiazhong.youlian.provider.ContactsProvider;
import com.huangjiazhong.youlian.utils.FormatUtils;
import com.huangjiazhong.youlian.utils.PinyinUtils;
import com.huangjiazhong.youlian.utils.ToastUtils;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import static com.google.android.gms.internal.zzs.TAG;
import static com.huangjiazhong.youlian.service.IMService.conn;
import static com.huangjiazhong.youlian.xmpp.XmppConnection.SERVICENAME;

/**
 * Created by Administrator on 2016/11/25.
 */

public class XmppAdmin {
    /**
     * =============== 查找用户 ===============
     */
    public static XmppUser searchUser(String userName) {
        XmppUser user = null;
        UserSearchManager userSearchManager = new UserSearchManager(conn);
        try {
            Form searchForm = userSearchManager.getSearchForm("search."
                    + conn.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("Name", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = userSearchManager.getSearchResults(answerForm,
                    "search." + conn.getServiceName());
            Iterator<ReportedData.Row> rows = data.getRows();
            while (rows.hasNext()) {
                ReportedData.Row row = rows.next();
                String username = row.getValues("Username").next().toString();
                String name = row.getValues("Name").next().toString();
                if (username.equals(userName)) {
                    user = new XmppUser();
                    user.setUserName(username);
                    user.setNickName(name);
                    break;
                }
            }
        } catch (XMPPException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return user;
    }
    /**
     * =============== 注册 ===============
     */
    public static void register(Context context, String account, String password, String nickname) {
        System.out.println("conn.isConnected(): " + conn.isConnected());
        if (conn.isConnected()) {
            Registration reg = new Registration();
            reg.setType(IQ.Type.SET);
            reg.setTo(conn.getServiceName());
            reg.setUsername(account);
            reg.setPassword(password);
            reg.addAttribute("android", "geolo_createUser_android");//做个标志，表明是Android手机创建的
            reg.addAttribute("name", nickname);//添加昵称,name 必须全部小写
            reg.getAttributes();
            AndFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()),
                    new PacketTypeFilter(IQ.class));
            PacketCollector collector = conn.createPacketCollector(filter);
            conn.sendPacket(reg);
            IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
            collector.cancel();
            if (result == null) {
                System.out.println("服务器没有返回结果");
                ToastUtils.showToastSafe(context, "服务器无反应");
            } else if (result.getType() == IQ.Type.RESULT) {
                ToastUtils.showToastSafe(context, "注册成功");
            } else if (result.getType() == IQ.Type.ERROR) {
                if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                    ToastUtils.showToastSafe(context, "此帐号已经存在了，么么哒!");
                } else {
                    ToastUtils.showToastSafe(context, "注册失败!");
                }
            }
        }
    }

    /**
     * =============== 登录 ===============
     */
    public static boolean login(String account, String password) {
        try {
            if (!conn.isAuthenticated()) {
                conn.login(account, password);
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return conn.isAuthenticated();
    }
    /**
     * =============== 添加好友 ===============
     */
    public static boolean addFriend(Context context, String userName, String nickName, String groupName,byte[] imgs) {
        String account = userName + "@" + SERVICENAME;
        Roster roster = conn.getRoster();
        try {
            roster.createEntry(account, nickName, null == groupName ? null
                    : new String[]{groupName});
            saveEntry(context, userName,nickName,imgs);
            return true;
        } catch (XMPPException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }
    /**
     * =============== 将添加的好友保存到本地数据库 ===============
     */
    private static void saveEntry(Context context, String username,String nickName,byte[] imgs) {
        ContentValues values = new ContentValues();
        String account = username + "@" + SERVICENAME;
        String nickname = nickName;
        if (nickname == null || "".equals(nickname)) {
            nickname = account.substring(0, account.indexOf("@"));// xiaoqi@jiazhong.com-->billy
        }
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, account);
        values.put(ContactOpenHelper.ContactTable.NICKNAME, nickname);
        values.put(ContactOpenHelper.ContactTable.AVATAR, imgs);
        values.put(ContactOpenHelper.ContactTable.PINYIN, PinyinUtils.getPinyin(account));
        // 先update,后插入-->重点
        int updateCount =
                context.getContentResolver().update(ContactsProvider.URI_CONTACT, values,
                        ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
        if (updateCount <= 0) {// 没有更新到任何记录
            context.getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
            ToastUtils.showToastSafe(context, "添加好友成功");
        }
    }

    /**
     * =============== 删除好友 ===============
     */
    public static boolean deleteFriend(String userName) {
        Roster roster = conn.getRoster();
        try {
            RosterEntry entry = roster.getEntry(userName);
            if (null != entry) {
                roster.removeEntry(entry);
            }
            return true;
        } catch (XMPPException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    /**
     * =============== 修改好友备注 ===============
     */
    public static void reName(String user, String newName) {

    }
    /**
     * 获取用户头像信息
     *
     * @param jid
     * @return
     */
    public static Drawable getUserImage(String jid) {
        VCard vCard = new VCard();
        ByteArrayInputStream bais = null;
        try {
            // 加入这句代码，解决No VCard for
            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
                    new org.jivesoftware.smackx.provider.VCardProvider());
            vCard.load(conn, jid);//返回Card信息
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        if (vCard == null || vCard.getAvatar() == null) {
            return null;
        }
        bais = new ByteArrayInputStream(vCard.getAvatar());
        if (bais == null) {
            return null;
        }
        Drawable drawable = FormatUtils.getInstance().InputStream2Drawable(bais);
        return drawable;
    }

    /**
     * =============== 修改头像 ===============
     */
    public static boolean changeImage(Bitmap img, String jid) {
        VCard vCard = new VCard();
        try {
            // 加入这句代码，解决No VCard for
            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
                    new org.jivesoftware.smackx.provider.VCardProvider());
            vCard.load(conn,jid);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        byte[] bytes = FormatUtils.getInstance().Bitmap2Bytes(img);
        String encodedImage = StringUtils.encodeBase64(bytes);
        vCard.setAvatar(bytes, encodedImage);
        vCard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage
                + "</BINVAL>", true);

        ByteArrayInputStream bais = new ByteArrayInputStream(vCard.getAvatar());
        FormatUtils.getInstance().InputStream2Bitmap(bais);
        try {
            vCard.save(conn);
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * ==================更改用户状态==================
     */
    public static void setPresence(int code) {
        if (conn == null) {
            return;
        }
        Presence presence;
        switch (code) {
            case 0://离线
                presence = new Presence(Presence.Type.unavailable);
                conn.sendPacket(presence);
                break;
            case 1://在线
                presence = new Presence(Presence.Type.available);
                conn.sendPacket(presence);
                break;
            default:
                break;
        }
    }
}
