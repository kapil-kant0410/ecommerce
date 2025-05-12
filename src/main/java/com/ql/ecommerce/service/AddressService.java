package com.ql.ecommerce.service;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.address.response.AddressDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface AddressService {

    ResponseEntity<ApiResponse<Map<String, List<AddressDto>>>> getCurrentUserAddresses();
    ResponseEntity<ApiResponse<Map<String, AddressDto>>> addAddressForCurrentUser(AddressDto addressDto);
    ResponseEntity<ApiResponse<Map<String,AddressDto>>> getAddressForCurrentUser(Long addressId);
    ResponseEntity<ApiResponse<Map<String, AddressDto>>> updateAddressForCurrentUser( Long addressId, AddressDto addressDto);
    ResponseEntity<ApiResponse<Map<String, AddressDto>>> deleteAddressForCurrentUser(Long addressId);
    ResponseEntity<ApiResponse<Map<String, AddressDto>>> setDefaultAddressForCurrentUser(Long addressId);
}
