package io.devfactory.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

  @Bean
  public StringEncryptor jasyptStringEncryptor() {
    final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();

    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    config.setPassword("dbgmltlr");
    config.setAlgorithm("PBEWithMD5AndDES");
    config.setPoolSize(3);

    encryptor.setConfig(config);
    return encryptor;
  }

}
