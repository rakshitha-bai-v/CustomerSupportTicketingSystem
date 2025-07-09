package com.example.CustomerSupportTicketingSystem.Service;
import com.example.CustomerSupportTicketingSystem.DTO.LoginRequest;
import com.example.CustomerSupportTicketingSystem.DTO.LoginResponse;
import com.example.CustomerSupportTicketingSystem.DTO.RegisterRequest;
import com.example.CustomerSupportTicketingSystem.Entities.User;
import com.example.CustomerSupportTicketingSystem.Entities.User.Role;
import com.example.CustomerSupportTicketingSystem.Repository.UserRepository;
import com.example.CustomerSupportTicketingSystem.Security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Register Tests

    @Test
    void register_ShouldRegisterUser_WhenValidRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setRole("USER");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.CUSTOMER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.register(request);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(Role.CUSTOMER, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrow_WhenUserAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("User Already exists", exception.getMessage());
    }

    @Test
    void register_ShouldThrow_WhenInvalidRole() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");
        request.setRole("INVALID_ROLE");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Invalid role provided", exception.getMessage());
    }

    // Login Tests

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getEmail(), user.getRole().name())).thenReturn("jwtToken");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void login_ShouldReturnNullToken_WhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertNull(response.getToken());
    }

    @Test
    void login_ShouldReturnNullToken_WhenPasswordMismatch() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertNull(response.getToken());
    }
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String raw = "yourTestPassword";
        String encoded = encoder.encode(raw);

        System.out.println("Encoded password: " + encoded);
        System.out.println("Matches: " + encoder.matches(raw, encoded));
    }
}
