package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import yuriy.dev.carfixbackend.dto.UserDto;
import yuriy.dev.carfixbackend.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toDto(User user);
}
