package com.xiaojiaqi.test;

import com.xiaojiaqi.core.MySqlQuery;
import com.xiaojiaqi.core.Query;
import com.xiaojiaqi.core.QueryFactory;
import com.xiaojiaqi.vo.EmpVO;

import java.util.List;

/**
 * 测试连接池的效率
 * @author Leung
 * @date 3/25/2019
 * @time 20:22
 */
public class Test2 {

    public static void test01(){
        Query q = QueryFactory.createQuery();

        String sql2 = "select e.id,e.empname,salary+bonus 'xinshui' ,age,d.dname 'deptName',d.address 'deptAddr' from emp e join dept d on e.deptId=d.id";
        List<EmpVO> list1 = q.queryRows(sql2,
                EmpVO.class,null);

        //System.out.println(list1==null ? "null" : "not null");
        for(EmpVO empVO:list1){
            //System.out.println("???????");
            System.out.println(empVO.getEmpname()+"-"+empVO.getDeptName()+"-"+empVO.getXinshui());
        }
    }
    public static void main(String[] args) {
        long a = System.currentTimeMillis();
        for(int i=0;i<3000;i++){
            test01();
        }
        long b = System.currentTimeMillis();
        System.out.println(b-a);  //不增加连接池 17532；增加连接池后 2243
    }
}
