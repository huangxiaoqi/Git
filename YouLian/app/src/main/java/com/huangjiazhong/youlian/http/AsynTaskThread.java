package com.huangjiazhong.youlian.http;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;


/**
 * Description: 异步进程类
 */
public class AsynTaskThread extends AsyncTask<Object, Integer, Object> {
    private Handler myHandler = null;
    private int myPage = -1;

    public AsynTaskThread(Handler handler,int page) {
        this.myHandler = handler;
        this.myPage = page;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Object object = null;
        String url = "";
        int i = (int) (1 + Math.random() * (3 - 1 + 1));//产生1-3的随机数
        switch (i) {
            case 1://latest
                url = "http://m2.qiushibaike.com/article/list/latest?count=20&page=" + myPage;
                object = JsonParser.getJokes(HttpGet.getResultString(url));
                break;
            case 2:  // suggest
                url = "http://m2.qiushibaike.com/article/list/suggest?count=20&page=" + myPage;
                object = JsonParser.getJokes(HttpGet.getResultString(url));
                break;
            case 3://
                url = "http://m2.qiushibaike.com/article/list/imgrank?count=20&page=" + myPage;
                object = JsonParser.getJokes(HttpGet.getResultString(url));
                break;
            default:
                break;
        }
        return object;
    }

    @Override
    protected void onPostExecute(Object object) {
        if(myHandler != null){
            Message message = myHandler.obtainMessage();
            message.obj = object;
            myHandler.sendMessage(message);
        }
    }
}
