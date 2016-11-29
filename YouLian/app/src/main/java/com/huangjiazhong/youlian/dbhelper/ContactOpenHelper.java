package com.huangjiazhong.youlian.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/10/17.
 */

public class ContactOpenHelper extends SQLiteOpenHelper {

    public static final String T_CONTACT = "t_contact";
    public static final String T_GROUP = "t_group";

    public class ContactTable implements BaseColumns {//就是会默认给我们添加一列  _id
        public static final String ACCOUNT = "account";//账号
        public static final String NICKNAME = "nickname";//昵称
        public static final String AVATAR = "avatar";//头像
        public static final String PINYIN = "pinyin";//账号拼音
    }
    public class GroupTable implements BaseColumns {//就是会默认给我们添加一列  _id
        public static final String GROUPACCOUNT = "groupaccount";//账号
        public static final String NUMBERACCOUNT = "account";//账号
        public static final String GROUPNAME = "groupname";//昵称
        public static final String GROUPAVATAR = "groupavatar";//头像
        public static final String GROUPPINYIN = "pinyin";//账号拼音
    }

    public ContactOpenHelper(Context context,int version) {
        super(context, "contact.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + T_CONTACT + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactTable.ACCOUNT + " TEXT, " +
                ContactTable.NICKNAME + " TEXT, " +
                ContactTable.AVATAR + " BLOB, " +
                ContactTable.PINYIN + " TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            String sql = "CREATE TABLE " + T_GROUP + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    GroupTable.GROUPACCOUNT + " TEXT, " +
                    GroupTable.NUMBERACCOUNT + " TEXT, " +
                    GroupTable.GROUPNAME + " TEXT, " +
                    GroupTable.GROUPAVATAR + " BLOB, " +
                    GroupTable.GROUPPINYIN+ " TEXT);";
            db.execSQL(sql);
        }
    }
}
