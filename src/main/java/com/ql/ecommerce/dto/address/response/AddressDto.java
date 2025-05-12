package com.ql.ecommerce.dto.address.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    @NotBlank(message = "street must not be blank")
    private String street;
    @NotBlank(message = "city must not be blank")
    private String city;
    @NotBlank(message = "state must not be blank")
    private String state;
    @NotBlank(message = "postal code must not be blank")
    private String postalCode;
    @NotBlank(message = "country must not be blank")
    private String country;
}