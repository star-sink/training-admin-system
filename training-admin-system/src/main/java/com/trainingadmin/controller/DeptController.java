package com.trainingadmin.controller;

import com.trainingadmin.anno.Log;
import com.trainingadmin.pojo.Dept;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门管理控制器
 *
 * <p>对外提供的接口：</p>
 * <ul>
 *     <li>GET    /depts        查询全部部门</li>
 *     <li>GET    /depts/{id}   根据 ID 查询部门详情</li>
 *     <li>POST   /depts        新增部门</li>
 *     <li>PUT    /depts        修改部门</li>
 *     <li>DELETE /depts?id=    根据 ID 删除部门</li>
 * </ul>
 */
@Slf4j
@RequestMapping("/depts")
@RestController
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    /**
     * 查询全部部门
     */
    @GetMapping
    public Result list() {
        log.info("查询全部部门数据");
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }

    /**
     * 查询部门详情（修改页面回显）
     */
    @GetMapping("/{id:\\d+}")
    public Result getById(@PathVariable Integer id) {
        log.info("根据 ID 查询部门：{}", id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    /**
     * 新增部门
     */
    @Log
    @PostMapping
    public Result add(@RequestBody Dept dept) {
        log.info("新增部门：{}", dept.getName());
        deptService.add(dept);
        return Result.success();
    }

    /**
     * 修改部门
     */
    @Log
    @PutMapping
    public Result update(@RequestBody Dept dept) {
        log.info("修改部门：{}", dept.getId());
        deptService.update(dept);
        return Result.success();
    }

    /**
     * 删除部门
     */
    @Log
    @DeleteMapping
    public Result delete(Integer id) {
        log.info("根据 ID 删除部门：{}", id);
        deptService.deleteById(id);
        return Result.success();
    }
}
