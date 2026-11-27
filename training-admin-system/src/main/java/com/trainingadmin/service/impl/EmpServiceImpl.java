package com.trainingadmin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.trainingadmin.mapper.EmpExprMapper;
import com.trainingadmin.mapper.EmpMapper;
import com.trainingadmin.pojo.Emp;
import com.trainingadmin.pojo.EmpExpr;
import com.trainingadmin.pojo.EmpLog;
import com.trainingadmin.pojo.EmpQueryParam;
import com.trainingadmin.pojo.LoginInfo;
import com.trainingadmin.pojo.PageResult;
import com.trainingadmin.service.EmpLogService;
import com.trainingadmin.service.EmpService;
import com.trainingadmin.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class EmpServiceImpl implements EmpService {

    /** 新增员工未指定密码时使用的初始密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    private final EmpMapper empMapper;
    private final EmpExprMapper empExprMapper;
    private final EmpLogService empLogService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public EmpServiceImpl(EmpMapper empMapper,
                          EmpExprMapper empExprMapper,
                          EmpLogService empLogService,
                          JwtUtils jwtUtils,
                          PasswordEncoder passwordEncoder) {
        this.empMapper = empMapper;
        this.empExprMapper = empExprMapper;
        this.empLogService = empLogService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * PageHelper 分页查询
     *
     * <p>注意：PageHelper 只对紧随其后的第一条查询语句生效。</p>
     */
    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam) {
        // 1. 设置分页参数
        PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());
        // 2. 执行查询（返回的实际上是 PageHelper 的 Page 对象）
        List<Emp> empList = empMapper.list(empQueryParam);
        // 3. 解析分页结果并封装
        Page<Emp> p = (Page<Emp>) empList;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Emp emp) {
        try {
            // 1. 保存员工基础信息，密码统一使用 BCrypt 加密
            emp.setPassword(encodePassword(emp.getPassword(), DEFAULT_PASSWORD));
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());
            empMapper.insert(emp);

            // 2. 保存员工工作经历信息
            List<EmpExpr> exprList = emp.getExprList();
            if (!CollectionUtils.isEmpty(exprList)) {
                exprList.forEach(empExpr -> empExpr.setEmpId(emp.getId()));
                empExprMapper.insertBatch(exprList);
            }
        } finally {
            // 记录新增日志：日志独立事务，不能因为主业务回滚而丢失，也不能影响主业务异常
            try {
                empLogService.insertLog(new EmpLog(null, LocalDateTime.now(), "新增员工：" + emp.getName()));
            } catch (Exception e) {
                log.error("记录员工操作日志失败", e);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        // 1. 删除员工基本信息
        empMapper.deleteByIds(ids);
        // 2. 批量删除员工的工作经历信息
        empExprMapper.deleteByEmpIds(ids);
    }

    @Override
    public List<Emp> listAll() {
        return empMapper.listAll();
    }

    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Emp emp) {
        // 1. 修改员工基本信息（只有前端传了密码才重新加密）
        if (StringUtils.hasText(emp.getPassword())) {
            emp.setPassword(passwordEncoder.encode(emp.getPassword()));
        }
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        // 2. 先删除原有工作经历，再插入新的工作经历
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));
        List<EmpExpr> exprList = emp.getExprList();
        if (!CollectionUtils.isEmpty(exprList)) {
            exprList.forEach(empExpr -> empExpr.setEmpId(emp.getId()));
            empExprMapper.insertBatch(exprList);
        }
    }

    @Override
    public LoginInfo login(Emp emp) {
        // 1. 根据用户名查询员工
        Emp e = empMapper.selectByUsername(emp.getUsername());
        // 2. 校验密码
        if (e == null || !passwordMatches(emp.getPassword(), e)) {
            return null;
        }

        log.info("登录成功，员工ID：{}，用户名：{}", e.getId(), e.getUsername());
        // 3. 生成 JWT 令牌
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", e.getId());
        claims.put("username", e.getUsername());
        String jwt = jwtUtils.generateToken(claims);
        return new LoginInfo(e.getId(), e.getUsername(), e.getName(), jwt);
    }

    /**
     * 将明文密码加密；未提供密码时使用默认初始密码。
     */
    private String encodePassword(String rawPassword, String defaultPassword) {
        String password = StringUtils.hasText(rawPassword) ? rawPassword : defaultPassword;
        return passwordEncoder.encode(password);
    }

    /**
     * 校验密码。
     *
     * <p>数据库中保存的是 BCrypt 哈希；同时兼容历史明文数据，
     * 首次登录成功后会立即把明文密码升级为 BCrypt 哈希。</p>
     */
    private boolean passwordMatches(String rawPassword, Emp emp) {
        if (rawPassword == null || emp.getPassword() == null) {
            return false;
        }
        String stored = emp.getPassword();
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, stored);
        }
        if (stored.equals(rawPassword)) {
            empMapper.updatePassword(emp.getId(), passwordEncoder.encode(rawPassword));
            return true;
        }
        return false;
    }
}
