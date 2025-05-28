package com.ql.ecommerce.controller;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.address.response.AddressDto;
import com.ql.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService){
        this.addressService=addressService;
    }

    @GetMapping("/me/addresses")
    ResponseEntity<ApiResponse<Map<String,Object>>> getCurrentUserAddresses(){
        return addressService.getCurrentUserAddresses();
    }

    @PostMapping("/me/addresses")
    ResponseEntity<ApiResponse<Map<String, Object>>> addAddressForCurrentUser(@Valid @RequestBody AddressDto addressDto){
            return addressService.addAddressForCurrentUser(addressDto);
    }

    @GetMapping("/me/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAddressForCurrentUser(@PathVariable Long addressId){
           return addressService.getAddressForCurrentUser(addressId);
    }

    @PutMapping("/me/addresses/{addressId}")
    ResponseEntity<ApiResponse<Map<String, Object>>> updateAddressForCurrentUser(@PathVariable Long addressId, @Valid @RequestBody AddressDto addressDto){
          return addressService.updateAddressForCurrentUser(addressId,addressDto);
    }

    @DeleteMapping("/me/addresses/{addressId}")
    ResponseEntity<ApiResponse<Map<String, Object>>> deleteAddressForCurrentUser(@PathVariable Long addressId){
          return addressService.deleteAddressForCurrentUser(addressId);
    }

    @PatchMapping("/me/addresses/{addressId}/set-default")
    public ResponseEntity<ApiResponse<Map<String, Object>>> setDefaultAddressForCurrentUser(@PathVariable Long addressId){
         return addressService.setDefaultAddressForCurrentUser(addressId);
    }

}
