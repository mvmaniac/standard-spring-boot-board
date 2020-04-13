package io.devfactory.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JasyptRunner implements ApplicationRunner {

  private final StringEncryptor jasyptStringEncryptor;

  @Override
  public void run(ApplicationArguments args) {
    // 암호화
    String enc1 = jasyptStringEncryptor.encrypt("123");
    String enc2 = jasyptStringEncryptor.encrypt("456");
    String enc3 = jasyptStringEncryptor.encrypt("789");
    log.debug("[dev] enc: {}, {}, {}", enc1, enc2, enc3);

    // 복호화
    String des1 = jasyptStringEncryptor.decrypt(enc1);
    String des2 = jasyptStringEncryptor.decrypt(enc2);
    String des3 = jasyptStringEncryptor.decrypt(enc3);
    log.debug("[dev] des: {}, {}, {}", des1, des2, des3);
  }

}
