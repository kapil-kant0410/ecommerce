package com.ql.ecommerce.exception;

public class BlacklistedToken extends RuntimeException{
        BlacklistedToken(String message){
            super(message);
        }
}
