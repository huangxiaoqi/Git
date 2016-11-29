package com.huangjiazhong.youlian.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.dbhelper.ContactOpenHelper;
import com.huangjiazhong.youlian.utils.FormatUtils;

/**
 * Created by Administrator on 2016/11/14.
 */

public class MyFriendAdapter extends CursorAdapter {
    public MyFriendAdapter(Context context, Cursor c) {
        super(context, c);
    }
    //如果view==null 返回一个具体视图
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_contact, null);
        return view;
    }

    //设置显示数据
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String account = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
        //获得图片并为LiestView条目设置头像
        byte[] imgs = cursor.getBlob(cursor.getColumnIndex(ContactOpenHelper.ContactTable.AVATAR));
        String nickName = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));

        ImageView friendAvatar = (ImageView) view.findViewById(R.id.friend_avatar);
        TextView tvAccount = (TextView) view.findViewById(R.id.account);
        TextView tvNickName = (TextView) view.findViewById(R.id.nickname);
        //ImageView ivFriendState = (ImageView) view.findViewById(R.id.iv_friend_state);

        if (imgs != null) {
            Drawable avatar = FormatUtils.getInstance().Bytes2Drawable(imgs);
            friendAvatar.setImageDrawable(avatar);
        }
        tvAccount.setText(account);
        tvNickName.setText(nickName);
    }
}
