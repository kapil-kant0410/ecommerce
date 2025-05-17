package com.ql.ecommerce.exception;

public class AccessDenied extends RuntimeException {
      public AccessDenied(String message){
             super(message);
      }
}
