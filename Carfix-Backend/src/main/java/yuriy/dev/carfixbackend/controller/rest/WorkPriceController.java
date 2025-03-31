package yuriy.dev.carfixbackend.controller.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuriy.dev.carfixbackend.dto.response.ApiResponseDto;
import yuriy.dev.carfixbackend.dto.WorkPriceDto;
import yuriy.dev.carfixbackend.service.WorkPriceService;

import java.util.List;

@RestController
@RequestMapping("/api/work-prices")
@RequiredArgsConstructor
public class WorkPriceController {

    private final WorkPriceService workPriceService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<WorkPriceDto>>> findWorkPrices(){
        List<WorkPriceDto> workPriceDtos = workPriceService.findAllWorkPrices();
        return ResponseEntity.ok(new ApiResponseDto<>("ok", workPriceDtos));
    }
}
