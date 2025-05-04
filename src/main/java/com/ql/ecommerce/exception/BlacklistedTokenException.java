package com.ql.ecommerce.exception;

public class BlacklistedTokenException extends RuntimeException{
        BlacklistedTokenException(String message){
            super(message);
        }
}
