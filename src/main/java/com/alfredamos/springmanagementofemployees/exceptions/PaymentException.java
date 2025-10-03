package com.alfredamos.springmanagementofemployees.exceptions;


public class PaymentException extends RuntimeException{
    public PaymentException(String message) {
        super(message);
    }
}
