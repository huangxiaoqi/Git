package com.huangjiazhong.youlian.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.huangjiazhong.youlian.dbhelper.ContactOpenHelper;

/**
 * Created by Administrator on 2016/10/17.
 */
public class ContactsProvider extends ContentProvider {
    // 主机地址的常量-->当前类的完整路径
    public static final String AUTHORITIES = ContactsProvider.class.getCanonicalName();            // 得到一个类的完整路径
    // 地址匹配对象
    private static final UriMatcher mUriMatcher;
    // 对应联系人表的一个uri常量
    public static Uri URI_CONTACT = Uri.parse("content://" + AUTHORITIES + "/contact");
    public static Uri URI_GROUP = Uri.parse("content://" + AUTHORITIES + "/group");

    public static final int CONTACT = 1;
    public static final int GROUP = 2;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 添加一个匹配的规则
        mUriMatcher.addURI(AUTHORITIES, "contact", CONTACT);
        mUriMatcher.addURI(AUTHORITIES, "group", GROUP);
    }

    private ContactOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new ContactOpenHelper(getContext(), 2);
        if (mHelper != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * --------------- 增删改查  开始 ---------------
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // 数据是存到sqlite-->创建db文件,建立表-->sqliteOpenHelper
        if (mUriMatcher.match(uri) == CONTACT) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            long id = db.insert(ContactOpenHelper.T_CONTACT, "", values);// 新插入的id
            if (id != -1) {
                // 拼接最新的uri
                // content://com.huangjiazhong.youlian.provider.ContactsProvider/contact/id
                uri = ContentUris.withAppendedId(uri, id);
                // 通知ContentObserver数据改变了
                // getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,"指定只有某一个observer可以收到");//
                getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT, null);// 为null就是所有都可以收到

            }
        } else if (mUriMatcher.match(uri) == GROUP) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            long id = db.insert(ContactOpenHelper.T_GROUP, "", values);// 新插入的id
            if (id != -1) {
                // 拼接最新的uri
                // content://com.huangjiazhong.youlian.provider.ContactsProvider/contact/id
                uri = ContentUris.withAppendedId(uri, id);
                // 通知ContentObserver数据改变了
                // getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,"指定只有某一个observer可以收到");//
                getContext().getContentResolver().notifyChange(ContactsProvider.URI_GROUP, null);// 为null就是所有都可以收到

            }
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteCount = 0;
        if (mUriMatcher.match(uri) == CONTACT) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            // 影响的行数
            deleteCount = db.delete(ContactOpenHelper.T_CONTACT, selection, selectionArgs);
            if (deleteCount > 0) {
                // 通知ContentObserver数据改变了
                // getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,"指定只有某一个observer可以收到");//
                getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT, null);// 为null就是所有都可以收到
            }
        } else if (mUriMatcher.match(uri) == GROUP) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            // 影响的行数
            deleteCount = db.delete(ContactOpenHelper.T_GROUP, selection, selectionArgs);
            if (deleteCount > 0) {
                // 通知ContentObserver数据改变了
                // getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,"指定只有某一个observer可以收到");//
                getContext().getContentResolver().notifyChange(ContactsProvider.URI_GROUP, null);// 为null就是所有都可以收到
            }
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updateCount = 0;
        if (mUriMatcher.match(uri) == CONTACT) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            // 更新的记录总数
            updateCount = db.update(ContactOpenHelper.T_CONTACT, values, selection, selectionArgs);
            if (updateCount > 0) {
                // 通知ContentObserver数据改变了
                // getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,"指定只有某一个observer可以收到");//
                getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT, null);// 为null就是所有都可以收到
            }
        } else if (mUriMatcher.match(uri) == GROUP) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            // 更新的记录总数
            updateCount = db.update(ContactOpenHelper.T_GROUP, values, selection, selectionArgs);
            if (updateCount > 0) {
                // 通知ContentObserver数据改变了
                // getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,"指定只有某一个observer可以收到");//
                getContext().getContentResolver().notifyChange(ContactsProvider.URI_GROUP, null);// 为null就是所有都可以收到
            }
        }
        return updateCount;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        if (mUriMatcher.match(uri) == CONTACT) {
            cursor = mHelper.getReadableDatabase().query(ContactOpenHelper.T_CONTACT,
                    projection, selection, selectionArgs, null, null, sortOrder);
        } else if (mUriMatcher.match(uri) == GROUP) {
            cursor = mHelper.getReadableDatabase().query(ContactOpenHelper.T_GROUP,
                    projection, selection, selectionArgs, null, null, sortOrder);
        }
        return cursor;
    }
    /**--------------- 增删改查  结束 ---------------*/
}
