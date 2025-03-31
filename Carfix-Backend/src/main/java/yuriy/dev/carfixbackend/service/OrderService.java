package yuriy.dev.carfixbackend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.OrderDto;
import yuriy.dev.carfixbackend.mapper.OrderMapper;
import yuriy.dev.carfixbackend.model.Order;
import yuriy.dev.carfixbackend.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    public List<OrderDto> findAllOrders(){
        return orderRepository
                .findAll()
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    public OrderDto addOrder(OrderDto orderDto){
        Order order = orderMapper.toOrder(orderDto);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto updateOrder(UUID id, OrderDto orderDto){
        Order order = orderRepository.findById(id).orElse(null);
        Order updatedOrder = null;
        if(order != null){
            order.setPrice(orderDto.price());
            order.setOrderDate(orderDto.orderDate());
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
