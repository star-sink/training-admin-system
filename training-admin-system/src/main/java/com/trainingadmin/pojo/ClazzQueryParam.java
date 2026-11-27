package com.trainingadmin.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 班级分页查询参数
 *
 * <p>用于接收 GET /clazzs 的查询串参数。Spring MVC 会按属性名自动绑定，
 * 未传 page、pageSize 时使用默认值。</p>
 */
@Data
public class ClazzQueryParam {
    private Integer page = 1;       // 页码，默认 1
    private Integer pageSize = 10;  // 每页条数，默认 10
    private String name;            // 班级名称（模糊查询）

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;        // 结课时间-开始

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;          // 结课时间-结束
}
