package com.trainingadmin.service.impl;

import com.trainingadmin.mapper.DeptMapper;
import com.trainingadmin.pojo.Dept;
import com.trainingadmin.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptMapper deptMapper;
    /**
     * 查询所有的部门数据
     */
    @Override
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }
    /**
     *根据id删除部门
     */
    @Override
    public void deleteById(Integer id) {
        deptMapper.deleteById(id);
    }
    /**
     *新增部门
     */
    @Override
    public void add(Dept dept) {
        //1.补全基础属性 -CreateTime，UpdateTime(
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());

        //2.调用Mapper接口方法插入数据
        deptMapper.insert(dept);

    }
    /**
     * 修改部门(查询回显）
     */
    @Override
    public Dept getById(Integer id) {
        return deptMapper.getById(id);
    }
    /**
     * 修改部门(修改数据）
     */

    @Override
    public void update(Dept dept) {
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.update(dept);
    }
}
