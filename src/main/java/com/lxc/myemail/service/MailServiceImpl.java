package com.lxc.myemail.service;

import com.alibaba.fastjson.JSONObject;
import com.lxc.myemail.mapper.auto.EmailAccountMapper;
import com.lxc.myemail.mapper.custom.EmailAccountDao;
import com.lxc.myemail.model.AuthCode;
import com.lxc.myemail.model.EmailAccount;
import com.lxc.myemail.model.EmailAccountExample;
import com.lxc.myemail.utils.CompleteEmailInfo;
import com.lxc.myemail.utils.RandomAuthCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AuthCode authCode;

    private String nick="捷讯邮件系统";

    RandomAuthCode randomAuthCode =  new RandomAuthCode();


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    EmailAccountMapper emailAccountMapper;

    @Autowired
    EmailAccountDao emailAccountDao;


    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendSimpleMail(String to, String subject, String content) throws UnsupportedEncodingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(String.valueOf(new InternetAddress(from,nick,"UTF-8")));
      //  message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
            logger.info("简单邮件已经发送。");
        } catch (Exception e) {
            logger.error("发送简单邮件时发生异常！", e);
        }
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try { //true 表示需要创建一个 multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(new InternetAddress(from,nick,"UTF-8"));
//            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            logger.info("html邮件发送成功");
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("发送html邮件时发生异常！", e);
        }
    }

    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(new InternetAddress(from,nick,"UTF-8"));
