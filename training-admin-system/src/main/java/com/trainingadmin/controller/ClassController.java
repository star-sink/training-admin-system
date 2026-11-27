package com.trainingadmin.controller;

import com.trainingadmin.anno.Log;
import com.trainingadmin.pojo.Clazz;
import com.trainingadmin.pojo.ClazzQueryParam;
import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.service.ClassService;
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
 * 班级管理控制器
 *
 * <p>对外提供的接口：</p>
 * <ul>
 *     <li>GET    /clazzs        分页条件查询班级</li>
 *     <li>GET    /clazzs/list   查询全部班级（前端下拉框）</li>
 *     <li>GET    /clazzs/{id}   根据 ID 查询班级详情</li>
 *     <li>POST   /clazzs        新增班级</li>
 *     <li>PUT    /clazzs        修改班级</li>
 *     <li>DELETE /clazzs/{id}   根据 ID 删除班级</li>
 * </ul>
 */
@Slf4j
@RequestMapping("/clazzs")
@RestController
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    /**
     * 分页查询班级列表（支持按班级名称、结课时间范围筛选）
     *
     * @param clazzQueryParam 分页及筛选条件
     * @return 统一响应结果，data 为 {@link PageResult} 分页对象
     */
    @GetMapping
    public Result page(ClazzQueryParam clazzQueryParam) {
        log.info("分页查询班级：{}", clazzQueryParam);
        PageResult<Clazz> pageResult = classService.page(clazzQueryParam);
        return Result.success(pageResult);
    }

    /**
     * 查询所有班级（不分页）
     *
     * <p>用于前端下拉框/选择器，因此直接返回列表。</p>
     */
    @GetMapping("/list")
    public Result getAll() {
        List<Clazz> list = classService.findAll();
        return Result.success(list);
    }

    /**
     * 根据 ID 查询班级详情（修改页面回显）
     *
     * <p>路径变量限定为数字，非数字路径直接返回 404。</p>
     */
    @GetMapping("/{id:\\d+}")
    public Result getById(@PathVariable Integer id) {
        log.info("根据 ID 查询班级信息：{}", id);
        Clazz clazz = classService.getById(id);
        return Result.success(clazz);
    }

    /**
     * 新增班级
     */
    @Log
    @PostMapping
    public Result save(@RequestBody Clazz clazz) {
        log.info("新增班级：{}", clazz.getName());
        classService.save(clazz);
        return Result.success();
    }

    /**
     * 修改班级信息
     *
     * <p>由 Service 层补全更新时间，通过动态 SQL 只更新非空字段。</p>
     */
    @Log
    @PutMapping
    public Result update(@RequestBody Clazz clazz) {
        log.info("修改班级信息：{}", clazz.getId());
        classService.update(clazz);
        return Result.success();
    }

    /**
     * 根据 ID 删除班级
     */
    @Log
    @DeleteMapping("/{id:\\d+}")
    public Result delete(@PathVariable Integer id) {
        log.info("根据 ID 删除班级：{}", id);
        classService.deleteById(id);
        return Result.success();
    }
}
