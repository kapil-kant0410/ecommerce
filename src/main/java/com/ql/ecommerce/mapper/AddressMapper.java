package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.address.response.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.ql.ecommerce.entity.Address;

import java.util.List;

@Component
public class AddressMapper {

    private final ModelMapper modelMapper;

    public AddressMapper(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }

    public AddressDto toDto(Address address){
      return modelMapper.map(address,AddressDto.class);
    }

    public List<AddressDto> toDtoList(List<Address> addresses){
        return addresses.stream().map(this::toDto).toList();
    }

    public Address toEntity(AddressDto addressDto){
        return modelMapper.map(addressDto, Address.class);
    }

    public List<Address> toEntityList(List<AddressDto> addressDtoList){return addressDtoList.stream().map((this::toEntity)).toList();}

}
