package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.address.response.AddressDto;
import com.ql.ecommerce.entity.Address;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.exception.AddressNotFoundException;
import com.ql.ecommerce.exception.UnauthorizedException;
import com.ql.ecommerce.exception.UserNotFoundException;
import com.ql.ecommerce.mapper.AddressMapper;
import com.ql.ecommerce.repository.AddressRepository;
import com.ql.ecommerce.repository.UserRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.AddressService;
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

    public AddressServiceImpl(AddressRepository addressRepository,AuthUtil authUtil,AddressMapper addressMapper,UserRepository userRepository){
        this.userRepository=userRepository;
        this.addressMapper=addressMapper;
        this.authUtil=authUtil;
        this.addressRepository=addressRepository;
    }

    // Fetch all addresses belonging to the current authenticated user
    public ResponseEntity<ApiResponse<Map<String, List<AddressDto>>>> getCurrentUserAddresses() {

        String email=authUtil.getCurrentUserEmail();

        if(email==null){
          throw new UnauthorizedException("Unauthorized access");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        List<Address> addresses=user.getAddresses();
        List<AddressDto> addressDtoList=addressMapper.toDtoList(addresses);

        Map<String, List<AddressDto>> data = new HashMap<>();
        data.put("addresses", addressDtoList);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                "User addresses fetched successfully"
        ));
    }

    // Create and associate a new address with the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String, AddressDto>>> addAddressForCurrentUser(AddressDto addressDto){

        String email=authUtil.getCurrentUserEmail();

        if(email==null){
            throw new UnauthorizedException("Unauthorized access");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Address address=addressMapper.toEntity(addressDto);
        address.setUser(user);

       Address savedAddress=addressRepository.save(address);
       AddressDto savedAddressDto=addressMapper.toDto(savedAddress);

        Map<String, AddressDto> data = new HashMap<>();
        data.put("address", savedAddressDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        data,
                        "Address added successfully"
                )
        );
    }

    // Fetch the address with the given ID, only if it is associated with the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String,AddressDto>>> getAddressForCurrentUser(Long addressId){

        String email=authUtil.getCurrentUserEmail();
        if(email==null){
            throw new UnauthorizedException("Unauthorized access");
        }

        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User not found with email: " + email));
        Address address=addressRepository.findById(addressId).orElseThrow(()-> new AddressNotFoundException("Address not found with id: " + addressId));

       if(!address.getUser().getId().equals(user.getId())){
           throw new UnauthorizedException("You are not authorized to view this address");
       }

       AddressDto addressDto=addressMapper.toDto(address);

        Map<String, AddressDto> data = new HashMap<>();
        data.put("address", addressDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        data,
                        "Address fetched successfully"
                )
        );

    }

    // Update the address with the given ID, only if it is owned by the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String, AddressDto>>> updateAddressForCurrentUser( Long addressId, AddressDto addressDto){

        String email=authUtil.getCurrentUserEmail();
        if(email==null){
            throw new UnauthorizedException("Unauthorized access");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Address existingAddress=addressRepository.findById(addressId).orElseThrow(()-> new AddressNotFoundException("Address not found with id: " + addressId));

        if(!existingAddress.getUser().getId().equals(user.getId())){
            throw new UnauthorizedException("You are not authorized to update this address");
        }

        existingAddress.setCity(addressDto.getCity());
        existingAddress.setCountry(addressDto.getCountry());
        existingAddress.setPostalCode(addressDto.getPostalCode());
        existingAddress.setState(addressDto.getState());
        existingAddress.setStreet(addressDto.getStreet());

        Address updatedAddress=addressRepository.save(existingAddress);
        AddressDto updatedAddressDto=addressMapper.toDto(updatedAddress);

        Map<String,AddressDto> data=new HashMap<>();
        data.put("address",updatedAddressDto);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), data,"Address updated successfully"));
    }

    // Delete the address with the given ID if it belongs to the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String, AddressDto>>> deleteAddressForCurrentUser(Long addressId) {

        String email=authUtil.getCurrentUserEmail();
        if(email==null){
            throw new UnauthorizedException("Unauthorized access");
        }

        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User not found with email: " + email));
        Address address=addressRepository.findById(addressId).orElseThrow(()-> new AddressNotFoundException("Address not found with id: " + addressId));

        if(!address.getUser().getId().equals(user.getId())){
            throw new UnauthorizedException("You are not authorized to delete this address");
        }

        addressRepository.deleteById(addressId);

        AddressDto addressDto=addressMapper.toDto(address);
        Map<String,AddressDto> data=new HashMap<>();
        data.put("address",addressDto);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),data, "Address deleted successfully"));

    }

     // Update the current user's addresses by marking the specified address as default
     // and resetting the 'isDefault' flag to false for all other addresses
    public ResponseEntity<ApiResponse<Map<String, AddressDto>>> setDefaultAddressForCurrentUser(Long addressId){

        String email=authUtil.getCurrentUserEmail();
        if(email==null){
            throw new UnauthorizedException("Unauthorized access");
        }

        User user=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User not found with email:"+email));
        Address address=addressRepository.findById(addressId).orElseThrow(()->new AddressNotFoundException("Address not found with id: " + addressId));

        if(!address.getUser().getId().equals(user.getId())){
            throw new UnauthorizedException("You are not authorized to use this address");
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

        Map<String,AddressDto> data=new HashMap<>();
        data.put("address",addressDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        data,
                        "Default address set successfully"
                )
        );
    }


}
