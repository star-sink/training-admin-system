package com.trainingadmin.mapper;

import com.trainingadmin.pojo.OperateLog;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface LogMapper {
    /**
     * 查询日志列表
     */
    List<OperateLog> list();
}