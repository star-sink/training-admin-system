package com.trainingadmin.service;

import com.trainingadmin.pojo.PageResult;

public interface LogService {
    PageResult page(Integer page, Integer pageSize);
}