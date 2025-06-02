package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.paymentMethod.PaymentMethodRequestDto;
import com.ql.ecommerce.dto.paymentMethod.PaymentMethodResponseDto;
import com.ql.ecommerce.dto.paymentMethod.UpdatePaymentMethodRequestDto;
import com.ql.ecommerce.exception.Forbidden;
import com.ql.ecommerce.exception.PaymentMethodNotFound;
import com.ql.ecommerce.mapper.PaymentMethodMapper;
import com.ql.ecommerce.repository.PaymentMethodRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.PaymentMethodService;
import com.ql.ecommerce.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.entity.PaymentMethod;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final AuthUtil authUtil;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final ResponseBuilder responseBuilder;

    public PaymentMethodServiceImpl(ResponseBuilder responseBuilder,PaymentMethodMapper paymentMethodMapper,PaymentMethodRepository paymentMethodRepository,AuthUtil authUtil){
        this.authUtil=authUtil;
        this.paymentMethodRepository=paymentMethodRepository;
        this.paymentMethodMapper=paymentMethodMapper;
        this.responseBuilder=responseBuilder;
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserPaymentMethods() {

        User user=authUtil.getCurrentUser();

        List<PaymentMethod> paymentMethods=paymentMethodRepository.findByUserId(user.getId());
        List<PaymentMethodResponseDto> paymentMethodResponseDtos=paymentMethodMapper.toDtoList(paymentMethods);

        return responseBuilder.build("payment methods",paymentMethodResponseDtos,"User payment methods fetched successfully");
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> addPaymentMethod(PaymentMethodRequestDto paymentMethodRequestDto){

        User user=authUtil.getCurrentUser();

        PaymentMethod paymentMethod = paymentMethodMapper.toEntity(paymentMethodRequestDto,user);
        paymentMethodRepository.save(paymentMethod);
        PaymentMethodResponseDto paymentMethodResponseDto=paymentMethodMapper.toDto(paymentMethod);

        return responseBuilder.build("payment methods",paymentMethodResponseDto,"User payment methods fetched successfully");
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePaymentMethod(Long paymentMethodId, UpdatePaymentMethodRequestDto updatePaymentMethodRequestDto) {

        User user=authUtil.getCurrentUser();
        PaymentMethod paymentMethod=paymentMethodRepository.findById(paymentMethodId).orElseThrow(()-> new PaymentMethodNotFound("Payment method not found with this payment method id "+ paymentMethodId));

        if(!paymentMethod.getUser().getId().equals(user.getId())){
            throw new Forbidden("You cannot update another user's payment method");
        }

        paymentMethod.setExpiryDate(updatePaymentMethodRequestDto.getExpiryDate());
        paymentMethodRepository.save(paymentMethod);

        PaymentMethodResponseDto paymentMethodResponseDto=paymentMethodMapper.toDto(paymentMethod);

        return responseBuilder.build("payment method",paymentMethodResponseDto,"Payment method updated successfully");
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> deletePaymentMethod(Long paymentMethodId){

        User user=authUtil.getCurrentUser();
        PaymentMethod paymentMethod=paymentMethodRepository.findById(paymentMethodId).orElseThrow(()-> new PaymentMethodNotFound("Payment method not found with this payment method id "+ paymentMethodId));

        if(!paymentMethod.getUser().getId().equals(user.getId())){
            throw new Forbidden("You cannot delete another user's payment method");
        }

        paymentMethodRepository.delete(paymentMethod);
        PaymentMethodResponseDto paymentMethodResponseDto=paymentMethodMapper.toDto(paymentMethod);

        return responseBuilder.build("payment method",paymentMethodResponseDto,"Payment method deleted successfully");
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> markAsDefaultPaymentMethod(Long paymentMethodId){

        User user=authUtil.getCurrentUser();
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new PaymentMethodNotFound("Payment method not found with id: " + paymentMethodId));

        if(paymentMethod.isDefault()){
            throw new IllegalArgumentException("This payment method is already set as default.");
        }

        if (!paymentMethod.getUser().getId().equals(user.getId())) {
            throw new Forbidden("You cannot update another user's payment method");
        }

        List<PaymentMethod> userPaymentMethods = paymentMethodRepository.findByUserId(user.getId());
        for (PaymentMethod pm : userPaymentMethods) {
            if (Boolean.TRUE.equals(pm.isDefault())) {
                pm.setDefault(false);
                paymentMethodRepository.save(pm);
            }
        }

        paymentMethod.setDefault(true);
        paymentMethodRepository.save(paymentMethod);

        PaymentMethodResponseDto paymentMethodResponseDto = paymentMethodMapper.toDto(paymentMethod);

        return responseBuilder.build("Payment method",paymentMethodResponseDto,"Default payment method updated successfully");
    }

}
