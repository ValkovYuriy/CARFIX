package yuriy.dev.carfixbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.ModelDto;
import yuriy.dev.carfixbackend.mapper.ModelMapper;
import yuriy.dev.carfixbackend.repository.ModelRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelService {

    private final ModelRepository modelRepository;

    private final ModelMapper modelMapper;

    public List<ModelDto> findAllModels() {
        return modelRepository.findAll().stream().map(modelMapper::toDto).toList();
    }
}
