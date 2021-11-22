package com.springboot.provider.module.his.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.provider.common.ResultCode;
import com.springboot.provider.common.ResultJson;
import com.springboot.provider.module.his.entity.User;
import com.springboot.provider.module.his.service.UserService;
import com.springboot.provider.module.lis.entity.Role;
import com.springboot.provider.module.lis.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-12-10
 */
@RestController
@RequestMapping("/his/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("add")
    public ResultJson add() {
        User user = new User();
        user.setUsername("kkkkkkk");
        user.setPassword("1111111");
        Role role = new Role();
        role.setTitle("USER");
        role.setName("user");
        userService.insert(user);
        roleService.insert(role);

        // int i = 1/0;
        List<User> userList = userService.getAllUser();
        List<Role> roleList = roleService.getRoleList();
        Map<String, Object> map = new HashMap<>();
        map.put("userList", userList);
        map.put("roleList", roleList);
        return ResultJson.success(map);
    }

    @RequestMapping("getAllUser")
    public ResultJson getAllUser() {
        // List<User> userList = userService.getAllUser();
        Page<User> page = userService.page(new Page<>(), null);
        return ResultJson.success(page);
    }

    @RequestMapping("getById")
    public ResultJson getById(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultJson.failure(ResultCode.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage());
        }
        return ResultJson.success(userService.getByUserId(user.getId()));
    }

    @RequestMapping("save")
    public ResultJson save(@RequestBody User user) {
        return ResultJson.success(userService.save(user));
    }

    @RequestMapping("updateById")
    public ResultJson updateById(@RequestBody User user) {
        return ResultJson.success(userService.updateById(user));
    }
}
