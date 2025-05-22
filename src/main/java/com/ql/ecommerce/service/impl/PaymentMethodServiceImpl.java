package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.paymentMethod.PaymentMethodRequestDto;
import com.ql.ecommerce.dto.paymentMethod.PaymentMethodResponseDto;
import com.ql.ecommerce.exception.Forbidden;
import com.ql.ecommerce.exception.PaymentMethodNotFound;
import com.ql.ecommerce.mapper.PaymentMethodMapper;
import com.ql.ecommerce.repository.PaymentMethodRepository;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.PaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.entity.PaymentMethod;
import com.ql.ecommerce.exception.UserNotFound;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodServiceImpl(PaymentMethodMapper paymentMethodMapper,PaymentMethodRepository paymentMethodRepository,UserRepository userRepository,AuthUtil authUtil){
        this.authUtil=authUtil;
        this.userRepository=userRepository;
        this.paymentMethodRepository=paymentMethodRepository;
        this.paymentMethodMapper=paymentMethodMapper;
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserPaymentMethods() {

        String email = authUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with this email: " + email));

        List<PaymentMethod> paymentMethods=paymentMethodRepository.findByUserId(user.getId());
        List<PaymentMethodResponseDto> paymentMethodResponseDtos=paymentMethodMapper.toDtoList(paymentMethods);

        Map<String, Object> data = new HashMap<>();
        data.put("payment methods", paymentMethodResponseDtos);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "User payment methods fetched successfully"
        ));
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> addPaymentMethod(PaymentMethodRequestDto paymentMethodRequestDto){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()->new UserNotFound("User not found with this email"+email));

        PaymentMethod paymentMethod = paymentMethodMapper.toEntity(paymentMethodRequestDto,user);
        paymentMethodRepository.save(paymentMethod);
        PaymentMethodResponseDto paymentMethodResponseDto=paymentMethodMapper.toDto(paymentMethod);

        Map<String, Object> data = new HashMap<>();
        data.put("payment methods", paymentMethodResponseDto);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "User payment methods fetched successfully"
        ));

    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePaymentMethod(Long paymentMethodId, PaymentMethodRequestDto paymentMethodRequestDto) {

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email "+email));
        PaymentMethod paymentMethod=paymentMethodRepository.findById(paymentMethodId).orElseThrow(()-> new PaymentMethodNotFound("Payment method not found with this payment method id "+ paymentMethodId));

        if(!paymentMethod.getUser().getId().equals(user.getId())){
            throw new Forbidden("You cannot update another user's payment method");
        }

        paymentMethod.setType(paymentMethodRequestDto.getType());
        paymentMethod.setExpiryDate(paymentMethod.getExpiryDate());

        paymentMethodRepository.save(paymentMethod);

        PaymentMethodResponseDto paymentMethodResponseDto=paymentMethodMapper.toDto(paymentMethod);

        Map<String, Object> data = new HashMap<>();
        data.put("payment method", paymentMethodResponseDto);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "Payment method updated successfully"
        ));

    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> deletePaymentMethod(Long paymentMethodId){
        String email= authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email "+email));
        PaymentMethod paymentMethod=paymentMethodRepository.findById(paymentMethodId).orElseThrow(()-> new PaymentMethodNotFound("Payment method not found with this payment method id "+ paymentMethodId));

        if(!paymentMethod.getUser().getId().equals(user.getId())){
            throw new Forbidden("You cannot delete another user's payment method");
        }

        paymentMethodRepository.delete(paymentMethod);
        PaymentMethodResponseDto paymentMethodResponseDto=paymentMethodMapper.toDto(paymentMethod);

        Map<String, Object> data = new HashMap<>();
        data.put("payment method", paymentMethodResponseDto);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "Payment method deleted successfully"
        ));

    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> markAsDefaultPaymentMethod(Long paymentMethodId){

        String email= authUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with this email: " + email));
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new PaymentMethodNotFound("Payment method not found with id: " + paymentMethodId));

        if(paymentMethod.isDefault()){
            return ResponseEntity.ok(ApiResponse.success(
                    HttpStatus.OK.value(),
                    null,
                    "This payment method is already set as default."
            ));
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

        Map<String, Object> data = new HashMap<>();
        data.put("default payment method", paymentMethodResponseDto);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "Default payment method updated successfully"
        ));

    }



}
