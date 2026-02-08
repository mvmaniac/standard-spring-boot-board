package io.devfactory.article.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan("io.devfactory.article.mapper")
@Configuration(proxyBeanMethods = false)
public class MyBatisConfig {
}
