package com.lxc.myemail.utils;

import com.lxc.myemail.model.EmailAccount;

public class CompleteEmailInfo {
    public static void complete(EmailAccount emailAccount) {
        String username = emailAccount.getEmailAddress();
        String sendHost = "smtp." + username.substring(username.indexOf('@') + 1);
        emailAccount.setSendHost(sendHost);
        if(emailAccount.getReceiveProtocol().equals("POP"))
        {
            String receiveHost = "pop3." + username.substring(username.indexOf('@') + 1);
            emailAccount.setReceiveProtocol("pop3");
            emailAccount.setReceiveHost(receiveHost);
            emailAccount.setReceivePort("110");
        }
        else
        {
            String receiveHost = "imap." + username.substring(username.indexOf('@') + 1);
            emailAccount.setReceiveProtocol("imap");
            emailAccount.setReceiveHost(receiveHost);
            emailAccount.setReceivePort("993");

        }

    }
}
