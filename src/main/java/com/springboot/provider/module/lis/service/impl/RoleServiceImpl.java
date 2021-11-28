package com.springboot.provider.module.lis.service.impl;

import com.springboot.provider.module.lis.entity.Role;
import com.springboot.provider.module.lis.mapper.RoleMapper;
import com.springboot.provider.module.lis.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-12-10
 */
@Service
@Transactional(transactionManager = "transactionManager")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public List<Role> getRoleList() {
        return roleMapper.getRoleList();
    }

    // @Transactional(transactionManager = "transactionManager", propagation = Propagation.REQUIRES_NEW)
    @Override
    public Integer insert(Role role) {
        return roleMapper.insert(role);
    }
}
