package com.ql.ecommerce.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailPasswordLoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
