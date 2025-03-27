package yuriy.dev.carfixbackend.dto;


import yuriy.dev.carfixbackend.model.Model;

import java.util.List;
import java.util.UUID;

public record MarkDto(
        UUID id,
        String markName,
        List<Model> models
        ) {
}
