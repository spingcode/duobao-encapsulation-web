package com.duobao.controller;

import com.duobao.entity.User;
import com.duobao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:zouw
 * @Description:
 * @Date:Created in 15:12 2018/8/13
 * @Modified By:
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/helloworld")
    public User getUser(String param) {
        User user = new User();
        user.setName("zw");
        user.setCard("1212");
        user.setPhone("1221");
        user.setZmf("asda");
        userService.insertUser(user);
        return user;
    }
}
