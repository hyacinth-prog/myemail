package com.lxc.myemail.service;

import com.alibaba.fastjson.JSONObject;
import com.lxc.myemail.model.EmailAccount;
import com.lxc.myemail.utils.TextUtil;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import javax.mail.Message;
import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class POP3ReceiveMailImpl implements POP3ReceiveMail {

    @Override
    public Session setCollectProperties(EmailAccount emailAccount) {
        Properties props = new Properties();

        props.setProperty("mail.store.protocol", "pop3");        // 协议
        props.setProperty("mail.pop3.port", emailAccount.getReceivePort());                // 端口
        props.setProperty("mail.pop3.host", emailAccount.getReceiveHost());    // pop3服务器
//        props.setProperty("mail.popStore.protocol", "pop3");       // 使用pop3协议
//        props.setProperty("mail.pop3.port", "995");           // 端口
//
//        MailSSLSocketFactory sf = new MailSSLSocketFactory();
//        sf.setTrustAllHosts(true);
//        props.put("mail.pop3.ssl.enable",true);
//        props.put("mail.pop3.ssl.socketFactory",sf);
//        props.setProperty("mail.pop3.host", "pop.qq.com");
        return Session.getInstance(props);

    }

    @Override
    public List<JSONObject> receive(EmailAccount emailAccount) {
        ArrayList<JSONObject> arrayList = null;
        Message[] messages = null;
        try {
            Session session = setCollectProperties(emailAccount);
            POP3Store pop3Store = (POP3Store) session.getStore("pop3");
            //host,port,username,password
            pop3Store.connect(emailAccount.getReceiveHost(), Integer.parseInt(emailAccount.getReceivePort()), emailAccount.getEmailAddress(), emailAccount.getEmailPassword());

            POP3Folder pop3Folder = (POP3Folder) pop3Store.getFolder("INBOX");
            pop3Folder.open(Folder.READ_WRITE); //打开收件箱
            /* Folder.READ_ONLY：只读权限
             * Folder.READ_WRITE：可读可写（可以修改邮件的状态）
             */

            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);

            FlagTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            FlagTerm flagTerm1 = new FlagTerm(new Flags(),false);
            messages = pop3Folder.search(flagTerm1);//得到收件箱中的所有邮件
            pop3Folder.fetch(messages, fetchProfile);
            int length = messages.length;
            System.out.println("收件箱的邮件数：" + length);

            Folder folder = pop3Folder.getStore().getDefaultFolder();
            Folder[] folders = folder.list();

            for (Folder value : folders) {
                System.out.println("名称：" + value.getName());
            }
//            for (Message message : messages) {
//                MimeMessage msg = (MimeMessage) message;
//                String from = MimeUtility.decodeText(message.getFrom()[0].toString());
//                InternetAddress ia = new InternetAddress(from);
//                JSONObject o = new JSONObject();
//                o.put("发件人",ia.getPersonal() + '(' + ia.getAddress() + ')');
//                o.put("主题",message.getSubject());
//                o.put("邮件发送时间",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message.getSentDate()));
//
//
//
//
//
//                System.out.println("发件人：" + ia.getPersonal() + '(' + ia.getAddress() + ')');
//                System.out.println("主题：" + message.getSubject());
//                System.out.println("邮件发送时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message.getSentDate()));
//                boolean isContainerAttachment = isContainAttachment(msg);
//                System.out.println("是否包含附件：" + isContainerAttachment);
////                if (isContainerAttachment) {
////                    //设置需要保存的目录并保存附件
////                    saveAttachment(msg, "F:\\test\\"+msg.getSubject() + "_"+i+"_"); //保存附件
////                }
////                message.setFlag(Flags.Flag.SEEN, true);
//
//                arrayList.add(o);
//            }


            //释放资源
            arrayList = (ArrayList<JSONObject>) parseMessage(messages);

            pop3Folder.close(true);
            pop3Store.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return arrayList;
    }

    @Override
    public List<JSONObject> parseMessage(Message... messages) {
        ArrayList<JSONObject> arrayList = new ArrayList<>();
        try {
            if (messages == null || messages.length < 1)
                throw new MessagingException("未找到要解析的邮件!");

            // 解析所有邮件
            for (Message message : messages) {
                StringBuffer content = new StringBuffer(30);

                MimeMessage msg = (MimeMessage) message;
                getMailTextContent(msg, content);
                JSONObject o = new JSONObject();
                o.put("subject", getSubject(msg));
                o.put("from", getFrom(msg));
                o.put("date", getSentDate(msg, null));
                o.put("mainContent", content);
                o.put("content", TextUtil.filterHtml(content.toString()));

//                o.put("age","32");
//                System.out.println("------------------解析第" + msg.getMessageNumber() + "封邮件-------------------- ");
//                System.out.println("主题: " + getSubject(msg));
//                System.out.println("发件人: " + getFrom(msg));
//                System.out.println("收件人：" + getReceiveAddress(msg, null));
//                System.out.println("发送时间：" + getSentDate(msg, null));
//                System.out.println("是否已读：" + isSeen(msg));
//                System.out.println("邮件优先级：" + getPriority(msg));
//                System.out.println("是否需要回执：" + isReplySign(msg));
//                System.out.println("邮件大小：" + msg.getSize() * 1024 + "kb");
//                boolean isContainerAttachment = isContainAttachment(msg);
//                System.out.println("是否包含附件：" + isContainerAttachment);
//                if (isContainerAttachment) {
//                    saveAttachment(msg, "c:\\mailtmp\\"+msg.getSubject() + "_"); //保存附件
//                }


//                System.out.println("邮件正文：" + (content.length() > 100 ? content.substring(0, 100) + "..." : content));
//                System.out.println("------------------第" + msg.getMessageNumber() + "封邮件解析结束-------------------- ");
//                System.out.println();

                arrayList.add(o);

            }
        } catch (Exception e) {

            System.out.println(e.toString());
        }
        System.out.println(arrayList.toString());
        return arrayList;
    }

    /**
     * 获得邮件主题
     *
     * @param msg 邮件内容
     * @return 解码后的邮件主题
     */
    @Override
    public String getSubject(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        return MimeUtility.decodeText(msg.getSubject());
    }

    /**
     * 获得邮件发件人
     *
     * @param msg 邮件内容
     * @return 姓名 <Email地址>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    @Override
    public String getFrom(MimeMessage msg) {
        String from = "";
        try {
            Address[] froms = msg.getFrom();
            if (froms.length < 1)
                throw new MessagingException("没有发件人!");

            InternetAddress address = (InternetAddress) froms[0];
            String person = address.getPersonal();
            if (person != null) {
                person = MimeUtility.decodeText(person) + " ";
            } else {
                person = "";
            }
            if (person.equals("")) from = person + "<" + address.getAddress() + ">";
            else from = person;
        } catch (Exception e) {
            System.out.println("getFrom:" + e.toString());
        }
        return from;
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     *
     * @param msg  邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws MessagingException
     */
    @Override
    public String getReceiveAddress(MimeMessage msg, Message.RecipientType type) {
        StringBuffer receiveAddress = new StringBuffer();
        try {
            Address[] addresss = null;
            if (type == null) {
                addresss = msg.getAllRecipients();
            } else {
                addresss = msg.getRecipients(type);
            }

            if (addresss == null || addresss.length < 1)
                throw new MessagingException("没有收件人!");
            for (Address address : addresss) {
                InternetAddress internetAddress = (InternetAddress) address;
                receiveAddress.append(internetAddress.toUnicodeString()).append(",");
            }

            receiveAddress.deleteCharAt(receiveAddress.length() - 1);    //删除最后一个逗号
        } catch (Exception e) {
            System.out.println("getReceiveAddress:" + e.toString());
        }


        return receiveAddress.toString();
    }

    /**
     * 获得邮件发送时间
     *
     * @param msg 邮件内容
     * @return yyyy年mm月dd日 星期X HH:mm
     * @throws MessagingException
     */
    @Override
    public String getSentDate(MimeMessage msg, String pattern) {
        Date receivedDate = null;
        try {
            receivedDate = msg.getSentDate();
            if (receivedDate == null)
                return "";

            if (pattern == null || "".equals(pattern))
                pattern = "yyyy年MM月dd日 E HH:mm ";

            return new SimpleDateFormat(pattern).format(receivedDate);
        } catch (Exception e) {
            System.out.println("getSentDate:" + e.toString());
        }
        return new SimpleDateFormat(pattern).format(receivedDate);
    }

    /**
     * 判断邮件中是否包含附件
     *
     * @param part 邮件内容
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    @Override
    public boolean isContainAttachment(Part part) {
        boolean flag = false;
        try {
            if (part.isMimeType("multipart/*")) {
                MimeMultipart multipart = (MimeMultipart) part.getContent();
                int partCount = multipart.getCount();
                for (int i = 0; i < partCount; i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    String disp = bodyPart.getDisposition();
                    if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                        flag = true;
                    } else if (bodyPart.isMimeType("multipart/*")) {
                        flag = isContainAttachment(bodyPart);
                    } else {
                        String contentType = bodyPart.getContentType();
                        if (contentType.indexOf("application") != -1) {
                            flag = true;
                        }

                        if (contentType.indexOf("name") != -1) {
                            flag = true;
                        }
                    }

                    if (flag) break;
                }
            } else if (part.isMimeType("message/rfc822")) {
                flag = isContainAttachment((Part) part.getContent());
            }
        } catch (Exception e) {
            System.out.println("isContainAttachment:" + e.toString());
        }
        return flag;
    }

    /**
     * 判断邮件是否已读
     *
     * @param msg 邮件内容
     * @return 如果邮件已读返回true, 否则返回false
     * @throws MessagingException
     */
    @Override
    public boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    @Override
    public boolean isReplySign(MimeMessage msg) {
        boolean replySign = false;
        try {

            String[] headers = msg.getHeader("Disposition-Notification-To");
            if (headers != null)
                replySign = true;
        } catch (Exception e) {
            System.out.println("isReplySign:" + e.toString());
        }
        return replySign;
    }

    /**
     * 获得邮件的优先级
     *
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException
     */
    @Override
    public String getPriority(MimeMessage msg) {
        String priority = "普通";
        try {

            String[] headers = msg.getHeader("X-Priority");
            if (headers != null) {
                String headerPriority = headers[0];
                if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
                    priority = "紧急";
                else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
                    priority = "低";
                else
                    priority = "普通";
            }
        } catch (Exception e) {
            System.out.println("getPriority:" + e.toString());
        }
        return priority;
    }

    /**
     * 获得邮件文本内容
     *
     * @param part    邮件体
     * @param content 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    @Override
    public void getMailTextContent(Part part, StringBuffer content) {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        try {
            boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
            if (part.isMimeType("text/*") && !isContainTextAttach) {
                content.append(part.getContent().toString());
            } else if (part.isMimeType("message/rfc822")) {
                getMailTextContent((Part) part.getContent(), content);
            } else if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) part.getContent();
                boolean hasHtml = checkHasHtml(multipart);//这里校验是否有text/html内容
                int partCount = multipart.getCount();
                for (int i = 0; i < partCount; i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (!bodyPart.isMimeType("text/plain")||!hasHtml) {
                        //有html格式的则不显示无格式文档的内容
                        getMailTextContent(bodyPart, content);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("getMailTextContent:" + e.toString());
        }

    }

    /**
     * 保存附件
     *
     * @param part    邮件中多个组合体中的其中一个组合体
     * @param destDir 附件保存目录
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public void saveAttachment(Part part, String destDir) {
        try {
            if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
                //复杂体邮件包含多个邮件体
                int partCount = multipart.getCount();
                for (int i = 0; i < partCount; i++) {
                    //获得复杂体邮件中其中一个邮件体
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    //某一个邮件体也有可能是由多个邮件体组成的复杂体
                    String disp = bodyPart.getDisposition();
                    if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                        InputStream is = bodyPart.getInputStream();
                        saveFile(is, destDir, decodeText(bodyPart.getFileName()));
                    } else if (bodyPart.isMimeType("multipart/*")) {
                        saveAttachment(bodyPart, destDir);
                    } else {
                        String contentType = bodyPart.getContentType();
                        if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                            saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                        }
                    }
                }
            } else if (part.isMimeType("message/rfc822")) {
                saveAttachment((Part) part.getContent(), destDir);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 读取输入流中的数据保存至指定目录
     *
     * @param is       输入流
     * @param fileName 文件名
     * @param destDir  文件存储目录
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public void saveFile(InputStream is, String destDir, String fileName) {
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(

                    new FileOutputStream(new File(destDir + fileName)));
            int len = -1;
            while ((len = bis.read()) != -1) {
                bos.write(len);
                bos.flush();
            }
            bos.close();
            bis.close();


        } catch (Exception e) {
            System.out.println("saveAttachment:" + e.toString());
        }
    }

    /**
     * 文本解码
     *
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws UnsupportedEncodingException
     */
    @Override
    public String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }

    @Override
    public boolean checkHasHtml(Multipart part) {
        boolean hasHtml = false;
        try {
            int count = part.getCount();
            for (int i = 0; i < count; i++) {
                Part bodyPart = part.getBodyPart(i);
                if (bodyPart.isMimeType("text/html")) {
                    hasHtml = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("checkHasHtml:" + e.toString());
        }
        return hasHtml;
    }


}

