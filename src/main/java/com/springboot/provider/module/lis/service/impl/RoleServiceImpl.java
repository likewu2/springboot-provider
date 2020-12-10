package com.springboot.provider.module.lis.service.impl;

import com.springboot.provider.module.lis.entity.Role;
import com.springboot.provider.module.lis.mapper.RoleMapper;
import com.springboot.provider.module.lis.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-12-10
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
