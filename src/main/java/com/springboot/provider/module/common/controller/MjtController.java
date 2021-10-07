package com.springboot.provider.module.common.controller;

import com.springboot.mjt.proxy.JdbcOperationsProxy;
import com.springboot.provider.common.ResultJson;
import com.springboot.provider.mjt.constants.Mapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/test")
public class MjtController {

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/sql")
    public ResultJson sql() {
//        DataSource dataSource = MultiDataSourceHolder.buildDataSource("mysql", "localhost", "3306", "development", "root", "root", "");
//        DataSourceFactory.addDataSource("development", dataSource);
//
//        JdbcOperations jdbcTemplate = com.springboot.mjt.proxy.JdbcOperationsProxy.getProxyInstance("development");
//
//        RowMapper<Role> rowMapper = new BeanPropertyRowMapper<>(Role.class);
////        List<Role> roles = jdbcTemplate.query("select * from role", rowMapper);
//        List<Role> roles = jdbcTemplate.query(Mapper.selectById, rowMapper, 1, "超级管理员");

        JdbcOperations hisDataSource = JdbcOperationsProxy.getProxyInstance("hisDataSource");
        JdbcOperations lisDataSource = JdbcOperationsProxy.getProxyInstance("lisDataSource");

        int saveUser = hisDataSource.update(Mapper.saveUser, "xzk", "123456", new Date(), 1);
//        int a = 1 / 0;
        int saveRole = lisDataSource.update(Mapper.saveRole, "AUDITOR", "auditor");
//        int b = 1 / 0;

        return ResultJson.success(saveRole == saveUser);
    }

}
