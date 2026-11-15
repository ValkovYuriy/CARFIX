package yuriy.dev.carfixbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yuriy.dev.carfixbackend.dto.CarDto;
import yuriy.dev.carfixbackend.mapper.CarMapper;
import yuriy.dev.carfixbackend.model.Car;
import yuriy.dev.carfixbackend.repository.CarRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarService carService;

    private static final UUID CAR_ID = UUID.randomUUID();

    private Car buildCar() {
        return Car.builder()
                .id(CAR_ID)
                .govNumber("А123ВС163")
                .vinNumber("1HGCM82633A123456")
                .build();
    }

    @Test
    void findAllCars_returnsMappedDtos() {
        Car car = buildCar();
        CarDto dto = new CarDto(CAR_ID, "А123ВС163", "1HGCM82633A123456", null);

        when(carRepository.findAll()).thenReturn(List.of(car));
        when(carMapper.toCarDto(car)).thenReturn(dto);

        List<CarDto> result = carService.findAllCars();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).vinNumber()).isEqualTo("1HGCM82633A123456");
    }

    @Test
    void addCar_savesAndReturnsDto() {
        CarDto input = new CarDto(null, "А123ВС163", "1HGCM82633A123456", null);
        Car car = buildCar();
        CarDto saved = new CarDto(CAR_ID, "А123ВС163", "1HGCM82633A123456", null);

        when(carMapper.toCar(input)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toCarDto(car)).thenReturn(saved);

        CarDto result = carService.addCar(input);

        assertThat(result.id()).isEqualTo(CAR_ID);
        verify(carRepository).save(car);
    }

    @Test
    void updateCar_existingCar_updatesNumbers() {
        Car car = buildCar();
        CarDto update = new CarDto(CAR_ID, "В456ОР163", "ZZZ11122233344455", null);
        CarDto expected = new CarDto(CAR_ID, "В456ОР163", "ZZZ11122233344455", null);

        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toCarDto(car)).thenReturn(expected);

        CarDto result = carService.updateCar(CAR_ID, update);

        assertThat(car.getGovNumber()).isEqualTo("В456ОР163");
        assertThat(car.getVinNumber()).isEqualTo("ZZZ11122233344455");
        assertThat(result.govNumber()).isEqualTo("В456ОР163");
    }

    @Test
    void updateCar_notFound_returnsNull() {
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.empty());

        CarDto result = carService.updateCar(CAR_ID, new CarDto(CAR_ID, "x", "y", null));

        assertThat(result).isNull();
        verify(carRepository, never()).save(any());
    }

    @Test
    void deleteCar_delegatesToRepository() {
        carService.deleteCar(CAR_ID);

        verify(carRepository).deleteById(CAR_ID);
    }

    @Test
    void findCarByVin_returnsDto_whenExists() {
        Car car = buildCar();
        CarDto dto = new CarDto(CAR_ID, "А123ВС163", "1HGCM82633A123456", null);

        when(carRepository.findByVinNumber("1HGCM82633A123456")).thenReturn(Optional.of(car));
        when(carMapper.toCarDto(car)).thenReturn(dto);

        assertThat(carService.findCarByVin("1HGCM82633A123456").id()).isEqualTo(CAR_ID);
    }

    @Test
    void findCarByVin_returnsNull_whenNotExists() {
        when(carRepository.findByVinNumber(any())).thenReturn(Optional.empty());

        assertThat(carService.findCarByVin("UNKNOWN")).isNull();
    }
}
