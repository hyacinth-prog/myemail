package com.lxc.myemail.mapper.custom;

import com.lxc.myemail.model.User;
import org.apache.ibatis.annotations.Mapper;


public interface UserDao {
    public User queryByUsername(String username);
}
