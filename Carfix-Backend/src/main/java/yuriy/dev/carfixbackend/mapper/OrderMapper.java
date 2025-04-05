package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yuriy.dev.carfixbackend.dto.OrderDto;
import yuriy.dev.carfixbackend.model.Order;

@Mapper(componentModel = "spring",uses = {CarMapper.class})
public interface OrderMapper {

    @Mapping(source = "carDto",target = "car")
    @Mapping(source = "userDto",target = "user")
    Order toOrder(OrderDto orderDto);

    @Mapping(source = "car", target = "carDto")
    @Mapping(source = "user",target = "userDto")
    OrderDto toDto(Order order);
}
