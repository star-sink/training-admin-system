package com.trainingadmin.controller;

import com.trainingadmin.anno.Log;
import com.trainingadmin.pojo.Emp;
import com.trainingadmin.pojo.EmpQueryParam;
import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 员工管理Controller
 */
@Slf4j
@RequestMapping("/emps")
@RestController
public class EmpController {

    @Autowired
    private EmpService empService;

//    /**
//     * 分页查询
//     */
//    /**
//     * page	是	1	分页查询的页码，如果未指定，默认为1
//     * pageSize	是	10	分页查询的每页记录数，如果未指定，默认为10
//     */
//    @GetMapping
//    public Result pag(@RequestParam(defaultValue = "1") Integer page,//@RequestParam(defaultValue="默认值")
//                      // 设置请求参数默认值
//                      @RequestParam(defaultValue = "10") Integer pageSize,
//                      String name, Integer gender,
//                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
//                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
//
//        log.info("分页查询:{},{},{},{},{},{},{}",page,pageSize,name,gender,begin,end);
//        PageResult<Emp> pageResult=empService.page(page,pageSize,name,gender,begin,end);
//        return Result.success(pageResult);
//    }

    /**
     * 分页查询
     */
    /**
     * page	是	1	分页查询的页码，如果未指定，默认为1
     * pageSize	是	10	分页查询的每页记录数，如果未指定，默认为10
     */

    @GetMapping
    public Result pag(EmpQueryParam empQueryParam) {

        log.info("分页查询:{}",empQueryParam);
        PageResult<Emp> pageResult=empService.page(empQueryParam);
        return Result.success(pageResult);
    }

    /**
     *新增员工
     */
    @Log
    @PostMapping
    public Result save(@RequestBody Emp emp) {
        log.info("新增员工：{}",emp);
        empService.save(emp);
        return Result.success();

    }


    /**
     * 删除员工--数组
     */
/*    @DeleteMapping()
    public Result delete(Integer[] ids) {
        log.info("删除员工", Arrays.toString(ids));
        return Result.success();
    }*/
    /**
     * 删除员工--注解
     */
    @Log
    @DeleteMapping()
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("删除员工", ids);
        empService.delete(ids);
        return Result.success();
    }

    /**
     * 查询全部员工（班级管理页面的班主任/助教下拉框）
     * 必须是具体路径，否则会被下面的 /{id} 当作 id 处理，导致 "list" 转 Integer 失败
     */
    @GetMapping("/list")
    public Result listAll() {
        log.info("查询全部员工数据");
        List<Emp> empList = empService.listAll();
        return Result.success(empList);
    }

    /**
     * 根据id查询员工信息
     * {id:\\d+} 只匹配数字，避免 /emps/list 之类的具体路径被当成路径参数
     */
    @GetMapping("/{id:\\d+}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("根据id查询员工信息{}",id);
        Emp emp= empService.getInfo(id);
        return Result.success(emp);
    }


    /**
     * 修改员工
     */
    @Log
    @PutMapping
    public Result update(@RequestBody Emp emp) {
        log.info("修改员工:{}",emp);
        empService.update(emp);
        return Result.success();
    }


}
