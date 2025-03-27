package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import yuriy.dev.carfixbackend.dto.ReviewDto;
import yuriy.dev.carfixbackend.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDto toDto(Review review);

    Review toReview(ReviewDto dto);
}
