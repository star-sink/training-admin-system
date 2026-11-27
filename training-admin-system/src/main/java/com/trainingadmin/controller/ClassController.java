package com.trainingadmin.controller;

import com.trainingadmin.anno.Log;
import com.trainingadmin.pojo.Clazz;
import com.trainingadmin.pojo.EmpQueryParam;
import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.service.ClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理控制器
 *
 * <p>职责：接收班级管理模块的前端 HTTP 请求，完成参数绑定与日志记录，
 * 将业务处理委托给 {@link ClassService}，并统一封装为 {@link Result} 返回。</p>
 *
 * <p>本控制器对外提供的接口：</p>
 * <ul>
 *     <li>GET    /clazzs        分页条件查询班级</li>
 *     <li>GET    /clazzs/list   查询全部班级（用于前端下拉框，不分页）</li>
 *     <li>GET    /clazzs/{id}   根据 ID 查询班级详情</li>
 *     <li>POST   /clazzs        新增班级</li>
 *     <li>PUT    /clazzs        修改班级</li>
 *     <li>DELETE /clazzs/{id}   根据 ID 删除班级</li>
 * </ul>
 *
 * <p>说明：{@code @RestController} 等价于 {@code @Controller + @ResponseBody}，
 * 方法返回值会由 Jackson 序列化为 JSON；{@code @RequestMapping("/clazzs")}
 * 为类中所有方法抽取统一的请求路径前缀。</p>
 */
@Slf4j//打印日志
@RequestMapping("/clazzs")//设置公共路径
@RestController//返回JSON格式+接受前端请求
public class ClassController {
    /**
     * 班级业务接口（Service 层）
     * 由 Spring 容器自动注入，无需手动 new，便于解耦和单元测试时替换实现
     */
    //自动注入，创建classService，不用new
    @Autowired
    private ClassService classService;

    /**
     * 分页查询班级列表（支持按班级名称、结课时间范围筛选）
     *
     * <p>接口：GET /clazzs</p>
     * <p>请求参数通过 URL 查询串绑定到 {@link ClazzQueryParam}：
     * page（页码，默认 1）、pageSize（每页条数，默认 10）、name（班级名称）、
     * begin / end（结课时间范围）。</p>
     *
     * <p>返回数据为分页结构：total 表示总记录数，rows 表示当前页数据；
     * 每条班级数据中的 status（班级状态）由 Service 层根据日期实时计算。</p>
     *
     * @param clazzQueryParam 分页及筛选条件对象
     * @return 统一响应结果，data 为 {@link PageResult} 分页对象
     */
    // 3.1 分页查询
    @GetMapping
    public Result page(EmpQueryParam empQueryParam) {
        log.info("分页查询参数: {}", empQueryParam);
        // 调用 Service 层分页查询，内部由 PageHelper 完成物理分页
        PageResult pageResult = classService.page(empQueryParam);
        // 统一返回 code=1、msg=success，并将分页数据放入 data
        return Result.success(pageResult);
    }


    /**
     * 根据 ID 删除班级
     *
     * <p>接口：DELETE /clazzs/{id}</p>
     * <p>路径变量使用 {@code {id:\\d+}} 限定只能为数字，避免 /clazzs/list
     * 这类具体路径被误当作 ID 处理，同时拦截非数字参数引发的类型转换异常。</p>
     *
     * @param id 班级 ID（路径变量）
     * @return 统一响应结果，data 为 null
     */
    @Log
    @DeleteMapping("/{id:\\d+}")
    public Result delete(@PathVariable Integer id){
        log.info("根据id删除班级:{}", id);
        // 委托 Service 层执行删除
        classService.deleteById(id);
        return Result.success();
    }

    /**
     * 新增班级
     *
     * <p>接口：POST /clazzs</p>
     * <p>请求体为 application/json，由 {@code @RequestBody} 反序列化为 {@link Clazz} 对象。</p>
     *
     * @param clazz 班级信息（名称、教室、开课/结课时间、班主任 ID、学科）
     * @return 统一响应结果，data 为 null
     */
    @Log
    @PostMapping
    public Result save(@RequestBody Clazz clazz){
        log.info("新增班级");
        // 委托 Service 层完成新增
        classService.save(clazz);
        return Result.success();
    }

    /**
     * 根据 ID 查询班级详情
     *
     * <p>接口：GET /clazzs/{id}</p>
     * <p>主要用于修改班级时回显表单数据。路径变量限定为数字，
     * 非数字路径会直接返回 404，而不是进入方法后抛出类型转换异常。</p>
     *
     * @param id 班级 ID（路径变量）
     * @return 统一响应结果，data 为 {@link Clazz} 对象
     */
    @GetMapping("/{id:\\d+}")
    public Result getById(@PathVariable Integer id) {
        log.info("根据id查询班级信息: {}", id);
        // 调用 Service 查询，返回 Clazz 对象
        Clazz clazz = classService.getById(id);
        // 统一包装成 Result 返回
        return Result.success(clazz);
    }

    /**
     * 修改班级信息
     *
     * <p>接口：PUT /clazzs</p>
     * <p>请求体：application/json，包含 id、name、room、beginDate、endDate、masterId、subject。</p>
     * <p>业务说明：由 Service 层补全 updateTime 后，通过动态 SQL 只更新非空字段。</p>
     *
     * @param clazz 待修改的班级信息，id 必填
     * @return 统一响应结果：{"code":1,"msg":"success","data":null}
     */
    // 3.5 修改班级
    @Log
    @PutMapping
    public Result update(@RequestBody Clazz clazz) {
        log.info("修改班级信息: {}", clazz);
        // 调用 Service 层的修改方法（内部会统一设置 updateTime）
        classService.update(clazz);
        // 修改成功无需返回业务数据，统一返回成功标识
        return Result.success();
    }

    /**
     * 查询所有班级（不分页）
     *
     * <p>接口：GET /clazzs/list</p>
     * <p>主要用于前端下拉框/选择器，因此直接返回列表，不封装分页结构。</p>
     *
     * @return 统一响应结果，data 为 {@link Clazz} 列表
     */
    @GetMapping("/list") // 注意有 /list
    public Result getAll() {
        // 调用 Service 查询全部班级
        List<Clazz> list = classService.findAll();
        return Result.success(list);
    }
}
