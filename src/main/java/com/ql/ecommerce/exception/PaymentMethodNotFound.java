package com.ql.ecommerce.exception;

public class PaymentMethodNotFound extends ResourceNotFound{
    public PaymentMethodNotFound(String message){
        super(message);
    }
}
