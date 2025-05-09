package yuriy.dev.carfixbackend.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;

@Converter
@RequiredArgsConstructor
public class StringCryptoConverter implements AttributeConverter<String, String> {


    private final StringEncryptor encryptor;


    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute != null ? encryptor.encrypt(attribute) : null;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData != null ? encryptor.decrypt(dbData) : null;
    }
}
