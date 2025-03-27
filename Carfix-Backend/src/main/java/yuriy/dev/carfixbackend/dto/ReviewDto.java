package yuriy.dev.carfixbackend.dto;


import yuriy.dev.carfixbackend.model.User;

import java.sql.Date;
import java.util.UUID;

public record ReviewDto(UUID id,
         String reviewContent,
         Integer rating,
         Date reviewDate,
         User user) {
}
