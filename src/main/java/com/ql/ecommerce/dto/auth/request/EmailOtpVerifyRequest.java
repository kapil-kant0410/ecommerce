package com.ql.ecommerce.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailOtpVerifyRequest {
    @NotBlank
    @Email(message = "Please pass valid email.")
    private String email;

    @NotBlank(message = "Otp must not be empty")
    private String otp;
}
