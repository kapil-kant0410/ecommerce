package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.order.PlaceOrderRequestDto;
import com.ql.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService=orderService;
    }

    @PostMapping
    ResponseEntity<ApiResponse<Map<String, Object>>> placeOrder(@RequestBody @Valid PlaceOrderRequestDto placeOrderRequestDto){
        return orderService.placeOrder(placeOrderRequestDto);
    }

    @GetMapping
    ResponseEntity<ApiResponse<Map<String,Object>>> getMyOrders(){
        return orderService.getMyOrders();
    }

    @GetMapping("/{orderId}")
    ResponseEntity<ApiResponse<Map<String,Object>>> getOrderDetails(@PathVariable Long orderId){
        return orderService.getOrderDetails(orderId);
    }

    @PostMapping("/{orderId}/cancel")
    ResponseEntity<ApiResponse<Map<String,Object>>> cancelOrder(@PathVariable Long orderId){
        return orderService.cancelOrder(orderId);
    }

}
