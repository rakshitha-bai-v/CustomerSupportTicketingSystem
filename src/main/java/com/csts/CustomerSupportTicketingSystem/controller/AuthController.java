package com.csts.CustomerSupportTicketingSystem.controller;

import com.csts.CustomerSupportTicketingSystem.dto.LoginRequest;
import com.csts.CustomerSupportTicketingSystem.dto.LoginResponse;
import com.csts.CustomerSupportTicketingSystem.dto.RegisterRequest;
import com.csts.CustomerSupportTicketingSystem.model.User;
import com.csts.CustomerSupportTicketingSystem.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        try {
            User registredUser = authService.register(request);
            return new ResponseEntity<>(registredUser, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        try {
            LoginResponse response = authService.login(request);
            if(response.getToken()!=null){
                return ResponseEntity.ok().body(response);
            }else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Login failed: "+ e.getMessage());
        }
    }
}
