package com.trainingadmin.controller;


import com.trainingadmin.pojo.JobOption;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.service.ReportService;
import com.trainingadmin.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 报表统计控制器
 *
 * <p>提供员工、学员相关的图表统计数据，前端用 ECharts 等图表库展示。
 * 其中学员统计接口（学历分布、班级人数）的数据来自学员管理模块。</p>
 */
@Slf4j
@RequestMapping("/report")
@RestController
public class ReportController {

    /**
     * 报表业务接口，由 Spring 自动注入
     */
    @Autowired
    private ReportService reportService;
    /**
     * 统计员工职位人数
     */
    @GetMapping("/empJobData")
    public Result getEmpJobData(){
        log.info("统计员工职位人数");
        JobOption jobOption=reportService.getEmpJobData();
        return Result.success(jobOption);
    }

    /**
     * 统计员工性别人数
     * @return
     */
    @GetMapping("/empGenderData")
    public Result getEmpGenderData(){
        log.info("统计员工性别人数");
        List<Map<String,Object>> genderList=reportService.getEmpGenderData();
        return Result.success(genderList);
    }

    /**
     * 统计学员学历分布
     *
     * <p>接口：GET /report/studentDegreeData</p>
     * <p>返回列表中每个元素包含 name（学历名称）与 value（人数），
     * 用于前端饼图/柱状图展示。</p>
     *
     * @return 统一响应结果，data 为学历分布列表
     */
    @GetMapping("/studentDegreeData")
    public Result getStudentDegreeData() {
        log.info("统计学员学历信息");
        // 调用 Service 获取学历分布统计数据
        List<Map<String, Object>> data = reportService.getStudentDegreeData();
        return Result.success(data);
    }

    /**
     * 统计各班级人数
     *
     * <p>接口：GET /report/studentCountData</p>
     * <p>返回结构为 {clazzList: [班级名称], dataList: [对应人数]}，
     * 便于前端直接作为图表 X 轴与数据系列使用。</p>
     *
     * @return 统一响应结果，data 为班级名称与人数两个列表
     */
    @GetMapping("/studentCountData")
    public Result getStudentCountData() {
        log.info("统计班级人数");
        // 调用 Service 获取班级人数统计数据
        Map<String, Object> data = reportService.getStudentCountData();
        return Result.success(data);
    }
}
