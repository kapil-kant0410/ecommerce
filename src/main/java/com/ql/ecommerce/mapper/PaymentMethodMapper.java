package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.paymentMethod.PaymentMethodRequestDto;
import com.ql.ecommerce.dto.paymentMethod.PaymentMethodResponseDto;
import com.ql.ecommerce.entity.PaymentMethod;
import com.ql.ecommerce.entity.User;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PaymentMethodMapper {

    public PaymentMethodResponseDto toDto(PaymentMethod paymentMethod){
         return PaymentMethodResponseDto.builder()
                 .type(paymentMethod.getType())
                 .provider(paymentMethod.getProvider())
                 .accountNumber(paymentMethod.getAccountNumber())
                 .expiryDate(paymentMethod.getExpiryDate())
                 .isDefault(paymentMethod.isDefault())
                 .build();
    }

    public List<PaymentMethodResponseDto> toDtoList(List<PaymentMethod> paymentMethods){
        return paymentMethods.stream().map(this::toDto).toList();
    }

    public PaymentMethod toEntity(PaymentMethodRequestDto paymentMethodRequestDto, User user){
        return PaymentMethod.builder()
                .type(paymentMethodRequestDto.getType())
                .provider(paymentMethodRequestDto.getProvider())
                .accountNumber(paymentMethodRequestDto.getAccountNumber())
                .expiryDate(paymentMethodRequestDto.getExpiryDate())
                .user(user)
                .build();

    }



}
