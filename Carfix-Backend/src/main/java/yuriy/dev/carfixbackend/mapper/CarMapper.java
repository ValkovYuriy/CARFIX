package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import yuriy.dev.carfixbackend.dto.CarDto;
import yuriy.dev.carfixbackend.model.Car;

@Mapper(componentModel = "spring",uses = {ModelMapper.class, UserMapper.class})
public interface CarMapper {

    @Mappings(
    {
        @Mapping(source = "userDto",target = "user"),
        @Mapping(source = "modelDto",target = "model")
    }
    )
    Car toCar(CarDto carDto);

    @Mappings(
    {
        @Mapping(source = "user",target = "userDto"),
        @Mapping(source = "model",target = "modelDto")
    }
    )
    CarDto toCarDto(Car car);
}
