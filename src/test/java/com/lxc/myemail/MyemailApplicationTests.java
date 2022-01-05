package com.lxc.myemail;

import com.alibaba.fastjson.JSONObject;
import com.lxc.myemail.mapper.auto.EmailAccountMapper;
import com.lxc.myemail.model.EmailAccount;
import com.lxc.myemail.model.EmailAccountExample;
import com.lxc.myemail.model.User;
import com.lxc.myemail.service.MailService;
import com.lxc.myemail.service.POP3ReceiveMail;
import com.lxc.myemail.service.UserService;
import com.lxc.myemail.utils.MD5Util;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.*;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SpringBootTest

@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
class MyemailApplicationTests {

//    @Autowired
//    JavaMailSender javaMailSender;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    @Autowired
    private POP3ReceiveMail pop3ReceiveMail;

    @Autowired
    EmailAccountMapper emailAccountMapper;
    @Test
    public void testSendEmail() throws UnsupportedEncodingException {
        mailService.sendSimpleMail("1693019995a@gmail.com", "这是一封IDEA测试邮件", "Happy NewYear");
    }

    @Test
    public void testSendHtmlEmail() {
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
                "                        您正在进行<span style=\"color: red\">修改密码</span>操作，请在验证码输入框中输入：<span style=\"color:#f60;font-size: 24px\">+"+"16545"+"+</span>，以完成操作。\n" +
                "                    </strong>\n" +
                "                </div>\n" +
                "                <div style=\"margin-bottom:30px;\">\n" +
                "                    <small style=\"display:block;margin-bottom:20px;font-size:12px;\">\n" +
                "                        <p style=\"color:#747474;\">\n" +
                "                            注意：验证码3分钟内有效！\n此操作可能会修改您的密码、登录邮箱或绑定手机。如非本人操作，请及时登录并修改密码以保证帐户安全\n" +
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
                "</body>\n"+ "</html>";
        //String content = "<html>\n" + "<body>\n" + " <h3 style='color:red'>hello world ! 这是一封html邮件!</h3>\n" + "</body>\n" + "</html>";
        mailService.sendHtmlMail("17347064540@163.com", "这是一封HTML邮件", content);


    }
    @Test
    public void testSendCode() {
        mailService.sendAuthCode("17347064540@163.com");
        mailService.confirmAuthCode("123453");


}
    @Test
    public void testAuthCode() {


        mailService.sendAuthCode("17347064540@163.com");

//        mailService.confirmAuthCode("678323");

    }

    @Test
    public void testUserService() {
        User user = new User();
        user.setUsername("莫晓");
        user.setPassword("123456");
        int i = userService.registerUser(user);
        System.out.println(user.getUserId());
    }



