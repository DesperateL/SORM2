package com.xiaojiaqi.test;

import com.xiaojiaqi.core.MySqlQuery;
import com.xiaojiaqi.core.Query;
import com.xiaojiaqi.core.QueryFactory;
import com.xiaojiaqi.vo.EmpVO;

import java.util.List;

/**
 * 客户端调用的测试类
 * @author Leung
 * @date 3/25/2019
 * @time 10:26
 */
public class Test {
    public static void main(String[] args) {
        Query q = QueryFactory.createQuery();


        String sql2 = "select e.id,e.empname,salary+bonus 'xinshui' ,age,d.dname 'deptName',d.address 'deptAddr' from emp e join dept d on e.deptId=d.id";
        List<EmpVO> list1 = new MySqlQuery().queryRows(sql2,
                EmpVO.class,null);

        //System.out.println(list1==null ? "null" : "not null");
        for(EmpVO empVO:list1){
            //System.out.println("???????");
            System.out.println(empVO.getEmpname()+"-"+empVO.getDeptName()+"-"+empVO.getXinshui());
        }
    }
}
