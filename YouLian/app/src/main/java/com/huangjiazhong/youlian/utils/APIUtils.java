package com.huangjiazhong.youlian.utils;

import android.os.Build;

public class APIUtils {
    public static boolean isSupport(int apiNo){
        return Build.VERSION.SDK_INT >= apiNo;
    }
}
