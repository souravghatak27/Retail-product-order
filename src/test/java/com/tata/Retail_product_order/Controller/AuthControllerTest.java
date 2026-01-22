package com.tata.Retail_product_order.Controller;


import com.tata.Retail_product_order.controller.AuthController;
import com.tata.Retail_product_order.dto.LoginRequest;
import com.tata.Retail_product_order.dto.LoginResponse;
import com.tata.Retail_product_order.dto.RegisterRequest;
import com.tata.Retail_product_order.entity.UserRole;
import com.tata.Retail_product_order.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123", UserRole.USER);
        LoginResponse response = new LoginResponse("jwt-token", "testuser", "test@example.com", "USER");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        // when
        ResponseEntity<LoginResponse> result = authController.register(request);
        // then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        LoginResponse body = result.getBody();
        assertNotNull(body);
        assertEquals("jwt-token", body.token());
        assertEquals("testuser", body.username());
        assertEquals("test@example.com", body.email());
        assertEquals("USER", body.role());
    }

    @Test
    void register_InvalidInput_BadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("", "", "", UserRole.USER);
        when(authService.register(request)).thenThrow(new IllegalArgumentException("Invalid input"));
        assertThrows(IllegalArgumentException.class, () -> authController.register(request));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");
        LoginResponse response = new LoginResponse("jwt-token", "testuser", "test@example.com", "USER");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);
        // when
        ResponseEntity<LoginResponse> result = authController.login(request);
        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("jwt-token", result.getBody().token());
        assertEquals("testuser", result.getBody().username());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials_Unauthorized() throws Exception {
        LoginRequest request = new LoginRequest("user", "wrongpassword");

        // Simulate Spring Security throwing exception
        when(authService.login(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authController.login(request));
    }
}