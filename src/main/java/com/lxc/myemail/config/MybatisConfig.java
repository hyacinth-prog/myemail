package com.lxc.myemail.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.lxc.myemail.mapper")
public class MybatisConfig {

}
