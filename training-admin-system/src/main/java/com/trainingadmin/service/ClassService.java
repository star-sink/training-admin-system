package com.trainingadmin.service;

import com.trainingadmin.pojo.Clazz;
import com.trainingadmin.pojo.ClazzQueryParam;
import com.trainingadmin.pojo.PageResult;

import java.util.List;

/**
 * 班级业务逻辑接口（Service 层）
 *
 * <p>定义班级管理模块对外提供的业务能力，具体实现见
 * {@link com.trainingadmin.service.impl.ClassServiceImpl}。Controller 层只依赖该接口，
 * 便于替换实现以及在单元测试中使用 Mock。</p>
 */
public interface ClassService {
    /**
     * 分页条件查询班级，并计算每个班级的当前状态
     *
     * <p>实现要点：使用 PageHelper 完成物理分页；联表 emp 表查询班主任姓名；
     * 根据当前日期与开课/结课日期比较，得出“未开班 / 已开班 / 已结课”状态。</p>
     *
     * @return 分页结果，包含总记录数 total 与当前页数据 rows
     */
    PageResult<Clazz> page(ClazzQueryParam clazzQueryParam);

    /**
     * 根据 ID 删除班级
     *
     * @param id 班级 ID
     * @throws com.trainingadmin.exception.BusinessException 班级不存在时抛出
     */
    void deleteById(Integer id);

    /**
     * 新增班级
     *
     * @param clazz 班级信息
     */
    void save(Clazz clazz);

    /**
     * 根据 ID 查询班级详情
     *
     * @param id 班级 ID
     * @return 班级对象；记录不存在时返回 null
     */
    Clazz getById(Integer id);

    /**
     * 修改班级信息
     *
     * <p>由服务端统一维护 updateTime，并按非空字段动态更新。</p>
     *
     * @param clazz 待修改的班级信息，必须包含 id
     * @throws com.trainingadmin.exception.BusinessException 班级不存在时抛出
     */
    void update(Clazz clazz);

    /**
     * 查询所有班级（不分页）
     *
     * <p>用于前端下拉框等场景，不返回分页结构。</p>
     *
     * @return 班级列表
     */
    List<Clazz> findAll();


}
