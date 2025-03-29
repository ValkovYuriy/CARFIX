package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yuriy.dev.carfixbackend.dto.ReviewDto;
import yuriy.dev.carfixbackend.model.Review;

@Mapper(componentModel = "spring",uses = UserMapper.class)
public interface ReviewMapper {

    @Mapping(source = "user", target = "userDto")
    ReviewDto toDto(Review review);

    @Mapping(source = "userDto", target = "user")
    Review toReview(ReviewDto reviewDto);
}
