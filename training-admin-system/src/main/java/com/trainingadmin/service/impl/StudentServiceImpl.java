package com.trainingadmin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.trainingadmin.mapper.StudentMapper;
import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.pojo.Student;
import com.trainingadmin.pojo.StudentQueryParam;
import com.trainingadmin.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学员业务逻辑实现类
 *
 * <p>实现 {@link StudentService} 定义的全部业务方法，负责分页参数设置与结果封装，
 * 数据库访问统一委托给 {@link StudentMapper}。</p>
 *
 * <p>说明：{@code @Service} 将该类注册为 Spring Bean，供 Controller 层注入使用。</p>
 */
@Service // 交给 Spring 容器管理
public class StudentServiceImpl implements StudentService {

    /**
     * 学员数据访问层
     * 由 MyBatis 为 Mapper 接口生成的动态代理对象注入
     */
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 分页条件查询学员
     *
     * <p>实现步骤：</p>
     * <ol>
     *     <li>调用 {@link PageHelper#startPage(int, int)} 设置分页参数，
     *         该设置只对紧随其后的第一条 SQL 生效；</li>
     *     <li>调用 Mapper 执行联表查询，PageHelper 会自动改写 SQL 完成 count 与 limit；</li>
     *     <li>将返回的 List 强转为 Page，取出总记录数与当前页数据；</li>
     *     <li>封装为 {@link PageResult} 返回。</li>
     * </ol>
     *
     * @param studentQueryParam 分页及筛选条件
     * @return 分页结果（total + rows）
     */
    @Override
    public PageResult page(StudentQueryParam studentQueryParam) {
        // 1. 设置分页参数 (当前页码, 每页记录数)
        PageHelper.startPage(studentQueryParam.getPage(), studentQueryParam.getPageSize());

        // 2. 调用 Mapper 查询数据（返回的其实是 PageHelper 的 Page 对象）
        List<Student> list = studentMapper.list(studentQueryParam);

        // 3. 强转为 Page 对象，获取总记录数
        Page<Student> p = (Page<Student>) list;

        // 4. 封装返回结果：total 为总记录数，rows 为当前页数据
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 根据 ID 删除学员
     *
     * @param id 学员 ID
     */
    @Override
    public void deleteById(Integer id) {
        // 执行删除；必要时可根据返回的受影响行数判断学员是否存在
        studentMapper.deleteById(id);
    }

    /**
     * 新增学员
     *
     * <p>创建时间与修改时间由 XML 中的 NOW() 生成，
     * 主键由数据库自增并通过 useGeneratedKeys 回填到 student.id。</p>
     *
     * @param student 学员信息
     */
    @Override
    public void save(Student student) {
        // 插入学员记录
        studentMapper.insert(student);
    }

    /**
     * 根据 ID 查询学员详情
     *
     * @param id 学员 ID
     * @return 学员对象；记录不存在时返回 null
     */
    @Override
    public Student getById(Integer id) {
        // 供修改页面回显数据
        return studentMapper.getById(id);
    }

    /**
     * 修改学员信息
     *
     * <p>XML 中使用 {@code <set> + <if>} 实现动态更新，只更新前端传入的非空字段，
     * update_time 由 SQL 统一设置为当前时间。</p>
     *
     * @param student 待修改的学员信息，必须包含 id
     */
    @Override
    public void update(Student student) {
        // 委托 Mapper 执行动态更新
        studentMapper.update(student);
    }

    /**
     * 学员违纪处理：违纪次数 +1，违纪扣分累加
     *
     * <p>累加在 SQL 中完成，避免“先查询再写回”带来的并发覆盖问题。</p>
     *
     * @param id    学员 ID
     * @param score 本次扣分
     */
    @Override
    public void updateViolation(Integer id, Integer score) {
        // 由 SQL 完成次数 +1、分数累加与更新时间刷新
        studentMapper.updateViolation(id, score);
    }

}
