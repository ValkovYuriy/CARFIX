package yuriy.dev.carfixbackend.dto.enums;

import lombok.Getter;

@Getter
public enum Status {
    PENDING("В ОБРАБОТКЕ"),
    ACCEPTED("ПРИНЯТ"),
    REJECTED("ОТКЛОНЕН");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

}
