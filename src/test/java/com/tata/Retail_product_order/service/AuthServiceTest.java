package com.tata.Retail_product_order.service;



import com.tata.Retail_product_order.dto.LoginRequest;
import com.tata.Retail_product_order.dto.LoginResponse;
import com.tata.Retail_product_order.dto.RegisterRequest;
import com.tata.Retail_product_order.entity.User;
import com.tata.Retail_product_order.entity.UserRole;
import com.tata.Retail_product_order.exception.UserAlreadyExistsException;
import com.tata.Retail_product_order.repository.UserRepository;
import com.tata.Retail_product_order.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .username("manoj")
                .email("manoj@test.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();
    }

    // ---------------- SIGNUP ----------------

    @Test
    void signup_success() {
        RegisterRequest request = new RegisterRequest(
                "sourav",
                "sghatak@test.com",
                "password",
                UserRole.USER
        );

        when(userRepository.existsByUsername("sourav")).thenReturn(false);
        when(userRepository.existsByEmail("sghatak@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtUtil.generateToken("sourav")).thenReturn("jwt-token");

        LoginResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("sourav", response.username());
        assertEquals("sghatak@test.com", response.email());
        assertEquals("USER", response.role());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_shouldFail_whenUsernameExists() {
        RegisterRequest request = new RegisterRequest(
                "Sourav",
                "sghatak@test.com",
                "password",
                UserRole.USER
        );

        when(userRepository.existsByUsername("Sourav"))
                .thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(request));

    }

    @Test
    void signup_shouldFail_whenEmailExists() {
        RegisterRequest request = new RegisterRequest(
                "sourav",
                "sghatak@test.com",
                "password",
                UserRole.USER
        );

        when(userRepository.existsByUsername("sourav"))
                .thenReturn(false);

        when(userRepository.existsByEmail("sghatak@test.com"))
                .thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(request));
    }

    // ---------------- LOGIN ----------------

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("Sourav", "password");

        User user = User.builder()
                .username("Sourav")
                .email("sourav@test.com")
                .role(UserRole.USER)
                .build();

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByUsername("Sourav"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken("Sourav"))
                .thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("Sourav", response.username());
        assertEquals("sourav@test.com", response.email());
        assertEquals("USER", response.role());
    }

    @Test
    void login_shouldFail_whenUserNotFound() {
        LoginRequest request = new LoginRequest("sourav", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByUsername("sourav"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.login(request));
    }
}
