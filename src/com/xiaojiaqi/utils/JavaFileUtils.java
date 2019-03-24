package com.xiaojiaqi.utils;


import com.xiaojiaqi.bean.ColumnInfo;
import com.xiaojiaqi.bean.JavaFiedGetSet;
import com.xiaojiaqi.bean.TableInfo;
import com.xiaojiaqi.core.DBManager;
import com.xiaojiaqi.core.MySqlTypeConvertor;
import com.xiaojiaqi.core.TableContext;
import com.xiaojiaqi.core.TypeConvertor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装了Java文件(源代码)常用的操作
 */
public class JavaFileUtils {

    /**
     * 根据字段信息生成Java属性信息。如varchar username-->private String username;以及相应的get和set方法
     * @param column    字段信息
     * @param convertor     类型转化器
     * @return      java属性和set/get方法源码
     */
    public static JavaFiedGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor){
        JavaFiedGetSet jfgs = new JavaFiedGetSet();

        String javaFieldType = convertor.databaseType2JavaType(column.getDataType());

        jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");

        //public String getUsername(){return username;}
        //生成get方法源码
        StringBuilder getSrc = new StringBuilder();
        getSrc.append("\tpublic "+javaFieldType+" get"+StringUtils.firstChar2UpperCase(column.getName())+"(){\n");
        getSrc.append("\t\treturn "+column.getName()+";\n");
        getSrc.append("\t}\n");
        jfgs.setGetInfo(getSrc.toString());

        //public void setUsername(String username){this.username=username;}
        //生成set方法源码
        StringBuilder setSrc = new StringBuilder();
        setSrc.append("\tpublic "+"void"+" set"+StringUtils.firstChar2UpperCase(column.getName())+"(");
        setSrc.append(javaFieldType+" "+column.getName()+"){\n");
        setSrc.append("\t\tthis."+column.getName()+"="+column.getName()+";\n");
        setSrc.append("\t}\n");
        jfgs.setSetInfo(setSrc.toString());

        return jfgs;

    }

    /**
     * 根据表信息生成java类的源代码
     * @param tableInfo     表信息
     * @param convertor     数据类型转化器
     * @return      java类的源代码
     */
    public static String createJavaSrc(TableInfo tableInfo,TypeConvertor convertor){

        Map<String,ColumnInfo> columns = tableInfo.getColumns();
        List<JavaFiedGetSet> javaFields = new ArrayList<>();

        for(ColumnInfo c:columns.values()){
            javaFields.add(createFieldGetSetSRC(c,convertor));
        }

        StringBuilder src = new StringBuilder();

        //生成packsge语句
        src.append("package "+ DBManager.getConf().getPoPackage()+";\n\n");
        //生成import语句
        src.append("import java.sql.*;\n");
        src.append("import java.util.*;\n\n");
        //生成类声明语句
        src.append("public class "+StringUtils.firstChar2UpperCase(tableInfo.getTname())+" {\n\n");
        //生成属性列表
        for(JavaFiedGetSet gs:javaFields){
            src.append(gs.getFieldInfo());
        }
        src.append("\n\n");
        //生成get方法
        for(JavaFiedGetSet gs:javaFields){
            src.append(gs.getGetInfo());
        }
        src.append("\n\n");
        //生成set方法
        for(JavaFiedGetSet gs:javaFields){
            src.append(gs.getSetInfo());
        }
        src.append("\n\n");
        //生成结束
        src.append("}\n");

       // System.out.println(src);
        return src.toString();

    }

    /**
     * 建立表 tableInfo.getTname()对应的java类文件
     * @param tableInfo
     * @param convertor
     */
    public static void createJavaPOFile(TableInfo tableInfo,TypeConvertor convertor){
        String src = createJavaSrc(tableInfo,convertor);

        String srcPath = DBManager.getConf().getSrcPath()+"\\";
        String packsgePath = DBManager.getConf().getPoPackage().replaceAll("\\.","\\\\");

        File f = new File(srcPath+packsgePath);
        //System.out.println(f.getAbsolutePath()+"**********");

        if(!f.exists()){//如果指定目录不存在，帮助用户建立
            f.mkdirs();
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f.getAbsolutePath()+"\\"+StringUtils.firstChar2UpperCase(tableInfo.getTname())+".java"));
            bw.write(src);
            bw.flush();
            System.out.println("建立表："+tableInfo.getTname()+"对应的java类："+StringUtils.firstChar2UpperCase(tableInfo.getTname())+".java");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(bw!=null)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public static void main(String[] args) {
//        ColumnInfo ci = new ColumnInfo("id","int",0);
//        JavaFiedGetSet f = createFieldGetSetSRC(ci,new MySqlTypeConvertor());
//        System.out.println(f);

        Map<String,TableInfo> map = TableContext.tables;
        for(TableInfo table:map.values()){
            createJavaPOFile(table,new MySqlTypeConvertor());
        }




    }
}
