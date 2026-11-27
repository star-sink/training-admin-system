package com.trainingadmin.service.impl;

import com.trainingadmin.mapper.EmpLogMapper;
import com.trainingadmin.pojo.EmpLog;
import com.trainingadmin.service.EmpLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpLogServiceImpl implements EmpLogService {

    @Autowired
    private EmpLogMapper empLogMapper;

    /**
     * 使用独立事务写入日志：主业务回滚时日志仍然保留
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void insertLog(EmpLog empLog) {
        empLogMapper.insert(empLog);
    }
}
