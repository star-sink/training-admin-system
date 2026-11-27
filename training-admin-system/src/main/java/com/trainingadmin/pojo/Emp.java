package com.trainingadmin.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Emp {
    private Integer id; //ID,主键
    private String username; //用户名
    /** 密码：只接收前端传入，接口响应与操作日志中不会序列化输出 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String name; //姓名
    private Integer gender; //性别, 1:男, 2:女
    private String phone; //手机号
    private Integer job; //职位, 1:班主任,2:讲师,3:学工主管,4:教研主管,5:咨询师
    private Integer salary; //薪资
    private String image; //头像
    private LocalDate entryDate; //入职日期
    private Integer deptId; //关联的部门ID
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //修改时间




    //封装部门名称
    private String deptName;

    //封装工作经历信息
    private List<EmpExpr> exprList;
}
