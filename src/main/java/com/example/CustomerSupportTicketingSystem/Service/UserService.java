package com.example.CustomerSupportTicketingSystem.Service;

import com.example.CustomerSupportTicketingSystem.Entities.User;
import com.example.CustomerSupportTicketingSystem.Repository.UserRepository;
import com.example.CustomerSupportTicketingSystem.Security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(){
        try {
            return userRepository.findAll();
        }catch (Exception e){
            throw new RuntimeException("Error Fetching all users: " + e.getMessage());
        }
    }
    public User getMyProfile(String email){
        try {
            return userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found with email: " + email));
        }catch (Exception e){
            throw new RuntimeException("Error Fetching profile: " + e.getMessage());
        }
    }
    public User getUserById(String id){
        try{
            return userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found with Id: " + id));
        }catch (Exception e){
            throw new RuntimeException("Error fetching user by ID: " + e.getMessage());
        }
    }
}
