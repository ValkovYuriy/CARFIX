package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import yuriy.dev.carfixbackend.dto.MarkDto;
import yuriy.dev.carfixbackend.model.Mark;

@Mapper(componentModel = "spring")
public interface MarkMapper {

    MarkDto toDto(Mark mark);

    Mark toMark(MarkDto markDto);
}
