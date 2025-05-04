package com.ql.ecommerce.exception;

public class MissingTokenException extends RuntimeException{
    public MissingTokenException(String message){
          super(message);
    }
}
