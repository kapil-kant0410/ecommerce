package com.ql.ecommerce.exception;

public class OrderNotFoundException extends ResourceNotFoundException{
    public OrderNotFoundException(String message){
        super(message);
    }
}
