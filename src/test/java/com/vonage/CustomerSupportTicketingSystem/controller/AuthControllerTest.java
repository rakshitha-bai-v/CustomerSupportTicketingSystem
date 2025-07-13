package com.vonage.CustomerSupportTicketingSystem.controller;

import com.vonage.CustomerSupportTicketingSystem.dto.LoginRequest;
import com.vonage.CustomerSupportTicketingSystem.dto.LoginResponse;
import com.vonage.CustomerSupportTicketingSystem.dto.RegisterRequest;
import com.vonage.CustomerSupportTicketingSystem.model.User;
import com.vonage.CustomerSupportTicketingSystem.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Register Tests ---

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setRole("USER");

        User savedUser = new User();
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setRole(User.Role.CUSTOMER);

        when(authService.register(any(RegisterRequest.class))).thenReturn(savedUser);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof User);

        User responseUser = (User) response.getBody();
        assertEquals("John Doe", responseUser.getName());
        assertEquals("john@example.com", responseUser.getEmail());
        assertEquals(User.Role.CUSTOMER, responseUser.getRole());
    }

    @Test
    void register_failure_duplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("User Already exists"));

        ResponseEntity<?> response = authController.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("User Already exists"));
    }

    // --- Login Tests ---

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        LoginResponse loginResponse = new LoginResponse("mocked.jwt.token");

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof LoginResponse);

        LoginResponse responseBody = (LoginResponse) response.getBody();
        assertEquals("mocked.jwt.token", responseBody.getToken());
    }

    @Test
    void login_failure_invalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrongpassword");

        // Return LoginResponse with null token for invalid credentials
        when(authService.login(any(LoginRequest.class))).thenReturn(new LoginResponse(null));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void login_failure_exception() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("DB connection failed"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Login failed:"));
    }
}
