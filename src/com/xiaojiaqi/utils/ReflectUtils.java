package com.xiaojiaqi.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 负责封装反射相关的操作
 */
public class ReflectUtils {


    /**
     * 调用obj对应属性fieldName的get方法
     *
     * @param fieldName
     * @param obj
     * @return
     */
    public static Object invokeGet(String fieldName,Object obj){
        //通过反射机制，调用属性对应的get方法或set方法
        try {
            Class c = obj.getClass();
            Method m = c.getMethod("get"+ StringUtils.firstChar2UpperCase(fieldName),null);
            return m.invoke(obj,null);


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void invokeSet(Object obj,String columnName,Object columnValue)   {
        //调用rowObj对象的set方法，将columnValue设置进去
        Method m = null;
        try {
            if(columnValue!=null){
                m = obj.getClass().getDeclaredMethod("set"+ StringUtils.firstChar2UpperCase(columnName),columnValue.getClass());
                m.invoke(obj,columnValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
