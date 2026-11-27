package com.trainingadmin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.trainingadmin.exception.BusinessException;
import com.trainingadmin.mapper.ClassMapper;
import com.trainingadmin.pojo.Clazz;
import com.trainingadmin.pojo.ClazzQueryParam;
import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级业务逻辑实现类
 *
 * <p>实现 {@link ClassService} 定义的全部业务方法，负责分页参数设置、班级状态计算、
 * 修改时间维护等业务细节，数据库访问统一委托给 {@link ClassMapper}。</p>
 *
 * <p>说明：{@code @Service} 将该类注册为 Spring Bean，供 Controller 层注入使用。</p>
 */
@Service // 核心：加上这个，Spring 就会认识它了
public class ClassServiceImpl implements ClassService {

    /**
     * 班级数据访问层
     * 由 MyBatis 为 Mapper 接口生成的动态代理对象注入
     */
    @Autowired
    private ClassMapper classMapper;

    /**
     * 分页条件查询班级，并手动计算班级状态
     *
     * <p>实现步骤：</p>
     * <ol>
     *     <li>调用 {@link PageHelper#startPage(int, int)} 设置分页参数，
     *         该设置只对紧随其后的第一条 SQL 生效；</li>
     *     <li>调用 Mapper 执行联表查询，PageHelper 会自动改写 SQL 完成 count 与 limit；</li>
     *     <li>遍历结果集，根据当前日期与开课/结课日期计算班级状态；</li>
     *     <li>从返回的 Page 对象中取出总数与数据，封装为 {@link PageResult}。</li>
     * @return 分页结果（total + rows）
     */
    @Override
    public PageResult<Clazz> page(ClazzQueryParam clazzQueryParam) {
        // 1. 设置分页参数（页码、每页条数）
        PageHelper.startPage(clazzQueryParam.getPage(), clazzQueryParam.getPageSize());

        // 2. 调用 Mapper 查询数据
        List<Clazz> clazzList = classMapper.list(clazzQueryParam);

        // 3. 遍历计算状态
        LocalDate now = LocalDate.now();
        for (Clazz clazz : clazzList) {
            if (now.isBefore(clazz.getBeginDate())) {
                clazz.setStatus("未开班");
            } else if (now.isAfter(clazz.getEndDate())) {
                clazz.setStatus("已结课");
            } else {
                clazz.setStatus("已开班");
            }
        }

        // 4. 将 List 强转为 Page，封装为统一的分页结果
        Page<Clazz> p = (Page<Clazz>) clazzList;
        return new PageResult<>(p.getTotal(), p.getResult());
    }
    /**
     * 查询所有班级（不分页）
     *
     * <p>用于前端下拉框数据源，按修改时间倒序返回。</p>
     *
     * @return 班级列表
     */
    @Override
    public List<Clazz> findAll() {
        // 直接查询全表，不设置分页参数，因此不会触发 PageHelper 分页
        return classMapper.findAll();
    }

    /**
     * 根据 ID 删除班级
     *
     * @param id 班级 ID
     * @throws BusinessException 班级不存在（受影响行数为 0）时抛出
     */
    @Override
    public void deleteById(Integer id) {
        // 执行删除，并接收受影响行数；为 0 说明该 ID 对应的班级不存在
        int rows = classMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException("班级不存在，删除失败");
        }
    }

    /**
     * 新增班级
     *
     * <p>由服务端统一生成创建时间和修改时间，保证审计字段完整，
     * 同时使新增班级在按 update_time 倒序查询时能正常排在最前面。</p>
     *
     * @param clazz 班级信息
     */
    @Override
    public void save(Clazz clazz) {
        // 新增时创建时间与修改时间一致，统一使用服务端当前时间
        LocalDateTime now = LocalDateTime.now();
        clazz.setCreateTime(now);
        clazz.setUpdateTime(now);
        // 插入班级记录（主键由数据库自增生成，并通过 useGeneratedKeys 回填到 clazz.id）
        classMapper.insert(clazz);
    }

    /**
     * 根据 ID 查询班级详情
     *
     * @param id 班级 ID
     * @return 班级对象；记录不存在时返回 null
     */
    @Override
    public Clazz getById(Integer id) {
        // 单表根据主键查询，供修改页面回显数据
        return classMapper.getById(id);
    }


    /**
     * 修改班级信息
     *
     * <p>业务逻辑：</p>
     * <ol>
     *     <li>修改时间由服务端统一生成，避免信任前端传入的时间；</li>
     *     <li>调用 Mapper 动态 SQL，仅更新前端传入的非空字段；</li>
     *     <li>create_time 保持不变，创建时间在新增时确定。</li>
     * </ol>
     *
     * @param clazz 待修改的班级信息，必须包含 id
     * @throws BusinessException 班级不存在（受影响行数为 0）时抛出
     */
    @Override
    public void update(Clazz clazz) {
        // 1. 获取服务端当前时间，并设置到对象中（保证更新时间可信）
        clazz.setUpdateTime(LocalDateTime.now());

        // 2. 调用 Mapper 更新数据库（XML 中使用 set + if 实现动态更新）
        int rows = classMapper.update(clazz);
        // 为 0 说明 WHERE id 未命中任何记录，提示前端而不是静默返回成功
        if (rows == 0) {
            throw new BusinessException("班级不存在，修改失败");
        }
    }
}
