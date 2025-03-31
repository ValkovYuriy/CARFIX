package yuriy.dev.carfixbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.ModelDto;
import yuriy.dev.carfixbackend.mapper.ModelMapper;
import yuriy.dev.carfixbackend.model.Model;
import yuriy.dev.carfixbackend.repository.ModelRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelService {

    private final ModelRepository modelRepository;

    private final ModelMapper modelMapper;

    public List<ModelDto> findAllModels() {
        return modelRepository
                .findAll()
                .stream()
                .map(modelMapper::toDto)
                .toList();
    }

    public ModelDto addModel(ModelDto modelDto) {
        Model model = modelMapper.toModel(modelDto);
        return modelMapper.toDto(modelRepository.save(model));
    }

    public ModelDto updateModel(ModelDto modelDto, UUID id) {
        Model model = modelRepository.findById(id).orElse(null);
        ModelDto updatedModel = null;
        if (model != null) {
            model.setModelName(modelDto.modelName());
            model.setMark(modelDto.mark());
            updatedModel =  modelMapper.toDto(modelRepository.save(model));
        }
        return updatedModel;
    }

    public void deleteModel(UUID id) {
        modelRepository.deleteById(id);
    }

    public ModelDto findModelById(UUID id) {
        return modelRepository.findById(id).map(modelMapper::toDto).orElse(null);
    }
}
