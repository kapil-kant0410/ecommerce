package com.ql.ecommerce.exception;

public class ExpiredTokenException extends RuntimeException{

       public ExpiredTokenException(String message){
             super(message);
       }

}
