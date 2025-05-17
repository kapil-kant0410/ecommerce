package com.ql.ecommerce.exception;

public class Unauthorized extends RuntimeException {
       public Unauthorized(String message){
           super(message);
       }
}
