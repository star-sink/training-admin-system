package com.trainingadmin.service;

import com.trainingadmin.pojo.Emp;
import com.trainingadmin.pojo.EmpQueryParam;
import com.trainingadmin.pojo.LoginInfo;
import com.trainingadmin.pojo.PageResult;

import java.util.List;

public interface EmpService {

    /**
     * 分页查询
     * @param empQueryParam
     * @return
     */
    PageResult<Emp> page(EmpQueryParam empQueryParam);

    /**
     * 分页查询
     * @param page  页码
     * @param pageSize  每页记录数
     */
//    PageResult<Emp> page(Integer page, Integer pageSize,String name, Integer gender,
//                         LocalDate begin, LocalDate end);

    /**
     *新增员工
     */

    void save(Emp emp);


    /**
     *删除员工
     * @param ids
     */
    void delete(List<Integer> ids);


    /**
     * 查询全部员工（用于班级管理页面的班主任/助教下拉框）
     */
    List<Emp> listAll();

    /**
     * 根据id查询员工信息
     *
     * @return
     */
    Emp getInfo(Integer id);

    /**
     * 修改员工
     * @param emp
     */
    void update(Emp emp);

    LoginInfo login(Emp emp);
}
