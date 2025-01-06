package com.szorzo.szorzo_gyakorlo.service;

import com.szorzo.szorzo_gyakorlo.model.User;
import com.szorzo.szorzo_gyakorlo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully() {
        // Arrange
        User mockUser = new User("username", "encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        User result = userService.registerUser("username", "password");

        // Assert
        assertNotNull(result, "User should not be null");
        assertEquals("username", result.getUsername(), "Username should match");
        assertEquals("encodedPassword", result.getPassword(), "Password should be encoded");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUserExists() {
        // Arrange
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("existingUser", "password");
        });

        assertEquals("User already exists", exception.getMessage(), "Exception message should match");
        verify(userRepository, times(1)).existsByUsername("existingUser");
        verify(userRepository, never()).save(any(User.class));
    }
}
