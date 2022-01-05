package com.lxc.myemail.service;

import com.alibaba.fastjson.JSONObject;
import com.lxc.myemail.model.EmailAccount;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface POP3ReceiveMail {
    Session setCollectProperties(EmailAccount emailAccount) throws GeneralSecurityException;
    List<JSONObject>  receive(EmailAccount emailAccount) ; //接收邮件
    List<JSONObject> parseMessage(Message ...messages);//解析邮件
    String getSubject(MimeMessage msg) throws MessagingException, UnsupportedEncodingException;    //获得解析后的邮件主题
    String getFrom(MimeMessage msg);       //获得邮件发件人
    String getReceiveAddress(MimeMessage msg,Message.RecipientType type);     //根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
    String getSentDate(MimeMessage msg,String pattern);       //获得邮件发送时间
    boolean isContainAttachment(Part part);      //判断邮件中是否包含附件
    boolean isSeen(MimeMessage msg) throws MessagingException;       //判断邮件是否已读
    boolean isReplySign(MimeMessage msg);      //判断邮件是否需要阅读回执
    String getPriority(MimeMessage msg);       //获得邮件的优先级
    void getMailTextContent(Part part,StringBuffer content);      //获得邮件文本内容
    void saveAttachment(Part part,String destDir);      //保存附件
    void saveFile(InputStream is, String destDir, String fileName);        //读取输入流中的数据保存至指定目录
    String decodeText(String encodeText) throws UnsupportedEncodingException;        //文本解码
    boolean checkHasHtml(Multipart part);               //判断是否为html邮件
}
