package com.ql.ecommerce.exception;

public class InvalidTokenException extends RuntimeException {
       public InvalidTokenException(String message){
           super(message);
       }
}
