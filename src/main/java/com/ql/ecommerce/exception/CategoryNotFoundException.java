package com.ql.ecommerce.exception;

public class CategoryNotFoundException extends ResourceNotFoundException{
    public CategoryNotFoundException(String message){
        super(message);
    }
}
