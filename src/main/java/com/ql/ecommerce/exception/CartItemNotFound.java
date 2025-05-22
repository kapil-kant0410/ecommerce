package com.ql.ecommerce.exception;

public class CartItemNotFound extends  ResourceNotFound{
    public CartItemNotFound(String message){
        super(message);
    }
}
