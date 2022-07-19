package com.springboot.provider.module.common.service.impl;

import com.springboot.provider.common.constants.SnowflakeConstants;
import com.springboot.provider.module.common.service.CommonService;
import com.springboot.provider.module.his.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.module.common.service.impl
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-04 14:29
 **/
@Service
@Transactional(transactionManager = "transactionManager")
public class CommonServiceImpl implements CommonService {

    private final PasswordEncoder passwordEncoder;

    private final JdbcOperations hisJdbcOperations;

    private final JdbcOperations lisJdbcOperations;

    public CommonServiceImpl(PasswordEncoder passwordEncoder, @Qualifier("hisJdbcOperations") JdbcOperations hisJdbcOperations, @Qualifier("lisJdbcOperations") JdbcOperations lisJdbcOperations) {
        this.passwordEncoder = passwordEncoder;
        this.hisJdbcOperations = hisJdbcOperations;
        this.lisJdbcOperations = lisJdbcOperations;
    }

    @Override
    public Integer insert() {
        String username = UUID.randomUUID().toString();
        username = username.substring(0, 16);

        int his = hisJdbcOperations.update("insert into user(id, username, password) values(?, ?, ?)", SnowflakeConstants.next(), username, passwordEncoder.encode(username));
//        int a= 1/0;

        int lis = lisJdbcOperations.update("insert into role(id, name, title) values(?, ?, ?)", SnowflakeConstants.next(), "admin", "ADMIN");
//        int i = 1/0;
        return his + lis;
    }

    @Override
    public Integer update(User user) {
        try {
            return hisJdbcOperations.update("update user set username = ?, password = ? where id = ?", user.getUsername(), user.getPassword(), user.getId());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer deleteById(Long id) {
        try {
            return hisJdbcOperations.update("delete from user where id = ?", id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> selectAll() {
        try {
            return hisJdbcOperations.query("select * from user", new BeanPropertyRowMapper<>(User.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> selectByUsername(String username) {
        try {
            return hisJdbcOperations.query("select * from user where username = ?", new BeanPropertyRowMapper<>(User.class), username);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public User selectById(Long id) {
        try {
            return hisJdbcOperations.queryForObject("select * from user where id = ?", new BeanPropertyRowMapper<>(User.class), id);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
