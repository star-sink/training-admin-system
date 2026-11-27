package com.trainingadmin.service.impl;

import com.trainingadmin.mapper.EmpMapper;
import com.trainingadmin.mapper.StudentMapper;
import com.trainingadmin.pojo.JobOption;
import com.trainingadmin.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 报表统计业务实现类
 *
 * <p>负责调用 Mapper 获取统计数据，并把数据库返回的行列结构
 * 组装成前端图表需要的 {名称列表, 数值列表} 形式。</p>
 */
@Service
public class ReportServiceImpl implements ReportService {

    /**
     * 员工数据访问层
     */
    @Autowired
    private EmpMapper empMapper;

    /**
     * 学员数据访问层（学员学历分布、班级人数统计）
     */
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public JobOption getEmpJobData() {
        //1.调用mapper接口，获取统计数据
        List<Map<String, Object>> list=empMapper.countEmpJobData();//map:pos=教研主管 ，num=1


        //2.组装结果，并返回
        List<Object> jobList =list.stream().map(dataMap ->dataMap.get("pos")).toList();
        List<Object> dataList =list.stream().map(dataMap ->dataMap.get("num")).toList();

        return new  JobOption(jobList,dataList);
    }

    @Override
    public List<Map<String, Object>> getEmpGenderData() {
        return empMapper.countEmpGenderData();
    }

    /**
     * 统计学员学历分布
     *
     * <p>SQL 中已通过 CASE WHEN 把学历字典值转为中文名称，
     * 因此这里直接返回 {name, value} 形式的列表。</p>
     *
     * @return 学历分布列表
     */
    @Override
    public List<Map<String, Object>> getStudentDegreeData() {
        // 直接返回 Mapper 的统计结果
        return studentMapper.getStudentDegreeData();
    }

    /**
     * 统计各班级人数
     *
     * <p>把 SQL 返回的 [{clazzName, num}, ...] 拆分成两个平行的列表，
     * 分别作为图表的 X 轴（班级名称）和数据系列（人数）。</p>
     *
     * @return {clazzList: 班级名称列表, dataList: 对应人数列表}
     */
    @Override
    public Map<String, Object> getStudentCountData() {
        // 1. 获取每个班级的名称与人数
        List<Map<String, Object>> list = studentMapper.getStudentCountData();

        // 2. 分离成图表需要的两个列表
        List<String> clazzList = new ArrayList<>();
        List<Object> dataList = new ArrayList<>();

        for (Map<String, Object> map : list) {
            clazzList.add((String) map.get("clazzName"));
            dataList.add(map.get("num"));
        }

        // 3. 封装返回，键名与前端约定保持一致
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("clazzList", clazzList);
        resultMap.put("dataList", dataList);

        return resultMap;
    }
}
