package com.lxc.myemail.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

//@Component
//@ConfigurationProperties(prefix = "spring.mail")
public class EmailPropertiesConfig {
    private static  String host ="smtp.qq.com";
    private static String username="1693019995@qq.com";
    private static String password="waxspynwqwtqcjed";
}
