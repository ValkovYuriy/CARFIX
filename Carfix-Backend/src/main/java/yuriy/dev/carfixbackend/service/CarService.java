package yuriy.dev.carfixbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.CarDto;
import yuriy.dev.carfixbackend.mapper.CarMapper;
import yuriy.dev.carfixbackend.model.Car;
import yuriy.dev.carfixbackend.repository.CarRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarService {
    
    private final CarRepository carRepository;
    
    private final CarMapper carMapper;
    
    public List<CarDto> findAllCars() {
        return carRepository
                .findAll()
                .stream()
                .map(carMapper::toCarDto)
                .toList();
    }

    public CarDto addCar(CarDto carDto) {
        Car car = carMapper.toCar(carDto);
        return carMapper.toCarDto(carRepository.save(car));
    }

    public CarDto updateCar(UUID id, CarDto carDto) {
        Car car = carRepository.findById(id).orElse(null);
        CarDto updatedCar = null;
        if (car != null) {
            car.setGovNumber(carDto.govNumber());
            car.setVinNumber(carDto.vinNumber());
            updatedCar =  carMapper.toCarDto(carRepository.save(car));
        }
        return updatedCar;
    }

    public void deleteCar(UUID id) {
        carRepository.deleteById(id);
    }

    public CarDto findCarById(UUID id) {
        return carRepository.findById(id).map(carMapper::toCarDto).orElse(null);
    }


    public CarDto findCarByVin(String vin) {
        return carRepository.findByVinNumber(vin).map(carMapper::toCarDto).orElse(null);
    }
}
