CREATE DATABASE IF NOT EXISTS training DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE training;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `clazz`;
CREATE TABLE `clazz` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
  `name` varchar(30) NOT NULL COMMENT '班级名称',
  `room` varchar(20) DEFAULT NULL COMMENT '班级教室',
  `begin_date` date NOT NULL COMMENT '开课时间',
  `end_date` date NOT NULL COMMENT '结课时间',
  `master_id` int unsigned DEFAULT NULL COMMENT '班主任ID, 关联员工表ID',
  `subject` tinyint unsigned NOT NULL COMMENT '学科, 1:Java, 2:前端, 3:大数据, 4:Python, 5:Go, 6:嵌入式',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级表';

INSERT INTO `clazz` (`id`, `name`, `room`, `begin_date`, `end_date`, `master_id`, `subject`, `create_time`, `update_time`) VALUES
(1,'JavaEE就业163期','212','2023-04-30','2023-06-29',1,1,'2023-06-01 17:08:23','2023-06-01 17:39:58'),
(2,'前端就业90期','210','2023-07-10','2024-01-20',2,2,'2023-06-01 17:45:12','2023-06-01 17:45:12'),
(3,'大数据就业58期','209','2023-08-01','2024-02-15',3,3,'2023-06-01 17:51:21','2023-06-01 17:51:21');

DROP TABLE IF EXISTS `dept`;
CREATE TABLE `dept` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
  `name` varchar(10) NOT NULL COMMENT '部门名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

INSERT INTO `dept` (`id`, `name`, `create_time`, `update_time`) VALUES
(1,'教研部','2024-09-25 09:47:40','2024-09-25 09:47:40'),
(2,'咨询部','2024-09-25 09:47:40','2024-09-30 21:26:24'),
(3,'就业部','2024-09-25 09:47:40','2024-09-25 09:47:40');

DROP TABLE IF EXISTS `emp`;
CREATE TABLE `emp` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(60) DEFAULT '$2a$10$VANKPqW/j.tshIwQHomOwOlSG2o812gbG44IewyKNSF0bObI3bzZW' COMMENT '密码（BCrypt 哈希，初始密码 123456）',
  `name` varchar(10) NOT NULL COMMENT '姓名',
  `gender` tinyint unsigned NOT NULL COMMENT '性别, 1:男, 2:女',
  `phone` char(11) NOT NULL COMMENT '手机号',
  `job` tinyint unsigned DEFAULT NULL COMMENT '职位, 1:班主任, 2:讲师, 3:学工主管, 4:教研主管, 5:咨询师',
  `salary` int unsigned DEFAULT NULL COMMENT '薪资',
  `image` varchar(255) DEFAULT NULL COMMENT '头像',
  `entry_date` date DEFAULT NULL COMMENT '入职日期',
  `dept_id` int unsigned DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工表';

INSERT INTO `emp` (`id`, `username`, `password`, `name`, `gender`, `phone`, `job`, `salary`, `image`, `entry_date`, `dept_id`, `create_time`, `update_time`) VALUES
(1,'zhangsan','$2a$10$VANKPqW/j.tshIwQHomOwOlSG2o812gbG44IewyKNSF0bObI3bzZW','张三',1,'13309090001',1,8000,'https://example.com/avatar/1.jpg','2020-01-01',1,'2023-10-20 16:35:33','2023-11-16 16:11:26'),
(2,'lisi','$2a$10$Twa5PP2421oCOOkFqI7a2OX/ZUPK5JoU.4z7DS0FsDemDYvavI1ei','李四',1,'13309090002',2,9000,'https://example.com/avatar/2.jpg','2019-03-15',2,'2023-10-20 16:35:33','2023-10-20 16:35:37'),
(3,'wangwu','$2a$10$jT75/lE7oPQ6JHjCvz9c0OhQmdljrDvKKEHUcJhMLq9iaiAxTlr4e','王五',2,'13309090003',3,10000,'https://example.com/avatar/3.jpg','2021-07-01',3,'2023-10-20 16:35:33','2023-10-20 16:35:39');

