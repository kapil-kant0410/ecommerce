package com.ql.ecommerce.exception;

public class BlacklistedToken extends RuntimeException{
        public BlacklistedToken(String message){
            super(message);
        }
}
