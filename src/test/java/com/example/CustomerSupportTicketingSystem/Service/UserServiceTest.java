package com.example.CustomerSupportTicketingSystem.Service;

import com.example.CustomerSupportTicketingSystem.Entities.User;
import com.example.CustomerSupportTicketingSystem.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_success() {
        List<User> userList = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_exception() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers();
        });

        assertTrue(exception.getMessage().contains("Error Fetching all users"));
    }

    @Test
    void getMyProfile_success() {
        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.getMyProfile("user@example.com");

        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void getMyProfile_notFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getMyProfile("notfound@example.com");
        });

        assertTrue(exception.getMessage().contains("User Not Found with email"));
    }

    @Test
    void getUserById_success() {
        User user = new User();
        user.setId("123");
        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        User result = userService.getUserById("123");

        assertEquals("123", result.getId());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById("999");
        });

        assertTrue(exception.getMessage().contains("User not found with Id"));
    }
}

