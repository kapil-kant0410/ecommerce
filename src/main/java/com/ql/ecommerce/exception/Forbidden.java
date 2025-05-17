package com.ql.ecommerce.exception;

public class Forbidden extends RuntimeException {
    public Forbidden(String message){
        super(message);
    }
}
