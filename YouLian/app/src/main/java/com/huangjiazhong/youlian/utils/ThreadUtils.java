package com.huangjiazhong.youlian.utils;

import android.os.Handler;

/**
 * 线程工具类
 * Created by Administrator on 2016/10/14.
 */

public class ThreadUtils {
    /**
     * 在子线程中执行的方法
     * @param task
     */
    public static void runInThread(Runnable task){
        new Thread(task).start();
    }
    //主线程中的Handdler
    private static Handler mHandler = new Handler();

    /**
     * 在UI县城中执行的方法
     * @param task
     */
    public static void runInUIThread(Runnable task){
        mHandler.post(task);
    }
}
