package com.vonage.CustomerSupportTicketingSystem.exception;


public class ResourceNotFoundException  extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
