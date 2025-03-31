package yuriy.dev.carfixbackend.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID id,
        CarDto carDto,
        Timestamp orderDate,
        BigDecimal price,
        List<WorkDto> works
) {
}
