package com.trainingadmin.service;


import com.trainingadmin.pojo.JobOption;

import java.util.List;
import java.util.Map;

public interface ReportService {
    /**
     * 统计员工职位人数
     * @return
     */

    JobOption getEmpJobData();

    /**
     * 统计员工性别人数
     * @return
     */
    List<Map<String,Object>> getEmpGenderData();

    /**
     * 统计学员学历分布
     *
     * @return 学历分布列表，每个元素包含 name（学历名称）与 value（人数）
     */
    List<Map<String, Object>> getStudentDegreeData();

    /**
     * 统计各班级人数
     *
     * @return {clazzList: 班级名称列表, dataList: 对应人数列表}
     */
    Map<String, Object> getStudentCountData();
}
