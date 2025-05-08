package yuriy.dev.carfixbackend.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuriy.dev.carfixbackend.dto.PopularWork;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.dto.response.ApiResponseDto;
import yuriy.dev.carfixbackend.service.WorkService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/works")
public class WorkController {

    private final WorkService workService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<WorkDto>>> findAllWorks() {
        List<WorkDto> works = workService.findAll();
        return ResponseEntity.ok(new ApiResponseDto<>("OK", works));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponseDto<List<PopularWork>>> findPopularWorks() {
        return ResponseEntity.ok(new ApiResponseDto<>("OK",workService.findMostPopularWorksOfTheYear()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<WorkDto>> findWorkById(@PathVariable UUID id) {
        WorkDto work = workService.findById(id);
        return ResponseEntity.ok(new ApiResponseDto<>("OK", work));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<WorkDto>> createWork(@RequestBody WorkDto work) {
        WorkDto workDto = workService.createWork(work);
        return ResponseEntity.ok(new ApiResponseDto<>("OK", workDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<WorkDto>> updateWork(@PathVariable UUID id, @RequestBody WorkDto work) {
        WorkDto workDto = workService.updateWork(id, work);
        return ResponseEntity.ok(new ApiResponseDto<>("OK", workDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWork(@PathVariable UUID id){
        workService.deleteWork(id);
        return ResponseEntity.ok().build();
    }
}
