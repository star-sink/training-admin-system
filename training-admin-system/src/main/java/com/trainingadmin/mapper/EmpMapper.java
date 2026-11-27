package com.trainingadmin.mapper;

import com.trainingadmin.pojo.Emp;
import com.trainingadmin.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 员工信息
 */
@Mapper
public interface EmpMapper {
    //-------------------------------原始查询分页-----------------------------------
    /**
     *总记录数
     * -- 总记录数
     */
//    @Select(" select  count(*) from emp e left join dept d on e.dept_id = d.id")
//    public Long count();


    /**
     * 分页查询
     * -- 起始索引 = （页码-1） 每页展示记录数
     */
//    @Select("select e.* , d.name deptName from emp e left join dept d on e.dept_id = d.id " +
//            "order by e.update_time limit #{start},#{pageSize}")
//    public List<Emp> list(Integer start,Integer pageSize);


        //@Select("select e.* , d.name deptName from emp e left join dept d on e.dept_id = d.id order by e.update_time desc ")

//        public List<Emp> list(String name, Integer gender, LocalDate begin, LocalDate end);

    /**
     * 条件查询员工信息
     */
    public List<Emp> list(EmpQueryParam empQueryParam);

    /**
     * 查询全部员工信息（班级管理-班主任/助教下拉框）
     */
    List<Emp> listAll();


    /**
     *新增员工基本信息
     */
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into emp(username, password, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time)" +
            "values (#{username},#{password},#{name},#{gender},#{phone},#{job},#{salary},#{image},#{entryDate},#{deptId},#{createTime},#{updateTime})")
    void insert(Emp emp);


    /**
     * 根据id批量删除员工的基本信息
     * @param ids
     */
    void deleteByIds(List<Integer> ids);




    /**
     * 根据id修改员工基本信息以及员工工作经历
     */
    Emp getById(Integer id);


    /**
     * 根据ID更新员工基本信息
     * @param emp
     */
    void updateById(Emp emp);


    /**
     * 职位统计-实现
     */
    @MapKey("pos")
    List<Map<String,Object>> countEmpJobData();

    @MapKey("name")
    List<Map<String, Object>> countEmpGenderData();


    /**
     * 根据用户名查询员工（包含密码哈希，用于登录校验）
     */
    Emp selectByUsername(String username);

    /**
     * 更新密码哈希（用于历史明文密码的自动升级）
     */
    @Update("update emp set password = #{password} where id = #{id}")
    void updatePassword(@Param("id") Integer id, @Param("password") String password);
}
