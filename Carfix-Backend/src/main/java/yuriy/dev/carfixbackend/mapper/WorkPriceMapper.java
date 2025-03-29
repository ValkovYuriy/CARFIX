package yuriy.dev.carfixbackend.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yuriy.dev.carfixbackend.dto.WorkPriceDto;
import yuriy.dev.carfixbackend.model.WorkPrice;

@Mapper(componentModel = "spring",uses = WorkMapper.class)
public interface WorkPriceMapper {

    @Mapping(source = "workDto",target = "work")
    WorkPrice toWorkPrice(WorkPriceDto workPriceDto);

    @Mapping(source = "work",target = "workDto")
    WorkPriceDto toWorkPriceDto(WorkPrice workPrice);
}
