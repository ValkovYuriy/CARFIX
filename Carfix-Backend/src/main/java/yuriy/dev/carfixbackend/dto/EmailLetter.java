package yuriy.dev.carfixbackend.dto;

public record EmailLetter(
        String to,
        String subject,
        String text
) {
}
