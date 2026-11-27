package com.trainingadmin.controller;

import com.trainingadmin.anno.Log;
import com.trainingadmin.pojo.Emp;
import com.trainingadmin.pojo.EmpQueryParam;
import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 员工管理控制器
 *
 * <p>对外提供的接口：</p>
 * <ul>
 *     <li>GET    /emps        分页条件查询员工</li>
 *     <li>GET    /emps/list   查询全部员工（下拉框）</li>
 *     <li>GET    /emps/{id}   根据 ID 查询员工详情（含工作经历）</li>
 *     <li>POST   /emps        新增员工</li>
 *     <li>PUT    /emps        修改员工</li>
 *     <li>DELETE /emps?ids=   批量删除员工</li>
 * </ul>
 */
@Slf4j
@RequestMapping("/emps")
@RestController
public class EmpController {

    private final EmpService empService;

    public EmpController(EmpService empService) {
        this.empService = empService;
    }

    /**
     * 分页条件查询员工
     */
    @GetMapping
    public Result page(EmpQueryParam empQueryParam) {
        log.info("分页查询员工：{}", empQueryParam);
        PageResult<Emp> pageResult = empService.page(empQueryParam);
        return Result.success(pageResult);
    }

    /**
     * 新增员工
     */
    @Log
    @PostMapping
    public Result save(@RequestBody Emp emp) {
        log.info("新增员工，用户名：{}，姓名：{}", emp.getUsername(), emp.getName());
        empService.save(emp);
        return Result.success();
    }

    /**
     * 批量删除员工
     */
    @Log
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("批量删除员工：{}", ids);
        empService.delete(ids);
        return Result.success();
    }

    /**
     * 查询全部员工（班级管理页面的班主任等下拉框）
     *
     * <p>必须声明为具体路径，否则会被 /{id} 当作路径参数处理。</p>
     */
    @GetMapping("/list")
    public Result listAll() {
        log.info("查询全部员工数据");
        List<Emp> empList = empService.listAll();
        return Result.success(empList);
    }

    /**
     * 根据 ID 查询员工信息
     *
     * <p>{@code {id:\\d+}} 只匹配数字，避免 /emps/list 之类的具体路径被当成路径参数。</p>
     */
    @GetMapping("/{id:\\d+}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据 ID 查询员工信息：{}", id);
        Emp emp = empService.getInfo(id);
        return Result.success(emp);
    }

    /**
     * 修改员工
     */
    @Log
    @PutMapping
    public Result update(@RequestBody Emp emp) {
        log.info("修改员工，ID：{}", emp.getId());
        empService.update(emp);
        return Result.success();
    }
}
