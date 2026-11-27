package com.trainingadmin.mapper;

import com.trainingadmin.pojo.Student;
import com.trainingadmin.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 学员数据访问接口
 *
 * <p>由 MyBatis 生成动态代理实现，SQL 语句定义在
 * {@code resources/com/trainingadmin/mapper/StudentMapper.xml} 中。
 * 方法名必须与 XML 中 {@code <select>/<insert>/<update>/<delete>} 的 id 一致。</p>
 */
@Mapper
public interface StudentMapper {

    /**
     * 按条件查询学员列表（支持姓名模糊、学历、班级筛选）
     * 配合 Service 层的 PageHelper 实现物理分页
     *
     * @param studentQueryParam 查询条件
     * @return 学员列表（分页时实际为 Page 对象）
     */
    List<Student> list(StudentQueryParam studentQueryParam);

    /**
     * 根据 ID 删除学员
     *
     * @param id 学员 ID
     * @return 受影响行数
     */
    int deleteById(Integer id);

    /**
     * 新增学员
     *
     * @param student 学员信息，插入后自增主键回填到 id
     */
    void insert(Student student);

    /**
     * 根据 ID 查询学员详情
     *
     * @param id 学员 ID
     * @return 学员对象
     */
    Student getById(Integer id);

    /**
     * 修改学员信息（动态 SQL，只更新非空字段）
     *
     * @param student 待修改的学员信息，必须包含 id
     */
    void update(Student student);

    /**
     * 学员违纪处理：违纪次数 +1，违纪扣分累加
     *
     * @param id    学员 ID
     * @param score 本次扣分
     */
    void updateViolation(Integer id, Integer score);


    /**
     * 统计学员学历分布（学历名称 + 人数），用于报表图表
     *
     * @return 每个元素包含 name（学历名称）与 value（人数）
     */
    List<Map<String, Object>> getStudentDegreeData();

    /**
     * 统计各班级人数，用于报表图表
     *
     * @return 每个元素包含 clazzName（班级名称）与 num（人数）
     */
    List<Map<String, Object>> getStudentCountData();
}
