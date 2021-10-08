package com.springboot.provider.module.common.controller;

import com.springboot.mjt.proxy.JdbcOperationsProxy;
import com.springboot.provider.common.ResultJson;
import com.springboot.provider.mjt.constants.Mapper;
import com.springboot.provider.module.lis.entity.Role;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class MjtController {

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/sql")
    public ResultJson sql() {
        JdbcOperations hisJdbcOperations = JdbcOperationsProxy.getProxyInstance("hisDataSource");
        JdbcOperations lisJdbcOperations = JdbcOperationsProxy.getProxyInstance("lisDataSource");

//        int saveUser = hisJdbcOperations.update(Mapper.saveUser, "xzk", "123456", new Date(), 1);
////        int a = 1 / 0;
//        int saveRole = lisJdbcOperations.update(Mapper.saveRole, "auditor", "AUDITOR");
////        int b = 1 / 0;

//        String title = lisJdbcOperations.queryForObject(Mapper.getRoleById, String.class, 1);

//        Map<String, Object> map = lisJdbcOperations.queryForMap(Mapper.getRoleById, 1);

//        List<Map<String, Object>> maps = lisJdbcOperations.queryForList(Mapper.getRoleById, 1);

        List<Role> roles = lisJdbcOperations.query(Mapper.selectById, new BeanPropertyRowMapper<>(Role.class), 1, "ADMIN");

//        RowMapper<Role> rowMapper = new BeanPropertyRowMapper<>(Role.class);
////        List<Role> roles = jdbcTemplate.query("select * from role", rowMapper);
//        List<Role> roles = lisJdbcOperations.query(Mapper.selectById, rowMapper, 1, "ADMIN");

//        List<Object[]> list = new ArrayList<>();
//        list.add(new Object[]{"admin", "ADMIN"});
//        list.add(new Object[]{"dba", "DBA"});
//        int[] ints = lisJdbcOperations.batchUpdate("insert into role (name, title) values (?, ?)", list);

//        List<Role> roleList = new ArrayList<>();
//        roleList.add(new Role("admin", "ADMIN"));
//        roleList.add(new Role("dba", "DBA"));
//        int[][] ints = lisJdbcOperations.batchUpdate("insert into role (name, title) values (?, ?)", roleList, roleList.size(), (preparedStatement, role) -> {
//            preparedStatement.setString(1, role.getName());
//            preparedStatement.setString(2, role.getTitle());
//        });

        return ResultJson.success(roles);
    }

}
