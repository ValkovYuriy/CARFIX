package yuriy.dev.carfixbackend.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuriy.dev.carfixbackend.dto.ApiResponseDto;
import yuriy.dev.carfixbackend.dto.ModelDto;
import yuriy.dev.carfixbackend.service.ModelService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ModelDto>>> findAllModels() {
        List<ModelDto> models = modelService.findAllModels();
        return ResponseEntity.ok(new ApiResponseDto<>("OK",models));
    }
}
