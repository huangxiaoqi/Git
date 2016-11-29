package com.huangjiazhong.youlian.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.huangjiazhong.youlian.utils.ThreadUtils;
import com.huangjiazhong.youlian.xmpp.XXApp;
import com.huangjiazhong.youlian.xmpp.XmppAdmin;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/14.
 */

public class ChatAdapter extends CursorAdapter {
    public static final int RECEIVE = 1;
    public static final int SEND = 0;
    public Context context;
    public Cursor c;
    public static Drawable mCurAvatar;

    public ChatAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
        this.c = c;
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                mCurAvatar = XmppAdmin.getUserImage(IMService.mCurAccout);//获得当前用户的头像
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        c.moveToPosition(position);
        // 取出消息的创建者
        String fromAccount = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.FROM_ACCOUNT));
        if (!IMService.mCurAccout.equals(fromAccount)) {// 接收
            return RECEIVE;
        } else {// 发送
            return SEND;
        }
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;//view有两种形式
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (getItemViewType(position) == RECEIVE) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_chat_receive, null);
                holder = new ViewHolder();
                convertView.setTag(holder);

                // holder赋值
                holder.avatar = (ImageView) convertView.findViewById(R.id.chat_receive_avatar);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.body = (TextView) convertView.findViewById(R.id.content);
                holder.face = (ImageView) convertView.findViewById(R.id.iv_face);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
        } else {// 发送
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_chat_send, null);
                holder = new ViewHolder();
                convertView.setTag(holder);

                // holder赋值
                holder.avatar = (ImageView) convertView.findViewById(R.id.chat_send_avatar);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.body = (TextView) convertView.findViewById(R.id.content);
                holder.face = (ImageView) convertView.findViewById(R.id.iv_face);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // 得到数据,展示数据

        }
        // 得到数据,展示数据
        c.moveToPosition(position);

        String time = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
        String body = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
        //获得图片并为LiestView条目设置头像
        byte[] imgs = c.getBlob(c.getColumnIndex(SmsOpenHelper.SmsTable.AVATAR));


        String formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(time)));
        holder.time.setText(formatTime);
        if (getItemViewType(position) == RECEIVE) {
            if (imgs != null) {
                Drawable avatar = FormatUtils.getInstance().Bytes2Drawable(imgs);
                holder.avatar.setImageDrawable(avatar);
            } else {
                holder.avatar.setImageResource(R.mipmap.login);
            }
        } else {
            if (mCurAvatar != null) {
                holder.avatar.setImageDrawable(mCurAvatar);
            } else {
                holder.avatar.setImageResource(R.mipmap.login);
            }
        }
        if (body.startsWith("[") && body.endsWith("]")) {
            Integer integer = XXApp.getInstance().getFaceMap().get(body);
            if (integer != null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),integer);
                holder.face.setImageBitmap(bitmap);
                holder.face.setVisibility(View.VISIBLE);
                holder.body.setVisibility(View.GONE);
            } else {
                holder.face.setVisibility(View.GONE);
                holder.body.setVisibility(View.VISIBLE);
            }
        }else {
            holder.body.setText(body);
            holder.face.setVisibility(View.GONE);
            holder.body.setVisibility(View.VISIBLE);
        }
        return super.getView(position, convertView, parent);
    }

    /**
     * 内部类
     */
    class ViewHolder {
        TextView body;
        TextView time;
        ImageView avatar;
        ImageView face;

    }

    /**
     * =============== 必须实现的方法 ===============
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    /**
     * =============== 必须实现的方法 ===============
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
