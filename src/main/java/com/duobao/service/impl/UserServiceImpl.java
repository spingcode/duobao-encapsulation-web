package com.duobao.service.impl;

import com.duobao.entity.User;
import com.duobao.mapper.user.UserMapper;
import com.duobao.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("userServiceImpl")
public class UserServiceImpl implements UserService{

   @Autowired
    private UserMapper userMapper;
    @Override
    public boolean insertUser(User user) {
        if (user == null || StringUtils.isBlank(user.getName())
                || StringUtils.isBlank(user.getCard())
                || StringUtils.isBlank(user.getPhone())
                || StringUtils.isBlank(user.getZmf())) {
            return false;
        }
        user.setName("1");
        user.setZmf("1");
        user.setCard("1");
        return userMapper.insertUser(user)>0;
    }

}
