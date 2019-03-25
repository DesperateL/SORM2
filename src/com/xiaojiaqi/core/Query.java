package com.xiaojiaqi.core;

import com.xiaojiaqi.bean.ColumnInfo;
import com.xiaojiaqi.bean.TableInfo;
import com.xiaojiaqi.utils.JDBCUtils;
import com.xiaojiaqi.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责查询（对外提供服务的核心类）
 */
@SuppressWarnings("all")
public abstract class Query implements Cloneable{

    /**
     * 采用模板方法模式将JCDBC操作封装成模板，便于重用
     * @param sql   sql语句
     * @param params    sql参数
     * @param clazz     记录要封装到的javaBean
     * @param back     CallBack实现类  回调方法
     * @return
     */
    public Object executeQueryTemplate(String sql,Object[] params,Class clazz,CallBack back){
        Connection conn = DBManager.getConn();
        //List list = null;  //存储查询结果的容器
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            //给sql设参
            JDBCUtils.handleParams(ps,params);
            //ps.

            System.out.println(ps);
            rs = ps.executeQuery();

            return back.doExecute(conn,ps,rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            DBManager.close(ps, conn);
        }

    }
    /**
     * 直接执行一个DML语句
     * @param sql   sql语句
     * @param params    参数
     * @return      执行sql语句后影响记录的行数
     */
    public int executeDML(String sql,Object[] params){
        Connection conn = DBManager.getConn();
        int count = 0;
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(sql);

            //给sql设参
            JDBCUtils.handleParams(ps,params);
            //ps.
            count = ps.executeUpdate();
            System.out.println(ps);

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBManager.close(ps, conn);
        }
        return count;
    }

    /**
     * 将一个对象存储到数据库中
     * 把对象中不为null的属性存储到数据库中
     * @param obj   要存储的对象
     */
    public void insert(Object obj){
        //obj-->表中。  insert into 表名 (id,uname,pwd) values(?,?,?)
        Class c = obj.getClass();
        List<Object> params = new ArrayList<>();//存储sql的参数对象
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);

        //目前，只能处理数据库来维护自增主键的方式

        StringBuilder sql = new StringBuilder("insert into "+tableInfo.getTname()+" (");
        int countNotNullField = 0;
        Field[] fs = c.getDeclaredFields();
        for(Field f:fs){
            String fieldName = f.getName();
            Object fieldValues = ReflectUtils.invokeGet(fieldName,obj);

            if(fieldValues!=null) {
                countNotNullField++;
                sql.append(fieldName + ",");
                params.add(fieldValues);
            }
        }
        sql.setCharAt(sql.length()-1,')');
        sql.append(" values (");
        for(int i=0;i<countNotNullField;i++){
            sql.append("?,");
        }
        sql.setCharAt(sql.length()-1,')');

        executeDML(sql.toString(),params.toArray());
    };

    /**
     * 删除clazz表示类对应表中的记录（指定主键id的值）
     * @param clazz     与表对应的类的Class对象
     * @param id    主键的值
     *
     */
    public void delete(Class clazz,Object id){
        //Emp.class,2  --> delete from emp where id=2
        //通过Class对象找TableInfo       User-->User
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);

        //获得主键
        ColumnInfo onlyPriKey =  tableInfo.getOnlyPriKey();

        String sql = "delete from "+tableInfo.getTname()+" where "+onlyPriKey.getName()+"=?";

        executeDML(sql,new Object[]{id});
    } //delete from User where id=


    /**
     *  删除对象在数据库中的记录（对象所在的类对应到表，对象主键的值对应到记录）
     * @param obj
     */
    public void delete(Object obj){
        Class c  = obj.getClass();

        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        //主键
        ColumnInfo onlyPriKey =  tableInfo.getOnlyPriKey();

        //通过反射机制，调用属性对应的get方法或set方法

        Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(),obj);

        delete(c,priKeyValue);
    }

    /**
     * 更新对象对应的记录，并且只更新指定的字段的值
     * @param obj   要更新的对象
     * @param fieldNames    要更新的属性列表
     * @return      执行sql语句后影响记录的行数
     */
    public int update(Object obj,String[] fieldNames){
        //obj-->表中。  insert into 表名 (id,uname,pwd) values(?,?,?)
        Class c = obj.getClass();
        List<Object> params = new ArrayList<>();//存储sql的参数对象
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);

        //目前，只能处理数据库来维护自增主键的方式

        StringBuilder sql = new StringBuilder("insert into "+tableInfo.getTname()+" (");
        int countNotNullField = 0;
        Field[] fs = c.getDeclaredFields();
        for(Field f:fs){
            String fieldName = f.getName();
            Object fieldValues = ReflectUtils.invokeGet(fieldName,obj);

            if(fieldValues!=null) {
                countNotNullField++;
                sql.append(fieldName + ",");
                params.add(fieldValues);
            }
        }
        sql.setCharAt(sql.length()-1,')');
        sql.append(" values (");
        for(int i=0;i<countNotNullField;i++){
            sql.append("?,");
        }
        sql.setCharAt(sql.length()-1,')');

        return executeDML(sql.toString(),params.toArray());
    }; //update user set uname=?,pwd=?

    /**
     * 查询返回多行记录，并将每行记录封装到clazz指定的类的对象中
     * @param sql   查询语句
     * @param clazz     封装数据的javabean类的Class对象
     * @param params    sql的参数
     * @return  查询到的结果
     */
    public List queryRows(final String sql,final Class clazz,Object[] params){



        return (List) executeQueryTemplate(sql, params, clazz, new CallBack() {
            @Override
            public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
                List list = null;//存储查询结果的容器
                try {

                    ResultSetMetaData metaData = rs.getMetaData();
                    while (rs.next()) {
                        if(list==null){
                            list = new ArrayList();
                        }
                        Object rowObj = null;//调用javabean的无参构造器
                        try {
                            rowObj = clazz.newInstance();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        //多列  selsect username,pwd,age from user id>? and age>18
                        for (int i = 0; i < metaData.getColumnCount(); i++) {
                            String columnName = metaData.getColumnLabel(i + 1);//JDBC索引从1开始
                            Object columnValue = rs.getObject(i + 1);

                            //调用rowObj对象的set方法，将columnValue设置进去
                            ReflectUtils.invokeSet(rowObj, columnName, columnValue);
                        }

                        list.add(rowObj);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return list;
            }
        });



    }

    /**
     * 查询返回单行记录，并将该行记录封装到clazz指定的类的对象中
     * @param sql   查询语句
     * @param clazz     封装数据的javabean类的Class对象
     * @param params    sql的参数
     * @return  查询到的结果
     */
    public Object queryUniqueRows(String sql,Class clazz,Object[] params){
        List list = queryRows(sql,clazz,params);

        return (list==null||list.size()==0) ? null : list.get(0);
    };

    /**
     * 查询返回一个值（一行一列），并将该值返回
     * @param sql   查询语句
     * @param params    sql的参数
     * @return  查询到的结果
     */
    public Object queryValue(String sql,Object[] params){


        return  executeQueryTemplate(sql, params, null, new CallBack() {
            @Override
            public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
                Object value = null;
                try{

                    while (rs.next()) {
                        //select count(*) from user
                        value = rs.getObject(1);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return value;
            }
        });

    };

    /**
     * 查询返回一个数字，并将该值返回
     * @param sql   查询语句
     * @param params    sql的参数
     * @return  查询到的结果
     */
    public Number queryNumber(String sql,Object[] params){
        return (Number) queryValue(sql,params);
    }

    /**
     * 分页查询
     * @param pageNum  第几页数据
     * @param size  每页显示多少数据
     * @return
     */
    public abstract Object queryPagenate(int pageNum,int size);

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