//            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = file.getFilename();
            assert fileName != null;
            helper.addAttachment(fileName, file);
            helper.addAttachment("test" + fileName, file);
            mailSender.send(message);
            logger.info("带附件的邮件已经发送。");
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("发送带附件的邮件时发生异常！", e);
        }
    }

    @Override
    public boolean authSMTPServer(EmailAccount emailAccount) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        CompleteEmailInfo.complete(emailAccount);
        javaMailSender.setUsername(emailAccount.getEmailAddress());
        javaMailSender.setHost(emailAccount.getSendHost());
        javaMailSender.setPassword(emailAccount.getEmailPassword());
        boolean result = false;
        try {
            javaMailSender.testConnection();
            result = true;

        } catch (Exception ignored) {

            System.out.println(ignored.toString());
        }
        return result;
    }

    @Override
    public boolean authPOP3Server(EmailAccount emailAccount) throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", emailAccount.getReceiveProtocol());        // 协议
        props.setProperty("mail.pop3.port", emailAccount.getReceivePort());                // 端口
        props.setProperty("mail.pop3.host", emailAccount.getReceiveHost());    // pop3服务器

        // 创建Session实例对象
        Session session = Session.getInstance(props);
        Store store = session.getStore("pop3");
        try {
            store.connect(emailAccount.getEmailAddress(), emailAccount.getEmailPassword());
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return false;
    }

    @Override
    public boolean authIMAPServer(EmailAccount emailAccount) throws NoSuchProviderException {

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";//ssl加密


        Properties props = new Properties();
        props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.transport.protocol", emailAccount.getReceiveProtocol()); // 使用的协议
        props.setProperty("mail.imap.port", emailAccount.getReceivePort());
        props.setProperty("mail.imap.socketFactory.port", emailAccount.getReceivePort());

        //创建会话
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount.getEmailAddress(), emailAccount.getEmailPassword());
            }
        });
        //存储对象
        Store store = session.getStore("imap");
        //连接
        try {
            store.connect(emailAccount.getReceiveHost(), Integer.parseInt(emailAccount.getReceivePort()), emailAccount.getEmailAddress(), emailAccount.getEmailPassword());
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());

        }

        return false;
    }

    @Override
    public int addEmailAccount(EmailAccount emailAccount) {
        return emailAccountMapper.insertSelective(emailAccount);
    }

    @Override
    public List<JSONObject> getEmailListByUserId(int id) {
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        EmailAccountExample emailAccountExample = new EmailAccountExample();
        EmailAccountExample.Criteria criteria = emailAccountExample.createCriteria();
        criteria.andUserIdEqualTo(id);
        List<EmailAccount>list=emailAccountMapper.selectByExample(emailAccountExample);
        for (EmailAccount e:list) {
            JSONObject o = (JSONObject)JSONObject.toJSON(e);
            o.remove("receiveHost");
            o.remove("emailState");
            o.remove("sendHost");
            o.remove("emailPassword");
            o.remove("receiveProtocol");
            o.remove("receivePort");
            o.remove("userId");
            arrayList.add(o);
        }
        return arrayList;
    }

    @Override
    public boolean sendAuthCode(String to) {
        authCode  = new AuthCode();
        authCode.setEmail(to);
        randomAuthCode.createCode(authCode);
        String content ="<!DOCTYPE html PUBLIC -//W3C//DTD XHTML 1.0 Transitional//EN' \n" +
                "'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>"+"<html>\n"+ "<head>\n" +
                "    <base target=\"_blank\" />\n" +
                "    <style type=\"text/css\">::-webkit-scrollbar{ display: none; }</style>\n" +
                "    <style id=\"cloudAttachStyle\" type=\"text/css\">#divNeteaseBigAttach, #divNeteaseBigAttach_bak{display:none;}</style>\n" +
                "    <style id=\"blockquoteStyle\" type=\"text/css\">blockquote{display:none;}</style>\n" +
                "    <style type=\"text/css\">\n" +
                "        body{font-size:14px;font-family:arial,verdana,sans-serif;line-height:1.666;padding:0;margin:0;overflow:auto;white-space:normal;word-wrap:break-word;min-height:100px}\n" +
                "        td, input, button, select, body{font-family:Helvetica, 'Microsoft Yahei', verdana}\n" +
                "        pre {white-space:pre-wrap;white-space:-moz-pre-wrap;white-space:-pre-wrap;white-space:-o-pre-wrap;word-wrap:break-word;width:95%}\n" +
                "        th,td{font-family:arial,verdana,sans-serif;line-height:1.666}\n" +
                "        img{ border:0}\n" +
                "        header,footer,section,aside,article,nav,hgroup,figure,figcaption{display:block}\n" +
                "        blockquote{margin-right:0px}\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body tabindex=\"0\" role=\"listitem\">\n" +
                "<table width=\"700\" border=\"0\" align=\"center\" cellspacing=\"0\" style=\"width:700px;\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td>\n" +
                "            <div style=\"width:700px;margin:0 auto;border-bottom:1px solid #ccc;margin-bottom:30px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"700\" height=\"39\" style=\"font:12px Tahoma, Arial, 宋体;\">\n" +
                "                    <tbody><tr><td width=\"210\"></td></tr></tbody>\n" +
                "                </table>\n" +
                "            </div>\n" +
                "            <div style=\"width:680px;padding:0 10px;margin:0 auto;\">\n" +
                "                <div style=\"line-height:1.5;font-size:14px;margin-bottom:25px;color:#4d4d4d;\">\n" +
                "                    <strong style=\"display:block;margin-bottom:15px;\">尊敬的用户：<span style=\"color:#f60;font-size: 16px;\"></span>您好！</strong>\n" +
                "                    <strong style=\"display:block;margin-bottom:15px;\">\n" +
                "                        您正在进行<span style=\"color: red\">修改密码</span>操作，请在验证码输入框中输入：<span style=\"color:#f60;font-size: 24px\">"+authCode.getCode()+"</span>，以完成操作。\n" +
                "                    </strong>\n" +
                "                </div>\n" +
                "                <div style=\"margin-bottom:30px;\">\n" +
                "                    <small style=\"display:block;margin-bottom:20px;font-size:12px;\">\n" +
                "                        <p style=\"color:#747474;\">\n" +
                "                            注意：此操作可能会修改您的密码、登录邮箱或绑定手机。如非本人操作，请及时登录并修改密码以保证帐户安全\n" +
                "                            <br>（工作人员不会向你索取此验证码，请勿泄漏！)\n" +
                "                        </p>\n" +
                "                    </small>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div style=\"width:700px;margin:0 auto;\">\n" +
                "                <div style=\"padding:10px 10px 0;border-top:1px solid #ccc;color:#747474;margin-bottom:20px;line-height:1.3em;font-size:12px;\">\n" +
                "                    <p>此为系统邮件，请勿回复<br>\n" +
                "                        请保管好您的邮箱，避免账号被他人盗用\n" +
                "                    </p>\n" +
                "                    <p>捷讯邮件系统团队</p>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>\n" +
                "</body>\n"+ "</html>";;
        try {
            sendHtmlMail(to,"捷讯邮件系统邮箱验证码邮件",content);
            return true;
        }catch (Exception e)
        {
            System.out.println(e.toString());
        }
        return false;
    }

    @Override
    public boolean confirmAuthCode(String code) {
        AuthCode code1 = new AuthCode();
        code1.setEmail("17347064540@163.com");
        code1.setCode(code);

       return randomAuthCode.verifyCode(code1);


    }

    @Override
    public EmailAccount getEmailAccountByEmailAddress(String emailAddress) {
        EmailAccount emailAccount = null;
        try {
            EmailAccountExample emailAccountExample = new EmailAccountExample();
            EmailAccountExample.Criteria criteria = emailAccountExample.createCriteria();
            criteria.andEmailAddressEqualTo(emailAddress);
            emailAccount= emailAccountMapper.selectByExample(emailAccountExample).get(0);
        }catch (Exception e){
            System.out.println("getEmailAccountByEmailAddress:"+e.toString());
        }
        return emailAccount;
    }

}

