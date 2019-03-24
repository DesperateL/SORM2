package com.xiaojiaqi.core;

import com.xiaojiaqi.bean.ColumnInfo;
import com.xiaojiaqi.bean.TableInfo;
import com.xiaojiaqi.po.Emp;
import com.xiaojiaqi.utils.JDBCUtils;
import com.xiaojiaqi.utils.ReflectUtils;
import com.xiaojiaqi.utils.StringUtils;
import com.xiaojiaqi.vo.EmpVO;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlQuery implements Query{


    @Override
    public int executeDML(String sql, Object[] params) {
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

    @Override
    public void insert(Object obj) {
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

    }

    @Override
    public void delete(Class clazz, Object id) {
        //Emp.class,2  --> delete from emp where id=2
        //通过Class对象找TableInfo       User-->User
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);

        //获得主键
        ColumnInfo onlyPriKey =  tableInfo.getOnlyPriKey();

        String sql = "delete from "+tableInfo.getTname()+" where "+onlyPriKey.getName()+"=?";

        executeDML(sql,new Object[]{id});
    }

    @Override
    public void delete(Object obj) {
        Class c  = obj.getClass();

        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        //主键
        ColumnInfo onlyPriKey =  tableInfo.getOnlyPriKey();

        //通过反射机制，调用属性对应的get方法或set方法

        Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(),obj);

        delete(c,priKeyValue);
    }

    @Override
    public int update(Object obj, String[] fieldNames) {
        //obj{'uname','pwd'}  --> update 表名 set uname=?,pwd=? where id=?

        Class c = obj.getClass();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        List<Object> params = new ArrayList<>();
        ColumnInfo priKey = tableInfo.getOnlyPriKey();
        StringBuilder sql = new StringBuilder("update "+tableInfo.getTname()+" set ");

        for(String fname:fieldNames){
            Object fvalue = ReflectUtils.invokeGet(fname,obj);
            sql.append(fname+"=?,");
            params.add(fvalue);
        }
        params.add(ReflectUtils.invokeGet(priKey.getName(),obj));  //主键的值
        sql.setCharAt(sql.length()-1,' ');
        sql.append(" where ");
        sql.append(priKey.getName()+"=?");

        return executeDML(sql.toString(),params.toArray());


    }

    @Override
    public List queryRows(String sql, Class clazz, Object[] params)   {
        Connection conn = DBManager.getConn();
        List list = null;  //存储查询结果的容器
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            //给sql设参
            JDBCUtils.handleParams(ps,params);
            //ps.

            System.out.println(ps);
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                if (list == null) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBManager.close(ps, conn);
        }

        return list;
    }

    @Override
    public Object queryUniqueRows(String sql, Class clazz, Object[] params) {
        List list = queryRows(sql,clazz,params);

        return (list==null||list.size()==0) ? null : list.get(0);
    }

    @Override
    public Object queryValue(String sql, Object[] params) {
        Connection conn = DBManager.getConn();
        Object value = null;  //存储查询结果的容器
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            //给sql设参
            JDBCUtils.handleParams(ps,params);
            //ps.

            System.out.println(ps);
            rs = ps.executeQuery();

            while (rs.next()) {
                //select count(*) from user
                value = rs.getObject(1);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBManager.close(ps, conn);
        }

        return value;
    }

    @Override
    public Number queryNumber(String sql, Object[] params) {

        return (Number) queryValue(sql,params);
    }

    public static void main(String[] args) {
        Object obj = new MySqlQuery().queryValue("select count(*) from emp where salary>?",new Object[]{1000});
        System.out.println(obj);
    }
    public static void testQueryRows(){
        List<Emp> list = new MySqlQuery().queryRows("select id,empname,age from emp where age>? and salary>?",
                Emp.class,new Object[]{18,10000});

        String sql2 = "select e.id,e.empname,salary+bonus 'xinshui' ,age,d.dname 'deptName',d.address 'deptAddr' from emp e join dept d on e.deptId=d.id";
        List<EmpVO> list1 = new MySqlQuery().queryRows(sql2,
                EmpVO.class,null);

        //System.out.println(list1);
        for(EmpVO empVO:list1){
            System.out.println(empVO.getEmpname()+"-"+empVO.getDeptName()+"-"+empVO.getXinshui());
        }
    }
    public static void testDML(){
        Emp e = new Emp();
        e.setId(6);
        e.setEmpname("Jonylll");
        e.setBirthday(new Date(System.currentTimeMillis()));
        e.setAge(19);

        //new MySqlQuery().delete(e);
        new MySqlQuery().update(e,new String[]{"empname","birthday","age"});
    }
}
