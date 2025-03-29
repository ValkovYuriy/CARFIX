package yuriy.dev.carfixbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.MarkDto;
import yuriy.dev.carfixbackend.mapper.MarkMapper;
import yuriy.dev.carfixbackend.model.Mark;
import yuriy.dev.carfixbackend.repository.MarkRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkService {

    private final MarkRepository markRepository;

    private final MarkMapper markMapper;

    public List<MarkDto> findAllMarks(){
        return markRepository
                .findAll()
                .stream()
                .map(markMapper::toDto)
                .toList();
    }

    public MarkDto addMark(MarkDto markDto){
        Mark mark = markMapper.toMark(markDto);
        return markMapper.toDto(markRepository.save(mark));
    }

    public MarkDto updateMark(UUID id, MarkDto markDto){
        Mark mark = markRepository.findById(id).orElse(null);
        MarkDto updatedMark = null;
        if(mark != null){
            mark.setMarkName(markDto.markName());
            mark.setModels(markDto.models());
            updatedMark = markMapper.toDto(markRepository.save(mark));
        }
        return updatedMark;
    }

    public void deleteMark(UUID id){
        markRepository.deleteById(id);
    }

    public MarkDto findMarkById(UUID id) {
        return markRepository.findById(id).map(markMapper::toDto).orElse(null);
    }
}
