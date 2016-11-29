package com.huangjiazhong.youlian;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.huangjiazhong.youlian.dbhelper.ContactOpenHelper;
import com.huangjiazhong.youlian.provider.ContactsProvider;

/**
 * Created by Administrator on 2016/10/17.
 */

public class TestContactsProvider extends AndroidTestCase {
    public void testInsert() {
        /**
         public static final String ACCOUNT = "account";//账号
         public static final String NICKNAME = "nickname";//昵称
         public static final String AVATAR = "avatar";//头像
         public static final String PINYIN = "pinyin";//账号拼音
         */
//        ContentValues values = new ContentValues();
//        values.put(ContactOpenHelper.ContactTable.ACCOUNT, "xiaoqi@jiazhong.com");
//        values.put(ContactOpenHelper.ContactTable.NICKNAME, "小七");
//        values.put(ContactOpenHelper.ContactTable.AVATAR, "0");
//        values.put(ContactOpenHelper.ContactTable.PINYIN, "xiaoqi");
//        getContext().getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.GroupTable.GROUPACCOUNT, "mygroup@jiazhong.com");
        values.put(ContactOpenHelper.GroupTable.NUMBERACCOUNT, "xiaoqi@jiazhong.com");
        values.put(ContactOpenHelper.GroupTable.GROUPNAME, "我的群");
        values.put(ContactOpenHelper.GroupTable.GROUPAVATAR, "");
        values.put(ContactOpenHelper.GroupTable.GROUPPINYIN, "wodequn");
        getContext().getContentResolver().insert(ContactsProvider.URI_GROUP, values);
    }

    public void testDelete() {
        getContext().getContentResolver().delete(ContactsProvider.URI_CONTACT,
                ContactOpenHelper.ContactTable.NICKNAME + "=?", new String[] { "xiaoqi" });
    }

    public void testUpdate() {
        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, "xiaoqi@jiahzong.com");
        values.put(ContactOpenHelper.ContactTable.NICKNAME, "我是小七");
        values.put(ContactOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactOpenHelper.ContactTable.PINYIN, "woshixiaoqi");
        getContext().getContentResolver().update(ContactsProvider.URI_CONTACT, values,
                ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[] { "xiaoqi@jiazhong.com" });
    }

    public void testQuery() {
        Cursor c = getContext().getContentResolver().query(ContactsProvider.URI_CONTACT, null, null, null, null);
        int columnCount = c.getColumnCount();// 一共多少列
        while (c.moveToNext()) {
            // 循环打印列
            for (int i = 0; i < columnCount; i++) {
                System.out.print(c.getString(i) + "    ");
            }
            System.out.println("");
        }
    }
    public void testPinyin(){
        // String pinyinString = PinyinHelper.convertToPinyinString("内容", "分隔符", 拼音的格式);
//        String pinyinString = PinyinHelper.convertToPinyinString("黄家忠", "", PinyinFormat.WITHOUT_TONE);
//        System.out.println(pinyinString);
    }
}
