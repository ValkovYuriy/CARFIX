package yuriy.dev.carfixbackend.dto;


import yuriy.dev.carfixbackend.model.Mark;

import java.util.UUID;

public record ModelDto (
        UUID id,
        String modelName,
        Mark mark
){
}
