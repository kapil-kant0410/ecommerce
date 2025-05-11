package com.ql.ecommerce.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdate {
    @NotBlank(message = "Name is required")
    private String name;
}