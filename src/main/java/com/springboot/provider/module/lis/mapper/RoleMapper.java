package com.springboot.provider.module.lis.mapper;

import com.springboot.provider.module.lis.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-12-10
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> getRoleList();
}
