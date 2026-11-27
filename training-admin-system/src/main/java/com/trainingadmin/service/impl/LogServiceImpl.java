package com.trainingadmin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.trainingadmin.mapper.LogMapper;
import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper; // 注入新写的 Mapper

    @Override
    public PageResult page(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<?> p = (Page<?>) logMapper.list();
        return new PageResult(p.getTotal(), p.getResult());
    }
}