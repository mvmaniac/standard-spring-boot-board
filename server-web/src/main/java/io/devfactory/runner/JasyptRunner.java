package io.devfactory.runner;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JasyptRunner implements ApplicationRunner {

  @Override
  public void run(ApplicationArguments args) {
    StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
    pbeEnc.setPassword("dbgmltlr"); // 암호화 키를 입력

    // 암호화
    String enc1 = pbeEnc.encrypt("123");
    String enc2 = pbeEnc.encrypt("456");
    String enc3 = pbeEnc.encrypt("789");
    log.debug("[dev] enc: {}, {}, {}", enc1, enc2, enc3);

    // 복호화
    String des1 = pbeEnc.decrypt(enc1);
    String des2 = pbeEnc.decrypt(enc2);
    String des3 = pbeEnc.decrypt(enc3);
    log.debug("[dev] enc: {}, {}, {}", des1, des2, des3);
  }

}
