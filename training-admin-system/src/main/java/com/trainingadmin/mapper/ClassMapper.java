package com.trainingadmin.mapper;

import com.trainingadmin.pojo.Clazz;
import com.trainingadmin.pojo.Emp;
import com.trainingadmin.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 班级数据访问接口（Mapper 层）
 *
 * <p>由 MyBatis 通过动态代理生成实现类，具体 SQL 定义在
 * {@code resources/com/trainingadmin/mapper/ClassMapper.xml} 中。
 * 接口方法名必须与 XML 中对应标签的 id 保持一致。</p>
 *
 * <p>{@code @Mapper} 用于让 MyBatis 扫描并注册该接口的代理对象。</p>
 */
@Mapper
public interface ClassMapper {
    /**
     * 分页条件查询班级列表（联表查询班主任姓名）
     *
     * <p>分页由 Service 层的 PageHelper 控制，本方法只负责条件查询；
     * SQL 中通过 LEFT JOIN emp 表获取班主任姓名 masterName，
     * 支持按班级名称模糊匹配、按结课时间区间筛选。</p>
     *
     * @return 班级列表（含 masterName）
     */
    // 3.1 分页查询
    List<Clazz> list(EmpQueryParam empQueryParam);

    /**
     * 查询所有班级（不分页），按修改时间倒序
     *
     * @return 班级列表
     */
    List<Clazz> findAll();

    /**
     * 根据 ID 删除班级
     *
     * @param id 班级 ID
     * @return 受影响行数；0 表示该 ID 对应的班级不存在
     */
    int deleteById(Integer id);

    /**
     * 新增班级
     *
     * <p>插入后会把数据库自增主键回填到入参对象的 id 属性。</p>
     *
     * @param clazz 班级信息
     */
    void insert(Clazz clazz);

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
     * <p>XML 中使用动态 SQL，只更新入参中非空的字段，并强制刷新 update_time。</p>
     *
     * @param clazz 待修改的班级信息，必须包含 id 与 updateTime
     * @return 受影响行数；0 表示该 ID 对应的班级不存在
     */
    int update(Clazz clazz);
}
