package com.ql.ecommerce.exception;

import java.security.PublicKey;

public class UserNotFoundException extends ResourceNotFoundException {
      public UserNotFoundException(String message){
          super(message);
      }
}
