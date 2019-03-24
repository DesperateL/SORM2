package com.xiaojiaqi.utils;


/**
 * 封装了字符串常用的操作
 */
public class StringUtils {

    /**
     * 将目标字符串首字母变为大写
     * @param str   目标字符串
     * @return  首字母大写的字符串
     */
    public static String firstChar2UpperCase(String str){
        //abc-->Abc

        return str.substring(0,1).toUpperCase()+str.substring(1);
    }
}
