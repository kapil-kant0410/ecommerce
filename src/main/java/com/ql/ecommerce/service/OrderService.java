package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.order.PlaceOrderRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface OrderService {

    ResponseEntity<ApiResponse<Map<String, Object>>> placeOrder(PlaceOrderRequestDto placeOrderRequestDto);
    ResponseEntity<ApiResponse<Map<String,Object>>> getMyOrders();
    ResponseEntity<ApiResponse<Map<String,Object>>> getOrderDetails(Long orderId);
    ResponseEntity<ApiResponse<Map<String,Object>>> cancelOrder(Long orderId);
}
