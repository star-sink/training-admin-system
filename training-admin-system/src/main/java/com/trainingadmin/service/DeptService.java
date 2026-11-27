package com.trainingadmin.service;

import com.trainingadmin.pojo.Dept;

import java.util.List;

public interface DeptService {
    /**
     * 查询所有部门
     */
    List<Dept> findAll();

    /**
     * 根据id删除部门
     */
    void deleteById(Integer id);
    /**
     * 新增部门
     */
    void add(Dept dept);

    /**
     * 修改部门(查询回显）
     */
    Dept getById(Integer id);



    /**
     * 修改部门(修改数据）
     */
    void update(Dept dept);
}
