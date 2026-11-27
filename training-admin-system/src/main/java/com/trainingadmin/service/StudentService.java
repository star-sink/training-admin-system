package com.trainingadmin.service;

import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.pojo.Student;
import com.trainingadmin.pojo.StudentQueryParam;

/**
 * 学员业务接口
 *
 * <p>定义学员管理模块的业务能力，由 {@code StudentServiceImpl} 提供实现，
 * 由 Controller 层调用，屏蔽具体的数据库访问细节。</p>
 */
public interface StudentService {

    /**
     * 分页条件查询学员
     *
     * @param studentQueryParam 分页及筛选条件（姓名、学历、班级、页码、每页条数）
     * @return 分页结果，包含总记录数 total 与当前页数据 rows
     */
    PageResult<Student> page(StudentQueryParam studentQueryParam);

    /**
     * 根据 ID 删除学员
     *
     * @param id 学员 ID
     */
    void deleteById(Integer id);

    /**
     * 新增学员
     *
     * @param student 学员信息
     */
    void save(Student student);

    /**
     * 根据 ID 查询学员详情
     *
     * @param id 学员 ID
     * @return 学员对象；记录不存在时返回 null
     */
    Student getById(Integer id);

    /**
     * 修改学员信息（仅更新非空字段）
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


}
