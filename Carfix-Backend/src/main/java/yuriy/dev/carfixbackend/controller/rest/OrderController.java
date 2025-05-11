package yuriy.dev.carfixbackend.controller.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuriy.dev.carfixbackend.dto.OrderDto;
import yuriy.dev.carfixbackend.dto.enums.Status;
import yuriy.dev.carfixbackend.dto.response.ApiResponseDto;
import yuriy.dev.carfixbackend.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> findAllOrders(@RequestParam(required = false) UUID userId){
        List<OrderDto> list = orderService.findAllOrders(userId);
        return ResponseEntity.ok(new ApiResponseDto<>("OK",list));
    }

    @GetMapping("/dates")
    public ResponseEntity<ApiResponseDto<List<LocalDateTime>>> findAllOrderDates(){
        List<LocalDateTime> list = orderService.findOrderDates();
        return ResponseEntity.ok(new ApiResponseDto<>("OK",list));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OrderDto>> findOrderById(@PathVariable UUID id){
        OrderDto dto = orderService.findOrderById(id);
        return ResponseEntity.ok(new ApiResponseDto<>("OK",dto));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponseDto<OrderDto>> addOrder(@RequestBody OrderDto dto){
        OrderDto addedDto = orderService.addOrder(dto);
        return ResponseEntity.ok(new ApiResponseDto<>("OK",addedDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OrderDto>> updateOrderStatus(@PathVariable UUID id, @RequestBody Status status){
        OrderDto updatedDto = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(new ApiResponseDto<>("OK",updatedDto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OrderDto>> updateOrder(@PathVariable UUID id, @RequestBody OrderDto dto){
        OrderDto updatedDto = orderService.updateOrder(id,dto);
        return ResponseEntity.ok(new ApiResponseDto<>("OK",updatedDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
    
}
