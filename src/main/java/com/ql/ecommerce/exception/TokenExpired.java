package com.ql.ecommerce.exception;

public class TokenExpired extends RuntimeException {
    public TokenExpired(String message){
        super(message);
    }

}
