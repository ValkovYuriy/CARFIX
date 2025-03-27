package yuriy.dev.carfixbackend.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import yuriy.dev.carfixbackend.dto.ApiResponseDto;
import yuriy.dev.carfixbackend.exception.ExistsByUsernameException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponseDto<Object>> handleExistsByUsernameException(ExistsByUsernameException e){
        log.error(e.getMessage());
        return ResponseEntity.ok(ApiResponseDto.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, HttpServletResponse response) {
        log.error(e.getMessage());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");

        // Установка статуса в зависимости от типа исключения
        if (e instanceof NoResourceFoundException) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            modelAndView.addObject("status", HttpStatus.NOT_FOUND.value());
            modelAndView.addObject("message", "Страница не найдена");
        } else if (e instanceof AccessDeniedException) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            modelAndView.addObject("status", HttpStatus.FORBIDDEN.value());
            modelAndView.addObject("message", "Доступ запрещен");
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            modelAndView.addObject("message", "Ошибка на стороне сервера");
        }
        return modelAndView;
    }
}
