package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.address.response.AddressDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AddressService {

    ResponseEntity<ApiResponse<Map<String,Object>>> getCurrentUserAddresses();
    ResponseEntity<ApiResponse<Map<String,Object>>> addAddressForCurrentUser(AddressDto addressDto);
    ResponseEntity<ApiResponse<Map<String,Object>>> getAddressForCurrentUser(Long addressId);
    ResponseEntity<ApiResponse<Map<String,Object>>> updateAddressForCurrentUser( Long addressId, AddressDto addressDto);
    ResponseEntity<ApiResponse<Map<String,Object>>> deleteAddressForCurrentUser(Long addressId);
    ResponseEntity<ApiResponse<Map<String,Object>>> setDefaultAddressForCurrentUser(Long addressId);
}
