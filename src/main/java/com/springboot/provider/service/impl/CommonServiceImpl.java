package com.springboot.provider.service.impl;

import com.springboot.provider.entity.User;
import com.springboot.provider.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.service.impl
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-04 14:29
 **/
@Service
@Transactional(transactionManager = "transactionManager")
public class CommonServiceImpl implements CommonService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("hisJdbcTemplate")
    private JdbcTemplate hisJdbcTemplate;

    @Autowired
    @Qualifier("lisJdbcTemplate")
    private JdbcTemplate lisJdbcTemplate;

    @Override
    public Integer insert() {
        String username = UUID.randomUUID().toString();
        username  = username.substring(0,16);

        int his = hisJdbcTemplate.update("insert into user(username,password) values(?,?)", username, passwordEncoder.encode(username));
//        int a= 1/0;

        int lis = lisJdbcTemplate.update("insert into role(title) values(?)", "ADMIN");
//        int i = 1/0;
        return his + lis;
    }

    @Override
    public Integer update(User user) {
        try {
            return hisJdbcTemplate.update("update user set username=?,password=? where id=?",user.getUsername(),user.getPassword(),user.getId());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer deleteById(Long id) {
        try {
            return hisJdbcTemplate.update("delete from user where id=?", id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> selectAll() {
        try {
            return hisJdbcTemplate.query("select * from user", new BeanPropertyRowMapper<>(User.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> selectByUsername(String username) {
        try {
            return hisJdbcTemplate.query("select * from user where username = ?", new BeanPropertyRowMapper<>(User.class), username);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public User selectById(Long id) {
        try {
            return hisJdbcTemplate.queryForObject("select * from user where id = ?",new BeanPropertyRowMapper<>(User.class), id);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
