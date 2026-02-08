package io.devfactory.like.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan("io.devfactory.like.mapper")
@Configuration(proxyBeanMethods = false)
public class MyBatisConfig {
}
