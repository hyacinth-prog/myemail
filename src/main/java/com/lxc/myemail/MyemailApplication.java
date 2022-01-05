package com.lxc.myemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)

public class MyemailApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyemailApplication.class, args);
    }

}
