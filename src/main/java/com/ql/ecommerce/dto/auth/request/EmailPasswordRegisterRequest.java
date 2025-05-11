package com.ql.ecommerce.dto.auth.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class EmailPasswordRegisterRequest {
    @NotBlank(message = "Name field must not be empty")
    @Pattern(regexp = "^[A-Za-z]+([\\s][A-Za-z]+)*$", message = "Name must contain only alphabets and spaces")
    private String name;

    @Email(message = "Invalid email field")
    private String email;

    @NotBlank(message = "Password field must not be empty")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character")
    private String password;

    @NotBlank(message = "Role name must not be empty")
    @Pattern(regexp = "ROLE_SELLER|ROLE_CUSTOMER", message = "Role name must be either 'ROLE_SELLER' or 'ROLE_CUSTOMER'")
    private String role;
}