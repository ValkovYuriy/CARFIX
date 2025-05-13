package yuriy.dev.carfixbackend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WorkDto(
        UUID id,
        String name,
        String description,
        BigDecimal workPrice,
        String imageBase64
) {
}
