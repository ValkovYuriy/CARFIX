package yuriy.dev.carfixbackend.dto;

import yuriy.dev.carfixbackend.dto.enums.Status;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID id,
        CarDto carDto,
        UserDto userDto,
        Timestamp orderDate,
        BigDecimal price,
        Status status,
        String description,
        List<WorkDto> works
) {
}
