package io.devfactory.comment.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan("io.devfactory.comment.mapper")
@Configuration(proxyBeanMethods = false)
public class MyBatisConfig {
}
