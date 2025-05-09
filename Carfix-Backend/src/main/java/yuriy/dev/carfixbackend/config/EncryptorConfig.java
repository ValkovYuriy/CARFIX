package yuriy.dev.carfixbackend.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EncryptorConfig {

    @Value("${jasypt.encryptor.password}")
    private String password;

    @Value("${jasypt.encryptor.algorithm}")
    private String algorithm;

    @Value("${jasypt.encryptor.iv-generator-classname}")
    private String ivGeneratorClassname;

    @Value("${jasypt.encryptor.key-obtention-iterations}")
    private int keyObtentionIterations;

    @Value("${jasypt.encryptor.pool-size}")
    private int poolSize;

    @Value("${jasypt.encryptor.string-output-type}")
    private String stringOutputType;

    @Value("${jasypt.encryptor.salt-generator-classname}")
    private String saltGeneratorClassname;

    @Primary
    @Bean(name = "encryptorBean")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(algorithm);
        config.setKeyObtentionIterations(keyObtentionIterations);
        config.setPoolSize(poolSize);
        config.setSaltGeneratorClassName(saltGeneratorClassname);
        config.setIvGeneratorClassName(ivGeneratorClassname);
        config.setStringOutputType(stringOutputType);
        encryptor.setConfig(config);
        return encryptor;
    }
}
