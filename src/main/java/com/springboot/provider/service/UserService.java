package com.springboot.provider.service;

import com.springboot.provider.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-09-11
 */
public interface UserService {

    Integer insert(User user);

    Integer update(User user);

    User getUser(User user);

    List<User> getUserList();
}
