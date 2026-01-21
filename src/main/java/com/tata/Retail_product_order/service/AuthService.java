package com.tata.Retail_product_order.service;


import com.tata.Retail_product_order.dto.LoginRequest;
import com.tata.Retail_product_order.dto.LoginResponse;
import com.tata.Retail_product_order.dto.RegisterRequest;
import com.tata.Retail_product_order.entity.User;
import com.tata.Retail_product_order.entity.UserRole;
import com.tata.Retail_product_order.exception.UserAlreadyExistsException;
import com.tata.Retail_product_order.repository.UserRepository;
import com.tata.Retail_product_order.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.username());

        // Check if username already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.username());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.email());
        }

        // Handle null role - use default USER role
        UserRole role = request.role() != null ? request.role() : UserRole.USER;

        // Create new user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public LoginResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.username());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // Fetch user details
        User user = userRepository.findByUsername(request.username()).orElseThrow(() ->new RuntimeException("User not found"));

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        log.info("User logged in successfully: {}", user.getUsername());

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

}
