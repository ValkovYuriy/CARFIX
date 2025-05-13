package yuriy.dev.carfixbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuriy.dev.carfixbackend.dto.PopularWorkDto;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.mapper.PopularWorkMapper;
import yuriy.dev.carfixbackend.mapper.WorkMapper;
import yuriy.dev.carfixbackend.model.Work;
import yuriy.dev.carfixbackend.model.WorkPrice;
import yuriy.dev.carfixbackend.repository.WorkPriceRepository;
import yuriy.dev.carfixbackend.repository.WorkRepository;
import yuriy.dev.carfixbackend.util.ImageConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;
    private final PopularWorkMapper popularWorkMapper;
    private final WorkMapper workMapper;
    private final WorkPriceRepository workPriceRepository;

    public List<WorkDto> findAll() {
        return workRepository
                .findAll()
                .stream()
                .map(workMapper::toDto)
                .toList();
    }

    public List<PopularWorkDto> findMostPopularWorksOfTheYear(){
        return workRepository.findMostPopularWorksOfTheYear()
                .stream()
                .map(popularWorkMapper::toPopularWorkDto)
                .toList();
    }

    public WorkDto findById(UUID id) {
        Work work = workRepository.findByIdWithLatestPrice(id).orElse(null);
        return workMapper.toDto(work);
    }

    public WorkDto createWork(WorkDto workDto){
        Work work = workMapper.toWork(workDto);
        workRepository.save(work);
        workPriceRepository.save(WorkPrice.builder()
                .price(workDto.workPrice())
                .date(LocalDateTime.now())
                .work(work)
                .build());
        return workMapper.toDto(work);
    }
    public WorkDto updateWork(UUID id,WorkDto workDto) {
        Work work = workRepository.findByIdWithLatestPrice(id).orElseThrow(() -> new RuntimeException("Work not found"));
        work.setName(workDto.name());
        work.setDescription(workDto.description());
        work.setImage(ImageConverter.toByteArray(workDto.imageBase64()));
        workPriceRepository.save(WorkPrice.builder()
                        .work(work)
                        .price(workDto.workPrice())
                        .date(LocalDateTime.now())
                .build());
        return workMapper.toDto(workRepository.save(work));
    }


    @Transactional
    public void deleteWork(UUID id) {
        workPriceRepository.deleteAllByWorkId(id);
        workRepository.deleteById(id);
    }

}
