package com.ql.ecommerce.exception;

public class EmptyCart extends ResourceNotFound {
    public EmptyCart(String message){
        super(message);
    }
}
