package com.springboot.provider.mapper.his;

import com.springboot.provider.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-09-11
 */
@Repository
public interface UserMapper {
    Integer updateUser(User user);

    User getUser(User user);

    Integer insert(User user);

    List<User> getUserList();
}
