package com.huangjiazhong.youlian.utils;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * 获得汉字拼音的工具类
 * Created by Administrator on 2016/10/19.
 */

public class PinyinUtils {
    public static String getPinyin(String str){
        return PinyinHelper.convertToPinyinString(str,"", PinyinFormat.WITHOUT_TONE);
    }
}
