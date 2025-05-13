package yuriy.dev.carfixbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import yuriy.dev.carfixbackend.dto.PopularWork;
import yuriy.dev.carfixbackend.dto.PopularWorkDto;
import yuriy.dev.carfixbackend.util.ImageConverter;

@Mapper(componentModel = "spring")
public interface PopularWorkMapper {

    @Mapping(target = "image",source = "imageBase64",qualifiedByName = "toByteArray")
    PopularWork toPopularWork(PopularWorkDto popularWorkDto);

    @Mapping(target = "imageBase64",source = "image",qualifiedByName = "toBase64")
    PopularWorkDto toPopularWorkDto(PopularWork popularWork);

    @Named("toBase64")
    default String toBase64(byte[] imageBytes) {
        return ImageConverter.toBase64(imageBytes);
    }

    @Named("toByteArray")
    default byte[] toByteArray(String base64String) {
        return ImageConverter.toByteArray(base64String);
    }
}
