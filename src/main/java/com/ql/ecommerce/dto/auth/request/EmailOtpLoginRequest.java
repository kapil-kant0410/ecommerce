package com.ql.ecommerce.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailOtpLoginRequest {
    @NotBlank
    @Email(message = "Please enter valid email")
    private String email;
}
