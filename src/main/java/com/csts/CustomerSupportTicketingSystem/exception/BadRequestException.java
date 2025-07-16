package com.csts.CustomerSupportTicketingSystem.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }
}
