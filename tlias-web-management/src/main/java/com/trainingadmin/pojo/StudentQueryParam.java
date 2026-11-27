package com.trainingadmin.pojo;

import lombok.Data;

/**
 * 学员分页查询参数
 *
 * <p>用于接收 GET /students 的查询串参数。Spring MVC 会按属性名
 * 自动完成参数绑定；未传 page、pageSize 时使用默认值。</p>
 */
@Data
public class StudentQueryParam {
    private String name;     // 姓名（模糊查询）
    private Integer degree;  // 学历（精确匹配字典值）
    private Integer clazzId; // 班级ID（精确匹配）
    private Integer page = 1;       // 页码，默认 1
    private Integer pageSize = 10;  // 每页条数，默认 10
}
