package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.address.response.AddressDto;
import com.ql.ecommerce.entity.Address;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.exception.AddressNotFound;
import com.ql.ecommerce.exception.BadRequest;
import com.ql.ecommerce.exception.Unauthorized;
import com.ql.ecommerce.mapper.AddressMapper;
import com.ql.ecommerce.repository.AddressRepository;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.AddressService;
import com.ql.ecommerce.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;
    private final AuthUtil authUtil;
    private final AddressRepository addressRepository;
    private final ResponseBuilder responseBuilder;

    public AddressServiceImpl(ResponseBuilder responseBuilder,AddressRepository addressRepository,AuthUtil authUtil,AddressMapper addressMapper){
        this.addressMapper=addressMapper;
        this.authUtil=authUtil;
        this.addressRepository=addressRepository;
        this.responseBuilder=responseBuilder;
    }

    // Fetch all addresses belonging to the current authenticated user
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUserAddresses() {

        User currentUser = authUtil.getCurrentUser();

        List<Address> allAddresses=addressRepository.findByUser(currentUser);
        List<AddressDto> addressDtoList = addressMapper.toDtoList(allAddresses);

        return responseBuilder.build("addresses", addressDtoList, "User addresses fetched successfully");
    }

    // Create and associate a new address with the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String, Object>>> addAddressForCurrentUser(AddressDto addressDto){

        User currentUser = authUtil.getCurrentUser();

        Address address = addressMapper.toEntity(addressDto);
        address.setUser(currentUser);

        Address savedAddress = addressRepository.save(address);
        return responseBuilder.build("address", addressMapper.toDto(savedAddress), "Address added successfully");
    }

    // Fetch the address with the given ID, only if it is associated with the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String,Object>>> getAddressForCurrentUser(Long addressId){

        User currentUser = authUtil.getCurrentUser();

        Address address = getAddressOrThrow(addressId);
        requireOwnership(address, currentUser);

        return responseBuilder.build("address", addressMapper.toDto(address), "Address fetched successfully");
    }

    // Update the address with the given ID, only if it is owned by the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String,Object>>> updateAddressForCurrentUser( Long addressId, AddressDto addressDto){

        User currentUser = authUtil.getCurrentUser();

        Address address = getAddressOrThrow(addressId);
        requireOwnership(address, currentUser);

        address = addressMapper.updateAddress(address, addressDto);
        return responseBuilder.build("address", addressMapper.toDto(address), "Address updated successfully");
    }

    // Delete the address with the given ID if it belongs to the currently authenticated user
    public ResponseEntity<ApiResponse<Map<String,Object>>> deleteAddressForCurrentUser(Long addressId) {

        User currentUser = authUtil.getCurrentUser();

        Address address = getAddressOrThrow(addressId);
        requireOwnership(address, currentUser);

        addressRepository.delete(address);
        return responseBuilder.build("address", addressMapper.toDto(address), "Address deleted successfully");
    }

     // Update the current user's addresses by marking the specified address as default
     // and resetting the 'isDefault' flag to false for all other addresses
    public ResponseEntity<ApiResponse<Map<String, Object>>> setDefaultAddressForCurrentUser(Long addressId){

        User currentUser = authUtil.getCurrentUser();

        Address address = getAddressOrThrow(addressId);
        requireOwnership(address, currentUser);

        if (address.isDefault()) {
            throw new BadRequest("This address is already marked as default");
        }

        List<Address> userAddresses = addressRepository.findByUser(currentUser);
        userAddresses.forEach(a -> {
            if (a.isDefault()) a.setDefault(false);
        });

        address.setDefault(true);
        addressRepository.save(address);

        return responseBuilder.build("address", addressMapper.toDto(address), "Default address set successfully");
    }

    //helper method
    private Address getAddressOrThrow(Long id) {
        return addressRepository.findById(id).orElseThrow(() ->
                new AddressNotFound("Address not found with id: " + id));
    }

    //helper method
    private void requireOwnership(Address address, User user) {
        if (!address.getUser().getId().equals(user.getId())) {
            throw new Unauthorized("You are not authorized to access this address");
        }
    }

}
