package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.paymentMethod.PaymentMethodRequestDto;
import com.ql.ecommerce.dto.paymentMethod.UpdatePaymentMethodRequestDto;
import com.ql.ecommerce.service.PaymentMethodService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService){
        this.paymentMethodService=paymentMethodService;
    }

    @GetMapping()
    ResponseEntity<ApiResponse<Map<String, Object>>> getUserPaymentMethods(){
        return paymentMethodService.getUserPaymentMethods();
    }

    @PostMapping()
    ResponseEntity<ApiResponse<Map<String, Object>>> addPaymentMethod(@Valid @RequestBody PaymentMethodRequestDto paymentMethodRequestDto){
        return paymentMethodService.addPaymentMethod(paymentMethodRequestDto);
    }

    @PutMapping("/{paymentMethodId}")
    ResponseEntity<ApiResponse<Map<String, Object>>> updatePaymentMethod(@PathVariable Long paymentMethodId,@Valid @RequestBody UpdatePaymentMethodRequestDto updatePaymentMethodRequestDto){
        return paymentMethodService.updatePaymentMethod(paymentMethodId,updatePaymentMethodRequestDto);
    }

    @DeleteMapping("/{paymentMethodId}")
    ResponseEntity<ApiResponse<Map<String, Object>>> deletePaymentMethod(@PathVariable Long paymentMethodId){
        return paymentMethodService.deletePaymentMethod(paymentMethodId);
    }

    @PatchMapping("/{paymentMethodId}/set-default")
    ResponseEntity<ApiResponse<Map<String, Object>>> markAsDefaultPaymentMethod(@PathVariable Long paymentMethodId){
        return paymentMethodService.markAsDefaultPaymentMethod(paymentMethodId);
    }


}
