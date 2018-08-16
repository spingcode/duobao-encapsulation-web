package com.duobao.mapper.user;

import com.duobao.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    int insertUser(User user);

    int updateByPrimaryKeySelective(User record);

    User selectByPrimaryKey(Integer id);

}