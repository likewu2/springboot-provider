package com.springboot.provider.controller;

import com.springboot.provider.common.ResultJson;
import com.springboot.provider.entity.User;
import com.springboot.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-09-11
 */
@RestController
@RequestMapping("/user/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    public ResultJson add(@RequestBody User user){
        return ResultJson.success(userService.insert(user));
    }

    @RequestMapping("/update")
    public ResultJson update(@RequestBody User user){
        return ResultJson.success(userService.update(user));
    }

    @RequestMapping("/getUser")
    public ResultJson getUser(@RequestBody User user){
        return ResultJson.success(userService.getUser(user));
    }

    @RequestMapping("/getUserList")
    public ResultJson getUserList(){
        return ResultJson.success(userService.getUserList());
    }
}
