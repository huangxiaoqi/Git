package com.huangjiazhong.youlian.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/10/20.
 */

public class SmsOpenHelper extends SQLiteOpenHelper {

    public static final String T_SMS = "t_sms";
    public static final String T_GROUP_SMS = "t_group_sms";
    public class SmsTable implements BaseColumns{
        /**
         * from_account;//发送者
         * to_account//接收者
         * body//消息内容
         * status//发送状态
         * type//消息类型
         * time//发送时间
         * session_account//会话id-->最近你和哪些人聊天了-->
         * <p/>
         * //"我"  发送消息  给   "美女1" 对于我来讲  这个会话和谁的会话 ---->"美女1"
         * //"美女1"  发送消息  给   "我" 对于我来讲  这个会话和谁的会话 ---->"美女1"
         */
        public static final String FROM_ACCOUNT = "from_account";
        public static final String TO_ACCOUNT = "to_account";
        public static final String BODY = "body";
        public static final String STATUS = "status";
        public static final String TYPE = "type";
        public static final String TIME = "time";
        public static final String AVATAR = "avatar";//头像
        public static final String SESSION_ACCOUNT = "session_account";
    }
    public class GroupSmsTable implements BaseColumns{
        /**
         * from_account;//发送者
         * to_account//接收者
         * body//消息内容
         * status//发送状态
         * type//消息类型
         * time//发送时间
         * session_account//会话id-->最近你和哪些人聊天了-->
         * <p/>
         * //"我"  发送消息  给   "美女1" 对于我来讲  这个会话和谁的会话 ---->"美女1"
         * //"美女1"  发送消息  给   "我" 对于我来讲  这个会话和谁的会话 ---->"美女1"
         */
        public static final String FROM_ACCOUNT = "from_account";
        public static final String TO_ACCOUNT = "to_account";
        public static final String BODY = "body";
        public static final String TYPE = "type";
        public static final String TIME = "time";
        public static final String AVATAR = "avatar";//头像
        public static final String GROUP_ACCOUNT = "group_account";
    }
    public SmsOpenHelper(Context context,int version) {
        super(context, "sms.db", null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + T_SMS + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                SmsTable.FROM_ACCOUNT + " TEXT," +
                SmsTable.TO_ACCOUNT + " TEXT," +
                SmsTable.STATUS + " TEXT," +
                SmsTable.BODY + " TEXT," +
                SmsTable.TYPE + " TEXT," +
                SmsTable.TIME + " TEXT," +
                SmsTable.AVATAR + " BLOB," +
                SmsTable.SESSION_ACCOUNT + " TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            String sql = "CREATE TABLE " + T_GROUP_SMS + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    GroupSmsTable.FROM_ACCOUNT + " TEXT," +
                    GroupSmsTable.TO_ACCOUNT + " TEXT," +
                    GroupSmsTable.BODY + " TEXT," +
                    GroupSmsTable.TYPE + " TEXT," +
                    GroupSmsTable.TIME + " TEXT," +
                    GroupSmsTable.AVATAR + " BLOB," +
                    GroupSmsTable.GROUP_ACCOUNT + " TEXT);";
            db.execSQL(sql);
        }
    }
}
