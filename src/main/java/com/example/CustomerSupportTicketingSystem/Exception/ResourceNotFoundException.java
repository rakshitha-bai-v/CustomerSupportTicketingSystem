package com.example.CustomerSupportTicketingSystem.Exception;


public class ResourceNotFoundException  extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
