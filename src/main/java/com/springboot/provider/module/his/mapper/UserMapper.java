package com.springboot.provider.module.his.mapper;

import com.springboot.provider.module.his.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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
public interface UserMapper extends BaseMapper<User> {
    List<User> getAllUser();

    User getByUserId(@Param("id")Long id);

}
