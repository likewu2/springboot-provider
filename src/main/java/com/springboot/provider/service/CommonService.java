package com.springboot.provider.service;

import com.springboot.provider.entity.User;

import java.util.List;

/**
 * @program: springboot-provider
 * @package com.springboot.provider.service
 * @description
 * @author: XuZhenkui
 * @create: 2020-12-04 14:28
 **/
public interface CommonService {

    Integer insert();

    Integer update(User user);

    Integer deleteById(Long id);

    List<User> selectAll();

    List<User> selectByUsername(String username);

    User selectById(Long id);
}
