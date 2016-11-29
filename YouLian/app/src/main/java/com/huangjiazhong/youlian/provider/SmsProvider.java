package com.huangjiazhong.youlian.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.huangjiazhong.youlian.dbhelper.SmsOpenHelper;

/**
 * Created by Administrator on 2016/10/20.
 */

public class SmsProvider extends ContentProvider{

    public static final String	AUTHORITIES	= SmsProvider.class.getCanonicalName();

    private static UriMatcher mUriMatcher;

    public static Uri			URI_SESSION	  = Uri.parse("content://" + AUTHORITIES + "/session");
    public static Uri			URI_SMS		  = Uri.parse("content://" + AUTHORITIES + "/sms");
    public static Uri			URI_GROUP_SMS = Uri.parse("content://" + AUTHORITIES + "/group_sms");

    public static final int		SMS			= 1;
    public static final int		SESSION		= 2;
    public static final int		GROUP_SMS	= 3;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 添加匹配规则
        mUriMatcher.addURI(AUTHORITIES, "sms", SMS);
        mUriMatcher.addURI(AUTHORITIES, "session", SESSION);
        mUriMatcher.addURI(AUTHORITIES, "group_sms", GROUP_SMS);
    }


    private SmsOpenHelper mHelper;
    @Override
    public boolean onCreate() {
        mHelper = new SmsOpenHelper(getContext(),2);
        if(mHelper != null){
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**=========增删改查方法开始==============*/
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(mUriMatcher.match(uri) == SMS){
            // 插入之后对于的id
            long id = mHelper.getWritableDatabase().insert(SmsOpenHelper.T_SMS, "", values);
            if (id > 0) {
                uri = ContentUris.withAppendedId(uri, id);
                // 发送数据改变的信号
                getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS, null);
            }
        }else if(mUriMatcher.match(uri) == GROUP_SMS){
            // 插入之后对于的id
            long id = mHelper.getWritableDatabase().insert(SmsOpenHelper.T_GROUP_SMS, "", values);
            if (id > 0) {
                uri = ContentUris.withAppendedId(uri, id);
                // 发送数据改变的信号
                getContext().getContentResolver().notifyChange(SmsProvider.URI_GROUP_SMS, null);
            }
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteCount = 0;// 具体删除了几条数据
        if(mUriMatcher.match(uri) == SMS){
            deleteCount = mHelper.getWritableDatabase().delete(SmsOpenHelper.T_SMS, selection, selectionArgs);
            if (deleteCount > 0) {
                // 发送数据改变的信号
                getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS, null);
            }
        }else if(mUriMatcher.match(uri) == SESSION){
            deleteCount = mHelper.getWritableDatabase().delete(SmsOpenHelper.T_SMS, selection, selectionArgs);
            if (deleteCount > 0) {
                // 发送数据改变的信号
                getContext().getContentResolver().notifyChange(SmsProvider.URI_SESSION, null);
            }
        }else if(mUriMatcher.match(uri) == GROUP_SMS){
            deleteCount = mHelper.getWritableDatabase().delete(SmsOpenHelper.T_GROUP_SMS, selection, selectionArgs);
            if(deleteCount > 0) {
                // 发送数据改变的信号
                getContext().getContentResolver().notifyChange(SmsProvider.URI_GROUP_SMS, null);
            }
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updateCount = 0;// 更新了几条数据
        int match = mUriMatcher.match(uri);
        if(match == SMS){
            updateCount = mHelper.getWritableDatabase().update(SmsOpenHelper.T_SMS, values, selection, selectionArgs);
            if (updateCount > 0) {
                // 发送数据改变的信号
                getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS, null);
            }
        }else if(match == GROUP_SMS){
            updateCount = mHelper.getWritableDatabase().update(SmsOpenHelper.T_GROUP_SMS, values, selection, selectionArgs);
            if (updateCount > 0) {
                // 发送数据改变的信号
                getContext().getContentResolver().notifyChange(SmsProvider.URI_GROUP_SMS, null);
            }
        }
        return updateCount;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        if (mUriMatcher.match(uri) == SMS){
            SQLiteDatabase db = mHelper.getReadableDatabase();
            cursor =db.query(SmsOpenHelper.T_SMS, projection, selection, selectionArgs,
                    null,null,sortOrder);
        }else if(mUriMatcher.match(uri) == SESSION){
            SQLiteDatabase db = mHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM "//
                    + "(SELECT * FROM t_sms WHERE from_account = ? or to_account = ? ORDER BY time ASC)" //
                    + " GROUP BY session_account", selectionArgs);//
        }else if(mUriMatcher.match(uri) == GROUP_SMS){
            SQLiteDatabase db = mHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM "//
                    + "(SELECT * FROM t_group_sms WHERE group_account = ? ORDER BY time ASC)" , selectionArgs);//
        }
        return cursor;
    }
    /**=========增删改查方法结束=============*/
}
