package yuriy.dev.carfixbackend.dto;


import yuriy.dev.carfixbackend.auth.Role;

import java.util.UUID;


public record UserDto(
        UUID id,
        String username,
        String password,
        String phoneNumber,
        String firstName,
        String lastName,
        Role role
        ) { }
