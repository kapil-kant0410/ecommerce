package com.ql.ecommerce.exception;

public class OtpNotFoundException extends  ResourceNotFoundException{
    public OtpNotFoundException(String message) {
        super(message);
    }
}
