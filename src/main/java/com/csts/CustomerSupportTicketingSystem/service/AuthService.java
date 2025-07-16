package com.csts.CustomerSupportTicketingSystem.service;

import com.csts.CustomerSupportTicketingSystem.dto.LoginRequest;
import com.csts.CustomerSupportTicketingSystem.dto.LoginResponse;
import com.csts.CustomerSupportTicketingSystem.dto.RegisterRequest;
import com.csts.CustomerSupportTicketingSystem.model.User;
import com.csts.CustomerSupportTicketingSystem.repository.UserRepository;
import com.csts.CustomerSupportTicketingSystem.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(@Valid RegisterRequest request){
        try {
            if(userRepository.existsByEmail(request.getEmail())){
                throw new RuntimeException("User Already exists");
            }
            User.Role roleEnum=User.Role.valueOf(request.getRole().toUpperCase());
            User user =new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(roleEnum);
            return userRepository.save(user);
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Invalid role provided");
        }catch (Exception e){
            throw new RuntimeException("Registration Failed email already exists");
        }
    }
    public LoginResponse login(@Valid LoginRequest request){
        try {
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
            if (optionalUser.isEmpty()){
                throw new RuntimeException("User not found with the given mail");
            }
            User user=optionalUser.get();
            if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
                throw new RuntimeException("Invalid password");
            }
            String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
            return new LoginResponse(token);
        }catch (Exception e){
            System.out.println("Login error: " + e.getMessage());
            return new LoginResponse(null);
        }
    }
}
