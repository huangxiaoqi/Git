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
import com.huangjiazhong.youlian.dbhelper.SmsOpenHelper;
import com.huangjiazhong.youlian.service.IMService;
import com.huangjiazhong.youlian.utils.FormatUtils;
import com.huangjiazhong.youlian.utils.NickNameUtils;

import static com.huangjiazhong.youlian.fragment.SessionFragment.NOTIFYCODE;
import static com.huangjiazhong.youlian.fragment.SessionFragment.mChatActivityIsTop;

/**
 * Created by Administrator on 2016/11/14.
 */

public class SessionAdapter extends CursorAdapter {

    public SessionAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_message, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView friendAvatar = (ImageView) view.findViewById(R.id.friend_avatar);

        //获得图片并为LiestView条目设置头像
        byte[] imgs = cursor.getBlob(cursor.getColumnIndex(SmsOpenHelper.SmsTable.AVATAR));
        if(imgs!=null){
            Drawable avatar = FormatUtils.getInstance().Bytes2Drawable(imgs);
            friendAvatar.setImageDrawable(avatar);
        }
        TextView tvBody = (TextView) view.findViewById(R.id.body);
        TextView tvNickName = (TextView) view.findViewById(R.id.nickname);
        TextView tvMessageStatus = (TextView) view.findViewById(R.id.tv_message_status);
        String body = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
        String acccount = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));

        String nickName = NickNameUtils.getNickNameByAccount(context,acccount);
        // acccount 但是在聊天记录表(sms)里面没有保存别名信息,只有(Contact表里面有)
        tvBody.setText(body);
        tvNickName.setText(nickName);
        //消息提示
        if (NOTIFYCODE > 0 ) {
            if (!mChatActivityIsTop && acccount.equals(IMService.mParticipant)) {
                tvMessageStatus.setText(NOTIFYCODE + "");
                tvMessageStatus.setVisibility(View.VISIBLE);
            }
        }else {
            tvMessageStatus.setText("");
            tvMessageStatus.setVisibility(View.INVISIBLE);
        }
    }
}
