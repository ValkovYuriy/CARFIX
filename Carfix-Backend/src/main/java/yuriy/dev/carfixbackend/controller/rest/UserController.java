package yuriy.dev.carfixbackend.controller.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuriy.dev.carfixbackend.dto.UserDto;
import yuriy.dev.carfixbackend.dto.response.ApiResponseDto;
import yuriy.dev.carfixbackend.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDto<String>> updateUser(@PathVariable UUID id, @RequestBody UserDto userDto) {
        String updatedToken = userService.updateUser(id,userDto);
        return ResponseEntity.ok(new ApiResponseDto<>("OK",updatedToken));
    }
}
