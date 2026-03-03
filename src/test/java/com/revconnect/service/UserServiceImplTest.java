package com.revconnect.service;

import com.revconnect.dto.UserRegistrationDto;
import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import com.revconnect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto dto;

    @BeforeEach
    void setUp() {
        dto = new UserRegistrationDto();
        dto.setUsername("newuser");
        dto.setEmail("new@example.com");
        dto.setPassword("password");
        dto.setRole(UserRole.PERSONAL);
        dto.setFirstName("First");
        dto.setLastName("Last");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerUser(dto);

        assertThat(registeredUser.getUsername()).isEqualTo(dto.getUsername());
        assertThat(registeredUser.getPassword()).isEqualTo("encodedPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameExists_ThrowsException() {
        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username already exists");
    }

    @Test
    void findByUsername_ReturnsUser() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User found = userService.findByUsername("testuser");

        assertThat(found.getUsername()).isEqualTo("testuser");
    }

    @Test
    void findById_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 1");
    }
}
