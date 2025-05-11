package com.ql.ecommerce.exception;

public class VerificationTokenNotFoundException extends ResourceNotFoundException{
   public VerificationTokenNotFoundException(String message){
        super(message);
    }
}
