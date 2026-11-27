package com.trainingadmin.service;

import com.trainingadmin.pojo.OperateLog;
import com.trainingadmin.pojo.PageResult;

public interface LogService {

    /**
     * 分页查询操作日志
     */
    PageResult<OperateLog> page(Integer page, Integer pageSize);
}
