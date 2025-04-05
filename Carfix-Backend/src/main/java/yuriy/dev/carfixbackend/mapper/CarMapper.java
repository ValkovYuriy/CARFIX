package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yuriy.dev.carfixbackend.dto.CarDto;
import yuriy.dev.carfixbackend.model.Car;

@Mapper(componentModel = "spring",uses = {ModelMapper.class})
public interface CarMapper {

    @Mapping(source = "modelDto",target = "model")
    Car toCar(CarDto carDto);

    @Mapping(source = "model",target = "modelDto")
    CarDto toCarDto(Car car);
}
