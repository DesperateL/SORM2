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

public class MySqlQuery extends Query{




    public static void main(String[] args) {
       //testQueryRows();
        testValue();
    }
    public static void testQueryRows(){
        List<Emp> list = new MySqlQuery().queryRows("select id,empname,age from emp where age>? and salary>?",
                Emp.class,new Object[]{18,10000});

        for(Emp e:list){
            System.out.println(e.getEmpname());
        }

        String sql2 = "select e.id,e.empname,salary+bonus 'xinshui' ,age,d.dname 'deptName',d.address 'deptAddr' from emp e join dept d on e.deptId=d.id";
        List<EmpVO> list1 = new MySqlQuery().queryRows(sql2,
                EmpVO.class,null);

        //System.out.println(list1==null ? "null" : "not null");
        for(EmpVO empVO:list1){
            //System.out.println("???????");
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

    public static void testValue(){
        Object obj = new MySqlQuery().queryValue("select count(*) from emp where salary>?",new Object[]{1000});
        System.out.println(obj);
    }
    @Override
    public Object queryPagenate(int pageNum, int size) {
        return null;
    }
}
