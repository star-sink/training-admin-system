package com.trainingadmin.controller;


import com.trainingadmin.anno.Log;
import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.pojo.Student;
import com.trainingadmin.pojo.StudentQueryParam;
import com.trainingadmin.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学员管理控制器
 *
 * <p>职责：接收前端学员管理模块的 HTTP 请求，完成参数绑定与日志记录，
 * 将业务处理委托给 {@link StudentService}，并统一封装为 {@link Result} 返回。</p>
 *
 * <p>对外提供的接口：</p>
 * <ul>
 *     <li>GET    /students                        分页条件查询学员</li>
 *     <li>POST   /students                        新增学员</li>
 *     <li>GET    /students/{id}                   根据 ID 查询学员详情</li>
 *     <li>PUT    /students                        修改学员信息</li>
 *     <li>DELETE /students/{id}                   根据 ID 删除学员</li>
 *     <li>PUT    /students/violation/{id}/{score} 学员违纪处理（记录次数并扣分）</li>
 * </ul>
 *
 * <p>说明：{@code @RestController} 等价于 {@code @Controller + @ResponseBody}，
 * 方法返回值会由 Jackson 序列化为 JSON；{@code @RequestMapping("/students")}
 * 为类中所有方法抽取统一的请求路径前缀。</p>
 */
@Slf4j // 日志注解，Lombok 自动生成 log 对象
@RequestMapping("/students") // 设置公共请求路径
@RestController // 返回 JSON 格式 + 接收前端请求
public class StudentController {

    /**
     * 学员业务接口（Service 层）
     * 由 Spring 容器自动注入，无需手动 new，便于解耦和替换实现
     */
    @Autowired
    private StudentService studentService;

    /**
     * 分页查询学员列表（支持按姓名、学历、班级筛选）
     *
     * <p>接口：GET /students</p>
     * <p>请求参数通过 URL 查询串绑定到 {@link StudentQueryParam}：
     * page（页码，默认 1）、pageSize（每页条数，默认 10）、
     * name（姓名，模糊匹配）、degree（学历）、clazzId（班级 ID）。</p>
     *
     * <p>返回数据为分页结构：total 表示总记录数，rows 表示当前页数据。</p>
     *
     * @param studentQueryParam 分页及筛选条件对象
     * @return 统一响应结果，data 为 {@link PageResult} 分页对象
     */
    @GetMapping
    public Result page(StudentQueryParam studentQueryParam) {
        log.info("分页查询学员: {}", studentQueryParam);
        // 调用 Service 层分页查询，内部由 PageHelper 完成物理分页
        PageResult pageResult = studentService.page(studentQueryParam);
        // 统一返回 code=1、msg=success，并将分页数据放入 data
        return Result.success(pageResult);
    }

    /**
     * 根据 ID 删除学员
     *
     * <p>接口：DELETE /students/{id}</p>
     * <p>路径变量使用 {@code {id:\\d+}} 限定只能为数字，
     * 避免非数字参数引发类型转换异常。</p>
     *
     * @param id 学员 ID（路径变量）
     * @return 统一响应结果，data 为 null
     */
    @Log
    @DeleteMapping("/{id:\\d+}")
    public Result delete(@PathVariable Integer id) {
        log.info("根据id删除学员:{}", id);
        // 委托 Service 层执行删除
        studentService.deleteById(id);
        return Result.success();
    }

    /**
     * 新增学员
     *
     * <p>接口：POST /students</p>
     * <p>请求体为 application/json，由 {@code @RequestBody} 反序列化为 {@link Student} 对象。</p>
     *
     * @param student 学员信息（姓名、学号、性别、手机号、学历、班级等）
     * @return 统一响应结果，data 为 null
     */
    @Log
    @PostMapping
    public Result save(@RequestBody Student student) {
        log.info("新增学员: {}", student);
        // 委托 Service 层完成新增
        studentService.save(student);
        return Result.success();
    }

    /**
     * 根据 ID 查询学员信息
     *
     * <p>接口：GET /students/{id}</p>
     * <p>主要用于修改学员时回显表单数据。</p>
     *
     * @param id 学员 ID（路径变量）
     * @return 统一响应结果，data 为 {@link Student} 对象
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("根据ID查询学员信息: {}", id);
        // 调用 Service 查询学员详情
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    /**
     * 修改学员信息
     *
     * <p>接口：PUT /students</p>
     * <p>请求体为 application/json，id 必填；Service 层通过动态 SQL 只更新非空字段。</p>
     *
     * @param student 待修改的学员信息
     * @return 统一响应结果，data 为 null
     */
    @Log
    @PutMapping
    public Result update(@RequestBody Student student) {
        log.info("修改学员: {}", student);
        // 委托 Service 层完成修改
        studentService.update(student);
        return Result.success();
    }

    /**
     * 学员违纪处理
     *
     * <p>接口：PUT /students/violation/{id}/{score}</p>
     * <p>业务规则：违纪次数 +1，违纪扣分累加 score，由 SQL 中的
     * {@code violation_count = violation_count + 1} 保证原子性。</p>
     *
     * @param id    学员 ID（路径变量）
     * @param score 本次扣分（路径变量）
     * @return 统一响应结果，data 为 null
     */
    @Log
    @PutMapping("/violation/{id}/{score}")
    public Result updateViolation(
            @PathVariable("id") Integer id,
            @PathVariable("score") Integer score) {
        log.info("违纪处理, 学员id:{}, 扣分:{}", id, score);
        // 委托 Service 层执行违纪处理
        studentService.updateViolation(id, score);
        return Result.success();
    }


}
