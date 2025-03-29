package yuriy.dev.carfixbackend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.WorkPriceDto;
import yuriy.dev.carfixbackend.mapper.WorkPriceMapper;
import yuriy.dev.carfixbackend.model.WorkPrice;
import yuriy.dev.carfixbackend.repository.WorkPriceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkPriceService {

    private final WorkPriceRepository workPriceRepository;
    private final WorkPriceMapper workPriceMapper;

    public List<WorkPriceDto> findAllWorkPrices(){
        List<WorkPrice> workPrices = workPriceRepository.findAll();
        return workPrices
                .stream()
                .map(workPriceMapper::toWorkPriceDto)
                .toList();
    }
}
