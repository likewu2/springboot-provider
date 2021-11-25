package com.springboot.provider.module.his.service.impl;

import com.springboot.provider.module.his.entity.User;
import com.springboot.provider.module.his.mapper.UserMapper;
import com.springboot.provider.module.his.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-12-10
 */
@Service
@Transactional(transactionManager = "transactionManager")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    @Override
    public User getByUserId(Long id) {
        return userMapper.getByUserId(id);
    }

    @Override
    public Integer insert(User user) {
        return userMapper.insert(user);
    }
}
