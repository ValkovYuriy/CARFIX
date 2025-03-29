package yuriy.dev.carfixbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkPriceDto(
        UUID id,
        BigDecimal price,
        LocalDateTime date,
        WorkDto workDto
) {
}
