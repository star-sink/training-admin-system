package com.trainingadmin.controller;

import com.trainingadmin.pojo.Emp;
import com.trainingadmin.pojo.LoginInfo;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录Controller
 */
@Slf4j
@RestController
public class LoginController {


    private final EmpService empService;

    public LoginController(EmpService empService) {
        this.empService = empService;
    }

    @PostMapping("/login")
    public Result login(@RequestBody Emp emp) {
        log.info("用户登录：{}", emp.getUsername());
        LoginInfo info=empService.login(emp);
        if(info!=null){
            return Result.success(info);
        }
        return Result.error("用户名或密码错误");

    }
}