DROP TABLE IF EXISTS `emp_expr`;
CREATE TABLE `emp_expr` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
  `emp_id` int unsigned DEFAULT NULL COMMENT '员工ID',
  `begin` date DEFAULT NULL COMMENT '开始时间',
  `end` date DEFAULT NULL COMMENT '结束时间',
  `company` varchar(50) DEFAULT NULL COMMENT '公司名称',
  `job` varchar(50) DEFAULT NULL COMMENT '职位',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作经历表';

INSERT INTO `emp_expr` (`id`, `emp_id`, `begin`, `end`, `company`, `job`) VALUES
(1,1,'2018-07-01','2020-01-01','北京某某科技有限公司','Java开发工程师'),
(2,2,'2017-09-01','2019-03-01','上海某某信息技术有限公司','前端开发工程师');

DROP TABLE IF EXISTS `emp_log`;
CREATE TABLE `emp_log` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `info` varchar(2000) DEFAULT NULL COMMENT '日志信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工日志表';

INSERT INTO `emp_log` (`id`, `operate_time`, `info`) VALUES
(1,'2026-11-11 22:10:58','新增员工：张三'),
(2,'2026-11-11 22:11:04','修改员工：李四');

DROP TABLE IF EXISTS `operate_log`;
CREATE TABLE `operate_log` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
  `operate_emp_id` int unsigned DEFAULT NULL COMMENT '操作人ID',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `class_name` varchar(100) DEFAULT NULL COMMENT '操作的类名',
  `method_name` varchar(100) DEFAULT NULL COMMENT '操作的方法名',
  `method_params` varchar(2000) DEFAULT NULL COMMENT '方法参数',
  `return_value` varchar(2000) DEFAULT NULL COMMENT '返回值',
  `cost_time` bigint unsigned DEFAULT NULL COMMENT '方法执行耗时, 单位:ms',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';

INSERT INTO `operate_log` (`id`, `operate_emp_id`, `operate_time`, `class_name`, `method_name`, `method_params`, `return_value`, `cost_time`) VALUES
(1,1,'2026-11-11 22:10:58','com.trainingadmin.controller.DeptController','add','[{\"name\":\"教研部\"}]','{\"code\":1,\"msg\":\"success\"}',32),
(2,1,'2026-11-11 22:11:04','com.trainingadmin.controller.DeptController','update','[{\"id\":1,\"name\":\"教研部\"}]','{\"code\":1,\"msg\":\"success\"}',8);

DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主键',
  `name` varchar(10) NOT NULL COMMENT '姓名',
  `no` char(10) NOT NULL COMMENT '学号',
  `gender` tinyint unsigned NOT NULL COMMENT '性别, 1:男, 2:女',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `id_card` char(18) NOT NULL COMMENT '身份证号',
  `is_college` tinyint unsigned NOT NULL COMMENT '是否来自于院校, 1:是, 0:否',
  `address` varchar(100) DEFAULT NULL COMMENT '联系地址',
  `degree` tinyint unsigned DEFAULT NULL COMMENT '最高学历, 1:初中, 2:高中, 3:大专, 4:本科, 5:硕士, 6:博士',
  `graduation_date` date DEFAULT NULL COMMENT '毕业时间',
  `clazz_id` int unsigned NOT NULL COMMENT '班级ID, 关联班级表ID',
  `violation_count` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '违纪次数',
  `violation_score` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '违纪扣分',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `no` (`no`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `id_card` (`id_card`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学员表';

INSERT INTO `student` (`id`, `name`, `no`, `gender`, `phone`, `id_card`, `is_college`, `address`, `degree`, `graduation_date`, `clazz_id`, `violation_count`, `violation_score`, `create_time`, `update_time`) VALUES
(1,'赵六','2022000001',1,'18800000001','110120000300200001',1,'北京市昌平区建材城西路1号',4,'2022-07-01',1,0,0,'2023-11-14 21:22:19','2023-11-14 21:22:19'),
(2,'钱七','2022000002',2,'18800000002','110120000300200002',1,'北京市昌平区建材城西路2号',3,'2023-07-01',2,1,2,'2023-11-14 21:22:19','2024-01-10 10:00:00'),
(3,'孙八','2022000003',1,'18800000003','110120000300200003',0,'北京市昌平区回龙观街道3号',5,'2021-07-01',3,0,0,'2023-11-14 21:22:19','2023-11-14 21:22:19');

SET FOREIGN_KEY_CHECKS = 1;
