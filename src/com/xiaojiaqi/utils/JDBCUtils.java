package com.xiaojiaqi.utils;


import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 封装了JDBC查询常用的操作
 */
public class JDBCUtils {

    public static void handleParams(PreparedStatement ps,Object[] params){
        //给sql设参
        if(params!=null){
            for(int i=0;i<params.length;i++){
                try {
                    ps.setObject(1+i,params[i]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
