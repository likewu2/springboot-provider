package com.springboot.provider.mapper.lis;

import com.springboot.provider.entity.Role;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-10-15
 */
@Repository
public interface RoleMapper {

    Integer insert(Role role);
}
