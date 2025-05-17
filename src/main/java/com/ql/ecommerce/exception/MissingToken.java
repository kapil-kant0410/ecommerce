package com.ql.ecommerce.exception;

public class MissingToken extends RuntimeException{
    public MissingToken(String message){
          super(message);
    }
}
