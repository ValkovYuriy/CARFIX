package yuriy.dev.carfixbackend.controller.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuriy.dev.carfixbackend.dto.response.ApiResponseDto;
import yuriy.dev.carfixbackend.dto.MarkDto;
import yuriy.dev.carfixbackend.service.MarkService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/marks")
public class MarkController {

    private final MarkService markService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<MarkDto>>> findAllMarks(){
        List<MarkDto> marks = markService.findAllMarks();
        return ResponseEntity.ok(new ApiResponseDto<>("OK", marks));
    }
}
