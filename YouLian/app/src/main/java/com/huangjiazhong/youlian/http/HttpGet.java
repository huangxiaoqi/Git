package com.huangjiazhong.youlian.http;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Description: GET方法获取网络数据
 *
 */
public class HttpGet {

    public static String getResultString(String urlString){
        String result = "";
        URL url;
        try {
            url = new URL(urlString);
            URLConnection conn = url.openConnection();
            int reCode = ((HttpURLConnection) conn).getResponseCode();
            if (reCode == 200) {
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer buffer = new ByteArrayBuffer(32);
                int read;
                while ((read = bis.read()) != -1) {
                    buffer.append((char) read);
                }
                result = EncodingUtils.getString(buffer.toByteArray(), HTTP.UTF_8);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }

}
