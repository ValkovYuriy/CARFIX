package yuriy.dev.carfixbackend.dto;


import java.sql.Date;
import java.util.UUID;

public record ReviewDto(UUID id,
         String reviewContent,
         Integer rating,
         Date reviewDate,
         UserDto userDto) {
}
