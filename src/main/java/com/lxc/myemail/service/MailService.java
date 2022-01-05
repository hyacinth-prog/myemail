package com.lxc.myemail.service;

import com.alibaba.fastjson.JSONObject;
import com.lxc.myemail.model.EmailAccount;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MailService {
    void sendSimpleMail(String to, String subject, String content) throws UnsupportedEncodingException;

    void sendHtmlMail(String to, String subject, String content);

    void sendAttachmentsMail(String to, String subject, String content, String filePath);

    boolean authSMTPServer(EmailAccount emailAccount) ;
    boolean authPOP3Server(EmailAccount emailAccount) throws MessagingException;
    boolean authIMAPServer(EmailAccount emailAccount) throws NoSuchProviderException;

    int addEmailAccount(EmailAccount emailAccount);

    List<JSONObject> getEmailListByUserId(int id);

    boolean sendAuthCode(String to);

    boolean confirmAuthCode(String code);

    EmailAccount getEmailAccountByEmailAddress(String emailAddress);
}
