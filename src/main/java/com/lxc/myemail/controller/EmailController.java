package com.lxc.myemail.controller;

import com.alibaba.fastjson.JSONObject;
import com.lxc.myemail.model.EmailAccount;
import com.lxc.myemail.service.IMAPReceiveMailImpl;
import com.lxc.myemail.service.MailService;
import com.lxc.myemail.service.POP3ReceiveMail;
import com.lxc.myemail.service.UserService;
import com.lxc.myemail.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.mail.Message;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    MailService mailService;
    @Autowired
    UserService userService;
    @Autowired
    private POP3ReceiveMail pop3ReceiveMail;
    @Autowired
    private IMAPReceiveMailImpl imapReceiveMail;

    @GetMapping(value = "/emailAccount/authSendServer")
    public Map<String, Object> authSend(HttpServletRequest request,
                                        EmailAccount emailAccount) {
        Map<String, Object> retMap = new HashMap<>();
//        System.out.println("******");
        try {
            if (emailAccount != null) {
//                System.out.println(emailAccount.getEmailAddress());
//                System.out.println(emailAccount.getEmailPassword());
                boolean result = mailService.authSMTPServer(emailAccount);
                if (result) {
                    retMap.put("code", 200);
                    retMap.put("msg", "验证成功");
                    retMap.put("email", emailAccount);
                } else {
                    retMap.put("code", 500);
                    retMap.put("msg", "验证失败");
                }


            } else {
                retMap.put("code", 500);
                retMap.put("msg", "未知错误");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            retMap.put("code", 500);
            retMap.put("msg", "服务器内部错误");
        }
        return retMap;
    }

    @GetMapping(value = "/emailAccount/authReceiveServer")
    public Map<String, Object> authReceive(HttpServletRequest request,
                                           EmailAccount emailAccount) {
        Map<String, Object> retMap = new HashMap<>();
//        System.out.println("******");
        String msg = "";
        try {
            if (emailAccount != null) {
//                System.out.println(emailAccount.getEmailAddress());
                System.out.println(emailAccount.toString());
                boolean result = false;
                if (emailAccount.getReceiveProtocol().equals("pop3")) {
                    result = mailService.authPOP3Server(emailAccount);
                } else {
                    result = mailService.authIMAPServer(emailAccount);
                }
                int i = 0;
                if (result) i = mailService.addEmailAccount(emailAccount);
                System.out.println(result);
                if (result && i > 0) {

                    retMap.put("code", 200);
                    retMap.put("msg", "添加成功");

                } else {
                    retMap.put("code", 500);
                    retMap.put("msg", "添加失败");
                }


            } else {
                retMap.put("code", 500);
                retMap.put("msg", "未知错误");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            retMap.put("code", 500);
            retMap.put("msg", "服务器内部错误");
        }
        return retMap;
    }

    @GetMapping(value = "/emailAccount/getEmailAddressList")
    public Map<String, Object> getEmailAddressList(HttpServletRequest request) {
        Map<String, Object> retMap = new HashMap<>();
        int id=0;
        String username= TokenUtil.getUsername(request.getHeader("token"));
        try {
             id = userService.getUserIdByUsername(username);
            ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) mailService.getEmailListByUserId(id);
           // System.out.println(arrayList.toString());
            if (!arrayList.isEmpty()) {
                retMap.put("code", 200);
                retMap.put("list", arrayList);
                System.out.println(arrayList.toString());
            } else {
                retMap.put("code", 500);
                retMap.put("msg", "该账户未绑定邮箱账号");
            }
        }catch (Exception e){
            System.out.println(e.toString());
            if (id==0) {
                System.out.println(id);
                retMap.put("code", 500);
                retMap.put("msg", "不存在的账户");

            }
            else {
                retMap.put("code", 500);
                retMap.put("msg", "服务器内部错误");

            }
        }



        return retMap;
    }

    @GetMapping(value = "/receive/getEmailList")
    public Map<String, Object> getEmailList(HttpServletRequest request,String emailAddress) {
        Map<String, Object> retMap = new HashMap<>();
        ArrayList<JSONObject> arrayList =null;
        String username= TokenUtil.getUsername(request.getHeader("token"));
        int id=0;
        try {
            EmailAccount emailAccount = mailService.getEmailAccountByEmailAddress(emailAddress);
            if (emailAccount.getReceiveProtocol().equals("pop3")){
                arrayList = (ArrayList<JSONObject>) pop3ReceiveMail.receive(emailAccount);
                System.out.println(arrayList.toString());

            }else {
//                Message[] message = imapReceiveMail.receive(emailAccount);
//                ArrayList<JSONObject> arrayList= (ArrayList<JSONObject>) imapReceiveMail.parseMessage(message);
            }


            // System.out.println(arrayList.toString());
            if (!arrayList.isEmpty()) {
                retMap.put("code", 200);
                retMap.put("list", arrayList);
            } else {
                retMap.put("code", 500);
                retMap.put("msg", "接收邮件失败");
            }
        }catch (Exception e){
            System.out.println(e.toString());


                retMap.put("code", 500);
                retMap.put("msg", "服务器内部错误");


        }
        return retMap;
    }


    @GetMapping(value = "/emailAccount/getAuthCode")
    public Map<String, Object> getAuthCode(HttpServletRequest request,String emailAddress) {
        Map<String, Object> retMap = new HashMap<>();
        try{
//            if (emailAddress==null||emailAddress.equals("")){
//                    retMap.put("msg", "邮箱地址为空");
//                    retMap.put("code", 500);
//            }

            boolean result = mailService.sendAuthCode(emailAddress);

            if (result) {
                retMap.put("msg", "验证码已发送，请注意查收！");
                retMap.put("code", 200);

            } else {
                retMap.put("code", 500);
                retMap.put("msg", "验证码发送失败,请稍后重试！");
            }
        }catch (Exception e){
            retMap.put("code", 500);
            retMap.put("msg", "服务器内部错误");
        }


        return retMap;
    }



}