    @Test
    public void testMD5() throws MessagingException {


        String ps = "admin";
        String ps1 = MD5Util.encode(ps);
        String ps2 = MD5Util.encode(ps);
        System.out.println(ps1);
        System.out.println(ps2);

    }
//    @Autowired
//    JavaMailSenderImpl javaMailSender;

//    Session session = javaMailSender.getSession();
    @Test
    public void testemail() throws MessagingException {

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setUsername("17347064540@163.com");
        javaMailSender.setHost("smtp.163.com");
//        javaMailSender.setDefaultEncoding("UTF-8");
//        javaMailSender.setPort(25);

        javaMailSender.setPassword("MSYODAVUIWNHLASA");
        boolean result =false;
        try{
            javaMailSender.testConnection();
            result =true;

        }catch (Exception ignored)
        {

            System.out.println(ignored.toString());
        }
        System.out.println(result);
//        String username = javaMailSender.getUsername();
//        String password = javaMailSender.getPassword();
//        String protocol	= javaMailSender.getProtocol();
//        if (protocol == null) {
//            protocol = session.getProperty("mail.transport.protocol");
//            if (protocol == null) {
//                protocol = "smtp";
//            }
//        }
//        Transport transport = session.getTransport(protocol);
//        transport.connect(javaMailSender.getHost(), javaMailSender.getPort(), username, password);

    }
    @Test
    public void testPOP3() throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "pop3");		// 协议
        props.setProperty("mail.pop3.port", "110");				// 端口
        props.setProperty("mail.pop3.host", "pop3.163.com");	// pop3服务器

        // 创建Session实例对象
        Session session = Session.getInstance(props);
        Store store = session.getStore("pop3");
        store.connect("17347064540@163.com", "MSYODAVUIWNHLASA");

        // 获得收件箱

        POP3Store pop3Store = (POP3Store) session.getStore("pop3");
        POP3Folder pop3Folder = (POP3Folder) store.getFolder("INBOX");
        Folder folder = store.getFolder("INBOX");
        /* Folder.READ_ONLY：只读权限
         * Folder.READ_WRITE：可读可写（可以修改邮件的状态）
         */
        folder.open(Folder.READ_WRITE);	//打开收件箱

        // 由于POP3协议无法获知邮件的状态,所以getUnreadMessageCount得到的是收件箱的邮件总数
        System.out.println("未读邮件数: " + folder.getUnreadMessageCount());

        // 由于POP3协议无法获知邮件的状态,所以下面得到的结果始终都是为0
        System.out.println("删除邮件数: " + folder.getDeletedMessageCount());
        System.out.println("新邮件: " + folder.getNewMessageCount());

        // 获得收件箱中的邮件总数
        System.out.println("邮件总数: " + folder.getMessageCount());

        // 得到收件箱中的所有邮件,并解析
        Message[] messages = folder.getMessages();
       // parseMessage(messages);

        //释放资源
        folder.close(true);
        store.close();
    }

    @Test
    void testIMAP() throws MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";//ssl加密

        // 定义连接imap服务器的属性信息
        String port = "993";
        String imapServer = "imap.163.com";
        String protocol = "imap";

        //有些参数可能不需要
        Properties props = new Properties();
        props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.transport.protocol", protocol); // 使用的协议
        props.setProperty("mail.imap.port", port);
        props.setProperty("mail.imap.socketFactory.port", port);

        //创建会话
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("17347064540@163.com","MSYODAVUIWNHLASA");
            }
        });
        //存储对象
        Store store = session.getStore("imap");
        //连接
        store.connect("imap.163.com",993,"17347064540@163.com","MSYODAVUIWNHLASA");
    }

    @Test
    void testMap(){
//        ArrayList<JSONObject> arrayList = new ArrayList<>();
//        EmailAccount emailAccount = new EmailAccount();
//        emailAccount.setEmailId(1);
//        emailAccount.setEmailAddress("dsasdfa");
//        //JSONObject jsonObject =(JSONObject) JSONObject.toJSON(emailAccount);
//      arrayList.add((JSONObject) JSONObject.toJSON(emailAccount));
//        emailAccount.setEmailId(2);
//        emailAccount.setEmailAddress("drewrwrwa");
//        arrayList.add((JSONObject) JSONObject.toJSON(emailAccount));
//
//        System.out.println(arrayList.toString());
        EmailAccountExample emailAccountExample = new EmailAccountExample();
        EmailAccountExample.Criteria criteria = emailAccountExample.createCriteria();
        criteria.andUserIdEqualTo(1);
        List<EmailAccount> list=emailAccountMapper.selectByExample(emailAccountExample);
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        for (EmailAccount e:list) {
            JSONObject o = (JSONObject)JSONObject.toJSON(e);
            o.remove("receiveHost");
            o.remove("emailState");
            o.remove("sendHost");
            o.remove("emailPassword");
            o.remove("receiveProtocol");
            o.remove("receivePort");
            o.remove("userId");
            arrayList.add(o );


        }

//        System.out.println(list.toString());
        System.out.println(arrayList.toString());
    }

    @Test
    void testPOP3Service(){
        EmailAccount emailAccount = new EmailAccount();
        emailAccount.setEmailAddress("17347064540@163.com");
        emailAccount.setEmailPassword("MSYODAVUIWNHLASA");
        emailAccount.setReceiveProtocol("pop3");
        emailAccount.setReceiveHost("pop.163.com");
        emailAccount.setReceivePort("110");
       // Message[] messages = pop3ReceiveMail.receive(emailAccount);
//        pop3ReceiveMail.parseMessage(messages);

    }

    @Test
    void testdelHTML(){
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
                "                        您正在进行<span style=\"color: red\">修改密码</span>操作，请在验证码输入框中输入：<span style=\"color:#f60;font-size: 24px\">+"+"16545"+"+</span>，以完成操作。\n" +
                "                    </strong>\n" +
                "                </div>\n" +
                "                <div style=\"margin-bottom:30px;\">\n" +
                "                    <small style=\"display:block;margin-bottom:20px;font-size:12px;\">\n" +
                "                        <p style=\"color:#747474;\">\n" +
                "                            注意：验证码3分钟内有效！\n此操作可能会修改您的密码、登录邮箱或绑定手机。如非本人操作，请及时登录并修改密码以保证帐户安全\n" +
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
                "</body>\n"+ "</html>";
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式


        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(content);
        content=m_script.replaceAll(""); //过滤script标签
       // System.out.println(content);

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(content);
        content=m_style.replaceAll(""); //过滤style标签
       // System.out.println(content);

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(content);
        content=m_html.replaceAll(""); //过滤html标签

        Pattern p_space=Pattern.compile("[\\r\\n\\s]",Pattern.CASE_INSENSITIVE);
        Matcher m_space=p_space.matcher(content);
        content=m_space.replaceAll(""); //过滤html标签
        System.out.println(content);
    }
    @Test
    void contextLoads() {
        String u ="dsada";
        String j= u.toString();

        j="d";
        System.out.println(u.hashCode()+"-"+j.hashCode());
        System.out.println(u==j);
    }

}
