package com.lxc.myemail.service;


import com.lxc.myemail.mapper.auto.UserMapper;
import com.lxc.myemail.mapper.custom.UserDao;
import com.lxc.myemail.model.User;
import com.lxc.myemail.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean loginCheckPassword(String username, String password) {

        User user = userDao.queryByUsername(username);
        if (user != null) {
            return user.getPassword().equals(MD5Util.encode(password));
        } else return false;
    }

    @Override
    public int registerUser(User user) {

        if (checkSameUser(user.getUsername())) return -1;//已存在相同用户名
        else {
            user.setPassword(MD5Util.encode(user.getPassword()));
            return userMapper.insertSelective(user);
        }

    }

    @Override
    public boolean checkSameUser(String username) {
        return userDao.queryByUsername(username) != null;
    }

    @Override
    public int getUserIdByUsername(String username) {
        User user = userDao.queryByUsername(username);
        return userDao.queryByUsername(username).getUserId();
    }

    @Override
    public int updateUserPassword(User user) {
        user.setPassword(MD5Util.encode(user.getPassword()));
        try {
            return userMapper.updateByPrimaryKeySelective(user);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return 0;
    }



}
