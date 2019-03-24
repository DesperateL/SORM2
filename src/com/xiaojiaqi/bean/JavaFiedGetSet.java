package com.xiaojiaqi.bean;


/**
 * 封装了Java属性和get、set方法的源代码
 */
public class JavaFiedGetSet {

    /**
     * 属性的源码信息。如private int userId;
     */
    private String fieldInfo;

    /**
     * get方法的源码信息，如：public int getUserId(){return id;}
     */
    private String getInfo;

    /**
     * set方法的源码信息。如:public void setUserId(int id){this.id=id;}
     */
    private String setInfo;

    public String getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(String fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public String getGetInfo() {
        return getInfo;
    }

    public void setGetInfo(String getInfo) {
        this.getInfo = getInfo;
    }

    public String getSetInfo() {
        return setInfo;
    }

    public void setSetInfo(String setInfo) {
        this.setInfo = setInfo;
    }

    public JavaFiedGetSet(String fieldInfo, String getInfo, String setInfo) {
        this.fieldInfo = fieldInfo;
        this.getInfo = getInfo;
        this.setInfo = setInfo;
    }

    public JavaFiedGetSet(){
    }

    @Override
    public String toString() {
        return fieldInfo+getInfo+setInfo;
    }
}
