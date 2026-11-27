package com.trainingadmin.service.impl;

import java.util.HashMap;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.trainingadmin.mapper.EmpExprMapper;
import com.trainingadmin.mapper.EmpMapper;
import com.trainingadmin.pojo.*;
import com.trainingadmin.service.EmpLogService;
import com.trainingadmin.service.EmpService;
import com.trainingadmin.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EmpServiceImpl  implements EmpService {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private EmpExprMapper empExprMapper;

    @Autowired
    private EmpLogService empLogService;
    //-------------------------------原始查询分页-----------------------------------
//    @Override
//    public PageResult<Emp> page(Integer page, Integer pageSize) {
//        //1.调用Mapeer接口，查询总记录数
//        Long total=empMapper.count();
//
//        //2.调用Mapper接口，查询结果列表
//        Integer start=(page-1)*pageSize;
//        List<Emp> rows=empMapper.list(start,pageSize);
//
//        //3.封装结果 PageResult
//        return new PageResult<Emp>(total,rows);
//    }
    /**
     * PageHelper分页查询
     * @param page  页码
     * @param pageSize  每页记录数
     * 注意事项：
     *                  1.定义的SQL语句不能加分号;
     *                  2.PageHelper仅仅能对紧跟在其后的第一个查询语句进行分页查询
     */
    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam) {
        //1.设置分页参数
        PageHelper.startPage(empQueryParam.getPage(),empQueryParam.getPageSize());
        //2.执行查询
        List<Emp> empList= empMapper.list(empQueryParam);

        //3.解释查询结果，并封装
        Page<Emp> p =(Page<Emp>) empList;

        return new PageResult<Emp>(p.getTotal(),p.getResult());
    }


    @Transactional(rollbackFor = {Exception.class})//事务管理的注解
    @Override
    public void save(Emp emp) {
        try {
            //1.保存员工基础信息
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());
            empMapper.insert(emp);

            //2.保存员工工作经历信息
            List<EmpExpr> exprList =emp.getExprList();
            if(!CollectionUtils.isEmpty(exprList)){
                //遍历集合，为empId赋值
                exprList.forEach(empExpr->{
                    empExpr.setEmpId(emp.getId());
                });
                empExprMapper.insertBatch(exprList);
            }
        } finally {
            //记录操作日志
            EmpLog empLog = new EmpLog(null,LocalDateTime.now(),"新增员工："+emp);
            empLogService.insertLog(empLog);
        }

    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delete(List<Integer> ids) {
        //1.删除员工基本信息
        empMapper.deleteByIds(ids);

        //2.批量删除员工的工作经历信息
        empExprMapper.deleteByEmpIds(ids);
    }

    /**
     * 查询全部员工
     */
    @Override
    public List<Emp> listAll() {
        return empMapper.listAll();
    }

    /**
     * 根据id查询员工信息
     *
     * @return
     */
    @Override
    public Emp getInfo(Integer id) {

        return empMapper.getById(id);
    }

    /**
     *
     * @param emp
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void update(Emp emp) {
        //1.根据ID修改员工的基本信息
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        //2.根据ID修改员工的工作经历信息
        //2.1 先根据员工ID删除原有的工作经历
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));

        //2.2再添加这个员工新的工作经历
        List<EmpExpr> exprList = emp.getExprList();
        if (!CollectionUtils.isEmpty(exprList)) {
            exprList.forEach(empExpr -> empExpr.setEmpId(emp.getId()));
            empExprMapper.insertBatch(exprList);
        }
    }

    @Override
    public LoginInfo login(Emp emp) {
        //1.调用mapper接口
        Emp e=empMapper.selectByUsernameAndPassword(emp);
        //2.判断：判断是否存在这个员工，如果存在，组装登录成功信息
        if (e!=null){
            log.info("登陆成功，员工信息：{}",e);
            //生成JWT令牌
            HashMap<String, Object> claims = new  HashMap<>();
            claims.put("id",e.getId());
            claims.put("username",e.getUsername());
            String jwt = JwtUtils.generateToken(claims);
            return new LoginInfo(e.getId(),e.getUsername(),e.getName(),jwt);
        }
        //3.不存在，返回null
        return null;
    }
}
