package yuriy.dev.carfixbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.mapper.WorkMapper;
import yuriy.dev.carfixbackend.model.Work;
import yuriy.dev.carfixbackend.repository.WorkRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;

    private final WorkMapper workMapper;

    public List<WorkDto> findAll() {
        return workRepository
                .findAll()
                .stream()
                .map(workMapper::toDto)
                .toList();
    }

    public WorkDto findById(UUID id) {
        Work work = workRepository.findById(id).orElse(null);
        return workMapper.toDto(work);
    }
}
