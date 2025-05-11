package yuriy.dev.carfixbackend.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuriy.dev.carfixbackend.dto.OrderDto;
import yuriy.dev.carfixbackend.dto.enums.Status;
import yuriy.dev.carfixbackend.mapper.CarMapper;
import yuriy.dev.carfixbackend.mapper.OrderMapper;
import yuriy.dev.carfixbackend.model.Car;
import yuriy.dev.carfixbackend.model.Order;
import yuriy.dev.carfixbackend.model.Work;
import yuriy.dev.carfixbackend.repository.CarRepository;
import yuriy.dev.carfixbackend.repository.OrderRepository;
import yuriy.dev.carfixbackend.repository.WorkRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CarMapper carMapper;
    private final OrderMapper orderMapper;
    private final WorkRepository workRepository;
    private final CarRepository carRepository;
    private final EmailService emailService;

    public List<OrderDto> findAllOrders(UUID userId) {
        List<Work> works = workRepository.findAllWorksWithPricesForEveryOrder();
        List<Order> orders = userId == null ? orderRepository.findAll() : orderRepository
                .findAll()
                .stream()
                .filter(order -> order.getUser().getId().equals(userId))
                .toList();
        return getOrderDtos(works, orders);
    }

    public List<LocalDateTime> findOrderDates() {
        return orderRepository.findOrderDates();
    }

    @Transactional
    public OrderDto addOrder(OrderDto orderDto) {
        Order order = orderMapper.toOrder(orderDto);
        Car car = carRepository.findByVinNumber(orderDto.carDto().vinNumber()).orElse(null);
        if (car != null) {
            order.setCar(car);
        } else {
            Car savedCar = carRepository.save(carMapper.toCar(orderDto.carDto()));
            order.setCar(savedCar);
        }
        if(orderDto.works().isEmpty()){
            order.setWorks(List.of(workRepository.findByWorkName("Диагностика автомобиля")));
        }
        order.setPrice(order.getWorks().stream().map(Work::getWorkPrice).reduce(BigDecimal.valueOf(0), BigDecimal::add));
        Order savedOrder =  orderRepository.save(order);
        String message = "Заказ на обслуживание автомобиля "
                + savedOrder.getCar().getModel().getMark().getMarkName()
                + " " + savedOrder.getCar().getModel().getModelName()
                + " успешно сформирован и имеет статус " + savedOrder.getStatus().getDisplayName();
        emailService.sendEmailAsync(
                savedOrder.getUser().getUsername(),
                "Заказ на обслуживание сформирован",
                 message);
        return orderMapper.toDto(savedOrder);
    }

    public OrderDto updateOrder(UUID id, OrderDto orderDto) {
        Order order = orderRepository.findById(id).orElse(null);
        Order updatedOrder = null;
        if (order != null) {
            order.setPrice(orderDto.price());
            order.setOrderDate(orderDto.orderDate());
            order.setStatus(orderDto.status());
            order.setDescription(orderDto.description());
            updatedOrder = orderRepository.save(order);
        }
        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public OrderDto updateOrderStatus(UUID id, Status status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);
            emailService.sendEmailAsync(updatedOrder.getUser().getUsername(),"Статус заказа изменен", "Статус заказа: " + updatedOrder.getStatus().getDisplayName());
            return orderMapper.toDto(updatedOrder);
        }
        return null;
    }

    public void deleteOrder(UUID id) {
        orderRepository.deleteById(id);
    }

    public OrderDto findOrderById(UUID id) {
        return orderRepository.findById(id).map(orderMapper::toDto).orElse(null);
    }

    private List<OrderDto> getOrderDtos(List<Work> works, List<Order> orders) {
        Map<UUID, Work> workById = works.stream()
                .collect(Collectors.toMap(Work::getId, work -> work));
        orders.stream()
                .filter(order -> order.getWorks() != null && !order.getWorks().isEmpty())
                .forEach(order -> {
                    List<Work> updatedWorks = order.getWorks().stream()
                            .map(work -> workById.getOrDefault(work.getId(), work))
                            .collect(Collectors.toList());
                    order.setWorks(updatedWorks);
                });
        return orders.stream().map(orderMapper::toDto).toList();
    }
}
