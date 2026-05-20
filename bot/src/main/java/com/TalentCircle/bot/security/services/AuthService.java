package com.TalentCircle.bot.security.services;

import com.TalentCircle.bot.security.dto.LoginRequest;
import com.TalentCircle.bot.security.dto.RegisterRequestDTO;
import com.TalentCircle.bot.security.exceptions.EmailAlreadyExistsException;
import com.TalentCircle.bot.user.entity.UserEntity;
import com.TalentCircle.bot.user.enums.Role;
import com.TalentCircle.bot.user.repository.UserRepository;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        return jwtService.generateToken(request.email());
    }

    public String register(RegisterRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserEntity user = new UserEntity();

        user.setEmail(request.email());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setRole(Role.EDITOR);

        userRepository.save(user);

        return jwtService.generateToken(user.getEmail());
    }
}