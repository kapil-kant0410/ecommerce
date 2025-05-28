package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.address.response.AddressDto;
import com.ql.ecommerce.entity.Address;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.exception.AddressNotFound;
import com.ql.ecommerce.exception.Unauthorized;
import com.ql.ecommerce.exception.UserNotFound;
import com.ql.ecommerce.mapper.AddressMapper;
import com.ql.ecommerce.repository.AddressRepository;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.AddressService;
import com.ql.ecommerce.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final AuthUtil authUtil;
    private final AddressRepository addressRepository;
    private final ResponseBuilder responseBuilder;

    public AddressServiceImpl(ResponseBuilder responseBuilder,AddressRepository addressRepository,AuthUtil authUtil,AddressMapper addressMapper,UserRepository userRepository){
        this.userRepository=userRepository;
        this.addressMapper=addressMapper;
        this.authUtil=authUtil;
        this.addressRepository=addressRepository;
        this.responseBuilder=responseBuilder;
    }

    // Fetch all addresses belonging to the current authenticated user
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUserAddresses() {

        String email=authUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with email: " + email));

        List<Address> addresses=user.getAddresses();
        List<AddressDto> addressDtoList=addressMapper.toDtoList(addresses);

        return responseBuilder.build("Addresses",addressDtoList,"User addresses fetched successfully");
    }

    // Create and associate a new address with the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String, Object>>> addAddressForCurrentUser(AddressDto addressDto){

        String email=authUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with email: " + email));

        Address address=addressMapper.toEntity(addressDto);
        address.setUser(user);

        Address savedAddress=addressRepository.save(address);
        AddressDto savedAddressDto=addressMapper.toDto(savedAddress);

        return responseBuilder.build("Address",savedAddressDto,"Address added successfully");
    }

    // Fetch the address with the given ID, only if it is associated with the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAddressForCurrentUser(Long addressId){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with email: " + email));
        Address address=addressRepository.findById(addressId).orElseThrow(()-> new AddressNotFound("Address not found with id: " + addressId));

       if(!address.getUser().getId().equals(user.getId())){
           throw new Unauthorized("You are not authorized to view this address");
       }

       AddressDto addressDto=addressMapper.toDto(address);

       return responseBuilder.build("Address",addressDto,"Address fetched successfully");
    }

    // Update the address with the given ID, only if it is owned by the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String,Object>>> updateAddressForCurrentUser( Long addressId, AddressDto addressDto){

        String email=authUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with email: " + email));

        Address existingAddress=addressRepository.findById(addressId).orElseThrow(()-> new AddressNotFound("Address not found with id: " + addressId));

        if(!existingAddress.getUser().getId().equals(user.getId())){
            throw new Unauthorized("You are not authorized to update this address");
        }

        Address updatedAddress=addressMapper.updateAddress(existingAddress,addressDto);
        AddressDto updatedAddressDto=addressMapper.toDto(updatedAddress);

        return responseBuilder.build("Address",updatedAddressDto,"Address updated successfully");
    }

    // Delete the address with the given ID if it belongs to the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String,Object>>> deleteAddressForCurrentUser(Long addressId) {

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with email: " + email));
        Address address=addressRepository.findById(addressId).orElseThrow(()-> new AddressNotFound("Address not found with id: " + addressId));

        if(!address.getUser().getId().equals(user.getId())){
            throw new Unauthorized("You are not authorized to delete this address");
        }

        addressRepository.deleteById(addressId);
        AddressDto addressDto=addressMapper.toDto(address);

        return responseBuilder.build("Address",addressDto,"Address deleted successfully");
    }

     // Update the current user's addresses by marking the specified address as default
     // and resetting the 'isDefault' flag to false for all other addresses
    public ResponseEntity<ApiResponse<Map<String, Object>>> setDefaultAddressForCurrentUser(Long addressId){

        String email=authUtil.getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with email:"+email));
        Address address=addressRepository.findById(addressId).orElseThrow(()->new AddressNotFound("Address not found with id: " + addressId));

        if(!address.getUser().getId().equals(user.getId())){
            throw new Unauthorized("You are not authorized to use this address");
        }

        List<Address> userAddresses=addressRepository.findByUser(user);

        for(Address address1:userAddresses){
            if(Boolean.TRUE.equals(address1.isDefault())){
                address1.setDefault(false);
            }
        }

        address.setDefault(true);
        addressRepository.save(address);

        AddressDto addressDto=addressMapper.toDto(address);

        return responseBuilder.build("Address",addressDto,"Default address set successfully");
    }


}
