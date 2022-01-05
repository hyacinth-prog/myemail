package com.lxc.myemail.service;

import com.lxc.myemail.model.User;

public interface UserService {
    boolean loginCheckPassword(String username,String password);
    int registerUser(User user);
    boolean checkSameUser(String username);
    int getUserIdByUsername(String username);
    int updateUserPassword(User user);

}
