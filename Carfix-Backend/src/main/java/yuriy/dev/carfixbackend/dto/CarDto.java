package yuriy.dev.carfixbackend.dto;

import java.util.UUID;

public record CarDto(
        UUID id,
        String govNumber,
        String vinNumber,
        Integer yearOfRelease,
        ModelDto modelDto,
        UserDto userDto
) {
}
