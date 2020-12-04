package com.springboot.provider.service.impl;

import com.springboot.provider.entity.Role;
import com.springboot.provider.mapper.lis.RoleMapper;
import com.springboot.provider.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-10-15
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Integer insert(Role role) {
        Integer flag = roleMapper.insert(role);

//        设置异常
//        int a = 1/0;
        return flag;
    }
}
