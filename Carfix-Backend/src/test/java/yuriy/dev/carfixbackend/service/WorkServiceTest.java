package yuriy.dev.carfixbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.mapper.PopularWorkMapper;
import yuriy.dev.carfixbackend.mapper.WorkMapper;
import yuriy.dev.carfixbackend.model.Work;
import yuriy.dev.carfixbackend.model.WorkPrice;
import yuriy.dev.carfixbackend.repository.WorkPriceRepository;
import yuriy.dev.carfixbackend.repository.WorkRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkServiceTest {

    @Mock
    private WorkRepository workRepository;
    @Mock
    private PopularWorkMapper popularWorkMapper;
    @Mock
    private WorkMapper workMapper;
    @Mock
    private WorkPriceRepository workPriceRepository;

    @InjectMocks
    private WorkService workService;

    private static final UUID WORK_ID = UUID.randomUUID();

    private Work buildWork() {
        return Work.builder()
                .id(WORK_ID)
                .name("Замена масла")
                .description("Замена моторного масла")
                .workPrice(new BigDecimal("1200"))
                .build();
    }

    @Test
    void findAll_returnsMappedDtos() {
        Work work = buildWork();
        WorkDto dto = new WorkDto(WORK_ID, "Замена масла", "Замена моторного масла", new BigDecimal("1200"), null);

        when(workRepository.findAll()).thenReturn(List.of(work));
        when(workMapper.toDto(work)).thenReturn(dto);

        List<WorkDto> result = workService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Замена масла");
    }

    @Test
    void findById_returnsDtoWithLatestPrice() {
        Work work = buildWork();
        WorkDto dto = new WorkDto(WORK_ID, "Замена масла", "Замена моторного масла", new BigDecimal("1200"), null);

        when(workRepository.findByIdWithLatestPrice(WORK_ID)).thenReturn(Optional.of(work));
        when(workMapper.toDto(work)).thenReturn(dto);

        WorkDto result = workService.findById(WORK_ID);

        assertThat(result.workPrice()).isEqualByComparingTo("1200");
    }

    @Test
    void createWork_savesWorkAndInitialPrice() {
        WorkDto input = new WorkDto(null, "Диагностика", "Полная диагностика", new BigDecimal("800"), null);
        Work work = Work.builder().name("Диагностика").description("Полная диагностика").build();

        when(workMapper.toWork(input)).thenReturn(work);
        when(workMapper.toDto(work)).thenReturn(
                new WorkDto(WORK_ID, "Диагностика", "Полная диагностика", new BigDecimal("800"), null));

        WorkDto result = workService.createWork(input);

        verify(workRepository).save(work);
        ArgumentCaptor<WorkPrice> priceCaptor = ArgumentCaptor.forClass(WorkPrice.class);
        verify(workPriceRepository).save(priceCaptor.capture());
        assertThat(priceCaptor.getValue().getPrice()).isEqualByComparingTo("800");
        assertThat(priceCaptor.getValue().getWork()).isEqualTo(work);
        assertThat(result.name()).isEqualTo("Диагностика");
    }

    @Test
    void updateWork_existingWork_updatesFieldsAndAppendsPriceHistory() {
        Work work = buildWork();
        WorkDto update = new WorkDto(WORK_ID, "Замена масла PRO", "Новое описание", new BigDecimal("1500"), null);

        when(workRepository.findByIdWithLatestPrice(WORK_ID)).thenReturn(Optional.of(work));
        when(workMapper.toDto(work)).thenReturn(
                new WorkDto(WORK_ID, "Замена масла PRO", "Новое описание", new BigDecimal("1500"), null));

        WorkDto result = workService.updateWork(WORK_ID, update);

        assertThat(work.getName()).isEqualTo("Замена масла PRO");
        assertThat(work.getDescription()).isEqualTo("Новое описание");
        assertThat(work.getWorkPrice()).isEqualByComparingTo("1500");
        verify(workPriceRepository).save(any(WorkPrice.class));
        verify(workRepository).save(work);
        assertThat(result.workPrice()).isEqualByComparingTo("1500");
    }

    @Test
    void updateWork_notFound_throwsException() {
        when(workRepository.findByIdWithLatestPrice(WORK_ID)).thenReturn(Optional.empty());

        WorkDto update = new WorkDto(WORK_ID, "n", "d", BigDecimal.TEN, null);

        assertThatThrownBy(() -> workService.updateWork(WORK_ID, update))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Work not found");
        verify(workRepository, never()).save(any());
    }

    @Test
    void deleteWork_removesPricesAndWork() {
        workService.deleteWork(WORK_ID);

        verify(workPriceRepository).deleteAllByWorkId(WORK_ID);
        verify(workRepository).deleteById(WORK_ID);
    }
}
