package yuriy.dev.carfixbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yuriy.dev.carfixbackend.auth.Role;
import yuriy.dev.carfixbackend.dto.CarDto;
import yuriy.dev.carfixbackend.dto.ModelDto;
import yuriy.dev.carfixbackend.dto.OrderDto;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.dto.enums.Status;
import yuriy.dev.carfixbackend.mapper.CarMapper;
import yuriy.dev.carfixbackend.mapper.OrderMapper;
import yuriy.dev.carfixbackend.model.Car;
import yuriy.dev.carfixbackend.model.Mark;
import yuriy.dev.carfixbackend.model.Model;
import yuriy.dev.carfixbackend.model.Order;
import yuriy.dev.carfixbackend.model.User;
import yuriy.dev.carfixbackend.model.Work;
import yuriy.dev.carfixbackend.repository.CarRepository;
import yuriy.dev.carfixbackend.repository.OrderRepository;
import yuriy.dev.carfixbackend.repository.WorkRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CarMapper carMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private WorkRepository workRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID CAR_ID = UUID.randomUUID();

    private User buildUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("user@mail.ru")
                .role(Role.ROLE_USER)
                .firstName("Иван")
                .lastName("Иванов")
                .phoneNumber("+79990001122")
                .build();
    }

    private Car buildCar() {
        Mark mark = Mark.builder().id(UUID.randomUUID()).markName("Acura").build();
        Model model = Model.builder().id(UUID.randomUUID()).modelName("CDX").mark(mark).build();
        return Car.builder()
                .id(CAR_ID)
                .govNumber("А123ВС163")
                .vinNumber("1HGCM82633A123456")
                .model(model)
                .build();
    }

    private Work buildWork(String name, String price) {
        return Work.builder()
                .id(UUID.randomUUID())
                .name(name)
                .workPrice(new BigDecimal(price))
                .build();
    }

    @Test
    void findAllOrders_nullUserId_returnsAllOrders() {
        Order order = Order.builder().id(ORDER_ID).works(List.of()).build();
        OrderDto dto = new OrderDto(ORDER_ID, null, null, null, null, Status.PENDING, "d", List.of());

        when(workRepository.findAllWorksWithPricesForEveryOrder()).thenReturn(List.of());
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(dto);

        List<OrderDto> result = orderService.findAllOrders(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(ORDER_ID);
    }

    @Test
    void findAllOrders_withUserId_filtersOrdersOfOtherUsers() {
        User owner = buildUser();
        User other = buildUser();
        Order own = Order.builder().id(ORDER_ID).user(owner).works(List.of()).build();
        Order foreign = Order.builder().id(UUID.randomUUID()).user(other).works(List.of()).build();
        OrderDto dto = new OrderDto(ORDER_ID, null, null, null, null, Status.PENDING, "d", List.of());

        when(workRepository.findAllWorksWithPricesForEveryOrder()).thenReturn(List.of());
        when(orderRepository.findAll()).thenReturn(List.of(own, foreign));
        when(orderMapper.toDto(own)).thenReturn(dto);

        List<OrderDto> result = orderService.findAllOrders(owner.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(ORDER_ID);
    }

    @Test
    void findOrderDates_delegatesToRepository() {
        List<LocalDateTime> dates = List.of(LocalDateTime.of(2026, 11, 20, 10, 0));
        when(orderRepository.findOrderDates()).thenReturn(dates);

        assertThat(orderService.findOrderDates()).isEqualTo(dates);
    }

    @Test
    void addOrder_withNewCar_savesCarAndCalculatesTotalPrice() {
        CarDto carDto = new CarDto(null, "А123ВС163", "1HGCM82633A123456", null);
        WorkDto workDto1 = new WorkDto(UUID.randomUUID(), "Замена масла", null, new BigDecimal("1200"), null);
        WorkDto workDto2 = new WorkDto(UUID.randomUUID(), "Диагностика", null, new BigDecimal("800"), null);
        LocalDateTime utcDate = LocalDateTime.of(2026, 11, 20, 12, 0);
        OrderDto request = new OrderDto(null, carDto, null, utcDate, null, Status.PENDING, "Тестовый заказ", List.of(workDto1, workDto2));

        Work work1 = buildWork("Замена масла", "1200");
        Work work2 = buildWork("Диагностика", "800");
        Order mappedOrder = Order.builder()
                .orderDate(utcDate)
                .status(Status.PENDING)
                .description("Тестовый заказ")
                .works(List.of(work1, work2))
                .build();
        Car savedCar = buildCar();
        User user = buildUser();

        when(orderMapper.toOrder(request)).thenReturn(mappedOrder);
        when(userService.getCurrentUser()).thenReturn(user);
        when(carRepository.findByVinNumber("1HGCM82633A123456")).thenReturn(Optional.empty());
        when(carMapper.toCar(carDto)).thenReturn(Car.builder().vinNumber("1HGCM82633A123456").build());
        when(carRepository.save(any(Car.class))).thenReturn(savedCar);
        when(orderRepository.save(mappedOrder)).thenReturn(mappedOrder);
        when(orderMapper.toDto(mappedOrder)).thenReturn(
                new OrderDto(ORDER_ID, carDto, null, null, new BigDecimal("2000"), Status.PENDING, "Тестовый заказ", List.of()));

        OrderDto result = orderService.addOrder(request, -240);

        // Цена заказа вычисляется как сумма цен работ, а не берётся из запроса
        assertThat(mappedOrder.getPrice()).isEqualByComparingTo("2000");
        // Дата из UTC приводится к часовому поясу пользователя (UTC+4 при timezoneOffset = -240)
        assertThat(mappedOrder.getOrderDate()).isEqualTo(LocalDateTime.of(2026, 11, 20, 16, 0));
        assertThat(mappedOrder.getCar()).isEqualTo(savedCar);
        assertThat(mappedOrder.getUser()).isEqualTo(user);
        verify(carRepository).save(any(Car.class));
        verify(emailService).sendEmailAsync(eq(user.getUsername()), anyString(), anyString());
        assertThat(result.id()).isEqualTo(ORDER_ID);
    }

    @Test
    void addOrder_withExistingCar_doesNotCreateDuplicateCar() {
        Car existingCar = buildCar();
        CarDto carDto = new CarDto(CAR_ID, "А123ВС163", "1HGCM82633A123456", null);
        WorkDto workDto = new WorkDto(UUID.randomUUID(), "Замена масла", null, new BigDecimal("1200"), null);
        OrderDto request = new OrderDto(null, carDto, null, LocalDateTime.of(2026, 11, 20, 12, 0),
                null, Status.PENDING, "Заказ", List.of(workDto));

        Work work = buildWork("Замена масла", "1200");
        Order mappedOrder = Order.builder()
                .orderDate(LocalDateTime.of(2026, 11, 20, 12, 0))
                .status(Status.PENDING)
                .works(List.of(work))
                .build();

        when(orderMapper.toOrder(request)).thenReturn(mappedOrder);
        when(userService.getCurrentUser()).thenReturn(buildUser());
        when(carRepository.findByVinNumber("1HGCM82633A123456")).thenReturn(Optional.of(existingCar));
        when(orderRepository.save(mappedOrder)).thenReturn(mappedOrder);
        when(orderMapper.toDto(mappedOrder)).thenReturn(
                new OrderDto(ORDER_ID, carDto, null, null, null, Status.PENDING, "Заказ", List.of()));

        orderService.addOrder(request, 0);

        assertThat(mappedOrder.getCar()).isEqualTo(existingCar);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void addOrder_withoutWorks_assignsDefaultDiagnostics() {
        Car existingCar = buildCar();
        CarDto carDto = new CarDto(CAR_ID, "А123ВС163", "1HGCM82633A123456", null);
        OrderDto request = new OrderDto(null, carDto, null, LocalDateTime.of(2026, 11, 20, 12, 0),
                null, Status.PENDING, "Без услуг", List.of());

        Work diagnostics = buildWork("Диагностика автомобиля", "500");
        Order mappedOrder = Order.builder()
                .orderDate(LocalDateTime.of(2026, 11, 20, 12, 0))
                .status(Status.PENDING)
                .works(List.of())
                .build();

        when(orderMapper.toOrder(request)).thenReturn(mappedOrder);
        when(userService.getCurrentUser()).thenReturn(buildUser());
        when(carRepository.findByVinNumber("1HGCM82633A123456")).thenReturn(Optional.of(existingCar));
        when(workRepository.findByWorkName("Диагностика автомобиля")).thenReturn(diagnostics);
        when(orderRepository.save(mappedOrder)).thenReturn(mappedOrder);
        when(orderMapper.toDto(mappedOrder)).thenReturn(
                new OrderDto(ORDER_ID, carDto, null, null, null, Status.PENDING, "Без услуг", List.of()));

        orderService.addOrder(request, 0);

        assertThat(mappedOrder.getWorks()).containsExactly(diagnostics);
        assertThat(mappedOrder.getPrice()).isEqualByComparingTo("500");
    }

    @Test
    void updateOrder_existingOrder_updatesAndReturnsDto() {
        Order order = Order.builder().id(ORDER_ID).status(Status.PENDING).build();
        OrderDto update = new OrderDto(null, null, null, LocalDateTime.of(2026, 12, 1, 10, 0),
                new BigDecimal("3000"), Status.ACCEPTED, "Обновлён", List.of());
        OrderDto expected = new OrderDto(ORDER_ID, null, null, update.orderDate(),
                new BigDecimal("3000"), Status.ACCEPTED, "Обновлён", List.of());

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expected);

        OrderDto result = orderService.updateOrder(ORDER_ID, update);

        assertThat(order.getPrice()).isEqualByComparingTo("3000");
        assertThat(order.getStatus()).isEqualTo(Status.ACCEPTED);
        assertThat(order.getDescription()).isEqualTo("Обновлён");
        assertThat(result.status()).isEqualTo(Status.ACCEPTED);
    }

    @Test
    void updateOrder_notFound_returnsNull() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderDto result = orderService.updateOrder(ORDER_ID,
                new OrderDto(null, null, null, null, BigDecimal.TEN, Status.ACCEPTED, "x", List.of()));

        assertThat(result).isNull();
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_existingOrder_savesStatusAndNotifiesUser() {
        User user = buildUser();
        Order order = Order.builder().id(ORDER_ID).status(Status.PENDING).user(user).build();
        OrderDto expected = new OrderDto(ORDER_ID, null, null, null, null, Status.ACCEPTED, "d", List.of());

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expected);

        OrderDto result = orderService.updateOrderStatus(ORDER_ID, Status.ACCEPTED);

        assertThat(order.getStatus()).isEqualTo(Status.ACCEPTED);
        verify(emailService).sendEmailAsync(eq(user.getUsername()), anyString(), anyString());
        assertThat(result.status()).isEqualTo(Status.ACCEPTED);
    }

    @Test
    void updateOrderStatus_notFound_returnsNullWithoutNotification() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderDto result = orderService.updateOrderStatus(ORDER_ID, Status.REJECTED);

        assertThat(result).isNull();
        verify(emailService, never()).sendEmailAsync(anyString(), anyString(), anyString());
    }

    @Test
    void deleteOrder_delegatesToRepository() {
        orderService.deleteOrder(ORDER_ID);

        verify(orderRepository).deleteById(ORDER_ID);
    }

    @Test
    void findOrderById_returnsDto() {
        Order order = Order.builder().id(ORDER_ID).build();
        OrderDto dto = new OrderDto(ORDER_ID, null, null, null, null, Status.PENDING, "d", List.of());

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(dto);

        assertThat(orderService.findOrderById(ORDER_ID).id()).isEqualTo(ORDER_ID);
    }
}
