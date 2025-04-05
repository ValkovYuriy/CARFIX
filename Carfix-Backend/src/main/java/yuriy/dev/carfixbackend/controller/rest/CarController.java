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
import yuriy.dev.carfixbackend.dto.CarDto;
import yuriy.dev.carfixbackend.dto.response.ApiResponseDto;
import yuriy.dev.carfixbackend.service.CarService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;


    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CarDto>>> findAllCars() {
        List<CarDto> cars = carService.findAllCars();
        return ResponseEntity.ok(new ApiResponseDto<>("ok",cars));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CarDto>> findCarById(@PathVariable UUID id) {
        CarDto car = carService.findCarById(id);
        return ResponseEntity.ok(new ApiResponseDto<>("ok",car));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<CarDto>> addCar(@RequestBody CarDto carDto) {
        CarDto car = carService.addCar(carDto);
        return ResponseEntity.ok(new ApiResponseDto<>("ok",car));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CarDto>> updateCar(@PathVariable UUID id, @RequestBody CarDto carDto) {
        CarDto car = carService.updateCar(id,carDto);
        return ResponseEntity.ok(new ApiResponseDto<>("ok",car));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable UUID id) {
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }

}
