package com.email.authservice.controller;

import com.email.authservice.dto.LoginRequestDTO;
import com.email.authservice.dto.LoginResponseDTO;
import com.email.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO>login(@RequestBody LoginRequestDTO loginRequestDTO) {

        Optional<String> tokenOptional=authService.authenticate(loginRequestDTO);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenOptional.get();
        return ResponseEntity.ok(new LoginResponseDTO(token));

    }

    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        // Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return authService.validateToken(authHeader.substring(7)) // remove the length of Bearer
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
