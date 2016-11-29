package com.huangjiazhong.youlian;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.huangjiazhong.youlian.dbhelper.SmsOpenHelper;
import com.huangjiazhong.youlian.provider.SmsProvider;

/**
 * Created by Administrator on 2016/10/20.
 */

public class TestSmsProvider extends AndroidTestCase{
    public void testInsert() {
        /**
         public static final String FROM_ACCOUNT = "from_account";
         public static final String TO_ACCOUNT = "to_account";
         public static final String BODY = "body";
         public static final String STATUS = "status";
         public static final String TYPE = "type";
         public static final String TIME = "time";
         public static final String SESSION_ACCOUNT = "session_account";
         */
//        ContentValues values = new ContentValues();
//        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, "xiaoqi@jiazhong.com");
//        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, "zhangsan@jiazhong.com");
//        values.put(SmsOpenHelper.SmsTable.BODY, "今晚约吗?");
//        values.put(SmsOpenHelper.SmsTable.STATUS, "offline");
//        values.put(SmsOpenHelper.SmsTable.TYPE, "chat");
//        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
//        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, "zhangsan@jiazhong.com");
//        getContext().getContentResolver().insert(SmsProvider.URI_SMS, values);
        ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.GroupSmsTable.FROM_ACCOUNT, "xiaoqi@jiazhong.com");
        values.put(SmsOpenHelper.GroupSmsTable.TO_ACCOUNT, "zhangsan@jiazhong.com");
        values.put(SmsOpenHelper.GroupSmsTable.BODY, "今晚约吗?");
        values.put(SmsOpenHelper.GroupSmsTable.TYPE, "chat");
        values.put(SmsOpenHelper.GroupSmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.GroupSmsTable.AVATAR, "");
        values.put(SmsOpenHelper.GroupSmsTable.GROUP_ACCOUNT, "zhangsan@jiazhong.com");
        getContext().getContentResolver().insert(SmsProvider.URI_GROUP_SMS, values);
    }

    public void testDelete() {
//        getContext().getContentResolver().delete(SmsProvider.URI_SMS, SmsOpenHelper.SmsTable.FROM_ACCOUNT + "=?",
//                new String[] { "xiaoqi@jiazhong.com" });
        getContext().getContentResolver().delete(SmsProvider.URI_SMS, null,
                null);
    }

    public void testUpdate() {
        ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.SmsTable.BODY, "今晚约吗?我好久没有看到你了.");
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, "zhangsan@jiazhong.com");
        getContext().getContentResolver().update(SmsProvider.URI_SMS, values,
                SmsOpenHelper.SmsTable.FROM_ACCOUNT + "=?", new String[] { "xiaoqi@jiazhong.com" });
    }

    public void testQuery() {
        Cursor c = getContext().getContentResolver().query(SmsProvider.URI_SMS, null, null, null, null);
        // 得到所有的列
        int columnCount = c.getColumnCount();
        while (c.moveToNext()) {
            for (int i = 0; i < columnCount; i++) {
                System.out.print(c.getString(i) + "  ");
            }
            System.out.println("");
        }
    }
}
