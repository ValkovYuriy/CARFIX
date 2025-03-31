package yuriy.dev.carfixbackend.controller.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuriy.dev.carfixbackend.dto.OrderDto;
import yuriy.dev.carfixbackend.dto.response.ApiResponseDto;
import yuriy.dev.carfixbackend.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> findAllOrders(){
        List<OrderDto> list = orderService.findAllOrders();
        return ResponseEntity.ok(new ApiResponseDto<>("ok",list));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OrderDto>> findOrderById(@PathVariable UUID id){
        OrderDto dto = orderService.findOrderById(id);
        return ResponseEntity.ok(new ApiResponseDto<>("ok",dto));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponseDto<OrderDto>> addOrder(@RequestBody OrderDto dto){
        OrderDto addedDto = orderService.addOrder(dto);
        return ResponseEntity.ok(new ApiResponseDto<>("ok",addedDto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OrderDto>> updateOrder(@PathVariable UUID id, @RequestBody OrderDto dto){
        OrderDto updatedDto = orderService.updateOrder(id,dto);
        return ResponseEntity.ok(new ApiResponseDto<>("ok",updatedDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
    
}
