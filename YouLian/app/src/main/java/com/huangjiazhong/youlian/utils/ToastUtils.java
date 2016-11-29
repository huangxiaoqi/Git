package com.huangjiazhong.youlian.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/10/14.
 */

public class ToastUtils {
    public static void showToastSafe(final Context context,final String text){
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
