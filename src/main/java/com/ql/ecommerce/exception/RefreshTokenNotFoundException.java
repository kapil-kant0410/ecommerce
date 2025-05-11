package com.ql.ecommerce.exception;

import com.ql.ecommerce.entity.RefreshToken;

public class RefreshTokenNotFoundException extends ResourceNotFoundException {
    public RefreshTokenNotFoundException(String message){
           super(message);
    }
}
