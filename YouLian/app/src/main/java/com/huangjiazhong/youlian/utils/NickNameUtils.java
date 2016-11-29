package com.huangjiazhong.youlian.utils;

import android.content.Context;
import android.database.Cursor;

import com.huangjiazhong.youlian.dbhelper.ContactOpenHelper;
import com.huangjiazhong.youlian.provider.ContactsProvider;

/**
 * Created by Administrator on 2016/11/14.
 */

public class NickNameUtils {
    /**
     * 获得昵称
     * @param account
     * @return
     */
    public static String getNickNameByAccount(Context context, String account) {
        String nickName = "";
        Cursor c =
                context.getContentResolver().query(ContactsProvider.URI_CONTACT, null,
                        ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[] { account }, null);
        if (c.getCount() > 0) {// 有数据
            c.moveToFirst();
            nickName = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
        }
        return nickName;
    }
}
