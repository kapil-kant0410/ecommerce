package com.ql.ecommerce.exception;

public class InvalidTokenStructureException extends  RuntimeException{
       public InvalidTokenStructureException(String message){
           super(message);
       }
}
