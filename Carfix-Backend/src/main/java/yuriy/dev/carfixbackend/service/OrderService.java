package yuriy.dev.carfixbackend.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.OrderDto;
import yuriy.dev.carfixbackend.mapper.CarMapper;
import yuriy.dev.carfixbackend.mapper.OrderMapper;
import yuriy.dev.carfixbackend.model.Order;
import yuriy.dev.carfixbackend.model.Work;
import yuriy.dev.carfixbackend.repository.CarRepository;
import yuriy.dev.carfixbackend.repository.OrderRepository;
import yuriy.dev.carfixbackend.repository.WorkRepository;

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

    public List<OrderDto> findAllOrders(){
        List<Work> works = workRepository.findAllWorksWithPricesForEveryOrder();
        List<Order> orders = orderRepository.findAll();
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

    public OrderDto addOrder(OrderDto orderDto){
        Order order = orderMapper.toOrder(orderDto);
        carRepository.findByVinNumber(orderDto.carDto().vinNumber()).ifPresentOrElse(order::setCar, () -> orderRepository.save(order));
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto updateOrder(UUID id, OrderDto orderDto){
        Order order = orderRepository.findById(id).orElse(null);
        Order updatedOrder = null;
        if(order != null){
            order.setPrice(orderDto.price());
            order.setOrderDate(orderDto.orderDate());
            order.setStatus(orderDto.status());
            order.setDescription(orderDto.description());
            updatedOrder = orderRepository.save(order);
        }
        return orderMapper.toDto(updatedOrder);
    }

    public void deleteOrder(UUID id){
        orderRepository.deleteById(id);
    }

    public OrderDto findOrderById(UUID id){
        return orderRepository.findById(id).map(orderMapper::toDto).orElse(null);
    }
}
