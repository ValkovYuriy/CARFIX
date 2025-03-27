package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import yuriy.dev.carfixbackend.dto.ModelDto;
import yuriy.dev.carfixbackend.model.Model;

@Mapper(componentModel = "spring")
public interface ModelMapper {

    ModelDto toDto(Model model);

    Model toModel(ModelDto modelDto);
}
