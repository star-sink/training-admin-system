package com.trainingadmin.controller;

import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/page")
    public Result page(Integer page, Integer pageSize) {
        log.info("分页查询日志: page={}, pageSize={}", page, pageSize);
        PageResult pageResult = logService.page(page, pageSize);
        return Result.success(pageResult);
    }
}