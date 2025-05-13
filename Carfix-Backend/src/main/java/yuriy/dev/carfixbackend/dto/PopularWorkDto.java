package yuriy.dev.carfixbackend.dto;

public record PopularWorkDto(
        String name,
        String imageBase64,
        Long count
) {
}
