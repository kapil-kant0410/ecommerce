package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.paymentMethod.PaymentMethodRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface PaymentMethodService {
     ResponseEntity<ApiResponse<Map<String, Object>>> getUserPaymentMethods();
     ResponseEntity<ApiResponse<Map<String, Object>>> addPaymentMethod(PaymentMethodRequestDto paymentMethodRequestDto);
     ResponseEntity<ApiResponse<Map<String, Object>>> updatePaymentMethod(Long paymentMethodId, PaymentMethodRequestDto paymentMethodRequestDto);
     ResponseEntity<ApiResponse<Map<String, Object>>> deletePaymentMethod(Long paymentMethodId);
     ResponseEntity<ApiResponse<Map<String, Object>>> markAsDefaultPaymentMethod(Long paymentMethodId);
}
