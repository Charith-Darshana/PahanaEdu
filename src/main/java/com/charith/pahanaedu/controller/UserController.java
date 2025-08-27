package com.charith.pahanaedu.controller;

import com.charith.pahanaedu.dto.LoginRequestDTO;
import com.charith.pahanaedu.dto.PasswordChangeRequestDTO;
import com.charith.pahanaedu.dto.RegistrationRequestDTO;
import com.charith.pahanaedu.dto.UserDTO;
import com.charith.pahanaedu.entity.User;
import com.charith.pahanaedu.service.UserService;
import com.charith.pahanaedu.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("http://localhost:4200")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequestDTO requestDTO) {
        try {
            User user = userService.register(requestDTO);

            if (user != null) {
                return ResponseEntity.ok("User registered successfully");
            } else {
                throw new RuntimeException("User could not be registered! Please try again later.");
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDTO) {
        try {
            UserDTO userDTO = userService.login(requestDTO);

            if (userDTO != null) {
                String token = jwtUtil.generateToken(userDTO.getUsername());

                return ResponseEntity.ok(Map.of("token", token, "user", userDTO, "message", "Login successful!"));
            } else {
                throw new RuntimeException("Login failed!");
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                throw new RuntimeException("Invalid token format");
            }

            String username = jwtUtil.getUsername(token);

            if (userService.logout(username)) {
                return ResponseEntity.ok("Logout successful!");
            } else {
                throw new RuntimeException("Logout failed! Please try again later.");
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Integer id, @RequestBody PasswordChangeRequestDTO dto) {
        userService.changePassword(id, dto);
        return ResponseEntity.ok().build();
    }
}
