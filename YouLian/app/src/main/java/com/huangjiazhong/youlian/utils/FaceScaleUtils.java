package com.huangjiazhong.youlian.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 位图缩放工具类
 * Created by Administrator on 2016/11/26.
 */

public class FaceScaleUtils {
    /**
     *
     * @param bitmap 要缩放的位图
     * @param newWidth 缩放后图片的宽
     * @param newHeight 缩放后图片的高
     * @return 返回值
     */
    public static Bitmap bitmapScale(Bitmap bitmap , int newWidth,int newHeight){
        int rawHeigh = bitmap.getHeight();
        int rawWidth = bitmap.getHeight();

//        int newHeight = 50;
//        int newWidth = 50;
        // 计算缩放因子
        float heightScale = ((float) newHeight) / rawHeigh;
        float widthScale = ((float) newWidth) / rawWidth;
        // 新建立矩阵
        Matrix matrix = new Matrix();
        matrix.postScale(heightScale, widthScale);
        // 设置图片的旋转角度
        // matrix.postRotate(-30);
        // 设置图片的倾斜
        // matrix.postSkew(0.1f, 0.1f);
        // 将图片大小压缩
        // 压缩后图片的宽和高以及kB大小均会变化
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeigh, matrix, true);
        return newBitmap;
    }
}
