package io.devfactory.view.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan("io.devfactory.view.mapper")
@Configuration(proxyBeanMethods = false)
public class MyBatisConfig {
}
