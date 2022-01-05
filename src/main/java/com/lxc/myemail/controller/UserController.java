package com.lxc.myemail.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxc.myemail.model.User;
import com.lxc.myemail.service.MailService;
import com.lxc.myemail.service.UserService;

import com.lxc.myemail.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;


    @GetMapping(value = "/login")
    public Map<String, Object> login(HttpServletRequest request,
                                     String username, String password) {
        Map<String, Object> retMap = new HashMap<>();

        try {
            if (userService.loginCheckPassword(username, password)) {
                String token = TokenUtil.sign(new User(username, password));
                int id = userService.getUserIdByUsername(username);
                ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) mailService.getEmailListByUserId(id);


                System.out.println("token:" + token);
                retMap.put("code", 200);
                retMap.put("token", token);
                retMap.put("list", arrayList);
                retMap.put("msg", "登录成功");

            } else {
                retMap.put("code", 500);
                retMap.put("msg", "用户名密码错误");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            retMap.put("code", 500);
            retMap.put("msg", "服务器内部错误");
        }
        return retMap;
    }

    @GetMapping(value = "/check")
    public Map<String, Object> check(HttpServletRequest request,
                                     String checkName) {
        Map<String, Object> retMap = new HashMap<>();
        try {
            if (userService.checkSameUser(checkName)) {
                retMap.put("code", 200);
                retMap.put("msg", "exist");
            } else {
                retMap.put("code", 500);
                retMap.put("msg", "notfound");
            }
        } catch (Exception e) {
            retMap.put("code", 500);
            retMap.put("msg", "服务器内部错误");
        }
        return retMap;
    }

    @PostMapping(value = "/register")
    public Map<String, Object> register(HttpServletRequest request,
                                        @RequestBody User user) {
        Map<String, Object> retMap = new HashMap<>();
        if (user != null)
            System.out.println(user.getUsername());
        else
            System.out.println("wrong");

        try {
            if (userService.registerUser(user) > 0) {
                retMap.put("code", 200);
                retMap.put("msg", "注册成功");
            } else {
                retMap.put("code", 500);
                retMap.put("msg", "注册失败");
            }
        } catch (Exception e) {
            retMap.put("code", 500);
            retMap.put("msg", "服务器内部错误");
        }
        return retMap;
    }

    /**
     * 重置密码
     *
     * @param request
     * @param map
     * @return
     */
    @PostMapping(value = "/reset/password")
    public Map<String, Object> resetPassword(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        Map<String, Object> retMap = new HashMap<>();
//        boolean result = mailService.(code);
        String s = JSON.toJSONString(map.get("user"));
        String code = (String) map.get("code");
        User user = JSON.parseObject(s, User.class);
        int i = 0;
        try {
            user.setUserId(userService.getUserIdByUsername(user.getUsername()));
            if (mailService.confirmAuthCode(code)) {
                i = userService.updateUserPassword(user);
                if (i > 0) {
                    retMap.put("msg", "密码修改成功");
                    retMap.put("code", 200);
                } else {
                    retMap.put("msg", "密码修改失败");
                    retMap.put("code", 500);
                }
            } else {
                retMap.put("msg", "验证码错误");
                retMap.put("code", 500);

            }

        } catch (Exception e) {
            retMap.put("msg", "服务器内部错误");
            retMap.put("code", 500);
            System.out.println(e.toString());
        }
//        System.out.println(user.toString());
//
//        System.out.println(code);
//
//        if (result) {
//            retMap.put("msg", "验证码已发送，请注意查收！");
//            retMap.put("code", 200);
//
//        } else {
//            retMap.put("code", 500);
//            retMap.put("msg", "验证码发送失败,请稍后重试！");
//        }
        return retMap;
    }

    @PostMapping(value = "/edit/password")
    public Map<String, Object> editPassword(HttpServletRequest request, @RequestBody User user) {
        Map<String, Object> retMap = new HashMap<>();

        int i=0;

        try {
            String username= TokenUtil.getUsername(request.getHeader("token"));
            user.setUserId(userService.getUserIdByUsername(username));
            user.setUsername(username);

            i = userService.updateUserPassword(user);
            if (i > 0) {
                retMap.put("msg", "密码修改成功");
                retMap.put("code", 200);
            } else {
                retMap.put("msg", "密码修改失败");
                retMap.put("code", 500);
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }


        return retMap;
    }

    /***
     * 获取邮箱地址列表
     * @param request
     * @param username
     * @return
     */
    @GetMapping(value = "/reset/getEmailAddressList")
    public Map<String, Object> getEmailAddressList(HttpServletRequest request, String username) {
        Map<String, Object> retMap = new HashMap<>();
        int id = 0;


        try {
            id = userService.getUserIdByUsername(username);
            ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) mailService.getEmailListByUserId(id);
            // System.out.println(arrayList.toString());
            if (!arrayList.isEmpty()) {
                retMap.put("code", 200);
                retMap.put("list", arrayList);
            } else {
                retMap.put("code", 500);
                retMap.put("msg", "该账户未绑定邮箱账号");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            if (id == 0) {
                System.out.println(id);
                retMap.put("code", 500);
                retMap.put("msg", "不存在的账户");

            } else {
                retMap.put("code", 500);
                retMap.put("msg", "服务器内部错误");

            }
        }
        return retMap;
    }


    @GetMapping(value = "/receive/getEmailList")
    public Map<String, Object> getEmailList(HttpServletRequest request, String username) {
        Map<String, Object> retMap = new HashMap<>();
        int id = 0;


        try {
            id = userService.getUserIdByUsername(username);
            ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) mailService.getEmailListByUserId(id);
            // System.out.println(arrayList.toString());
            if (!arrayList.isEmpty()) {
                retMap.put("code", 200);
                retMap.put("list", arrayList);
            } else {
                retMap.put("code", 500);
                retMap.put("msg", "该账户未绑定邮箱账号");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            if (id == 0) {
                System.out.println(id);
                retMap.put("code", 500);
                retMap.put("msg", "不存在的账户");

            } else {
                retMap.put("code", 500);
                retMap.put("msg", "服务器内部错误");

            }
        }
        return retMap;
    }
}
