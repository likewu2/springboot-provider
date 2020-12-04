package com.springboot.provider.service.impl;

import com.springboot.provider.entity.Role;
import com.springboot.provider.entity.User;
import com.springboot.provider.mapper.his.UserMapper;
import com.springboot.provider.service.RoleService;
import com.springboot.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-09-11
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Override
    public Integer insert(User user) {

        Integer userFlag = userMapper.insert(user);
//        int i = 1/0;
        Integer roleFlag = roleService.insert(new Role("admin"));
        int j = 1/0;

//        手动回滚事务
//        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return userFlag.equals(roleFlag) ? 1 : 0;
    }


    @Override
    public Integer update(User user) {
        return userMapper.updateUser(user);
    }

    @Override
    public User getUser(User user) {
        return userMapper.getUser(user);
    }

    @Override
    public List<User> getUserList() {
        return userMapper.getUserList();
    }
}
