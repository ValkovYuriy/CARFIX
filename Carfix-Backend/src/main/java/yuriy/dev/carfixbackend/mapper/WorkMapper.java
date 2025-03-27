package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.model.Work;


@Mapper(componentModel = "spring")
public interface WorkMapper {

    WorkDto toDto(Work work);

    Work toWork(WorkDto workDto);
}
