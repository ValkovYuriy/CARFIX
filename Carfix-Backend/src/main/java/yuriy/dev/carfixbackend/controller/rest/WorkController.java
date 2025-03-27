package yuriy.dev.carfixbackend.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuriy.dev.carfixbackend.dto.ApiResponseDto;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.service.WorkService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/services")
public class WorkController {

    private final WorkService workService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<WorkDto>>> findAllWorks() {
        List<WorkDto> works = workService.findAll();
        return ResponseEntity.ok(new ApiResponseDto<>("OK", works));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<WorkDto>> findWorkById(@PathVariable UUID id) {
        WorkDto work = workService.findById(id);
        return ResponseEntity.ok(new ApiResponseDto<>("OK", work));
    }
}
