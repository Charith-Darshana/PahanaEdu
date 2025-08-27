package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.LoginRequestDTO;
import com.charith.pahanaedu.dto.PasswordChangeRequestDTO;
import com.charith.pahanaedu.dto.RegistrationRequestDTO;
import com.charith.pahanaedu.dto.UserDTO;
import com.charith.pahanaedu.entity.SecurityLog;
import com.charith.pahanaedu.entity.User;
import com.charith.pahanaedu.entity.UserRole;
import com.charith.pahanaedu.exception.ResourceNotFoundException;
import com.charith.pahanaedu.exception.UserAlreadyExistsException;
import com.charith.pahanaedu.repository.SecurityLogRepository;
import com.charith.pahanaedu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityLogRepository securityLogRepository;

    @Override
    public User register(RegistrationRequestDTO requestDTO) {
        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }

        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setRole(UserRole.STAFF);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public UserDTO login(LoginRequestDTO requestDTO) {
        SecurityLog securityLog = new SecurityLog();
        securityLog.setUser(null);
        securityLog.setDateTime(LocalDateTime.now());

        try {
            User user = userRepository.findByUsername(requestDTO.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestDTO.getUsername()));

            if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid credentials: " + requestDTO.getUsername());
            }

            securityLog.setAction("200: LOGIN_SUCCESS");
            securityLog.setUser(user);
            securityLogRepository.save(securityLog);

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(requestDTO.getUsername());
            userDTO.setRole(user.getRole().name().toLowerCase());
            userDTO.setCreatedAt(user.getCreatedAt());
            return userDTO;
        } catch (Exception ex) {
            securityLog.setAction("401: LOGIN_FAILED - " + ex.getMessage());
            securityLogRepository.save(securityLog);
            throw ex;
        }
    }

    @Override
    public boolean logout(String username) {
        SecurityLog securityLog = new SecurityLog();
        securityLog.setUser(null);
        securityLog.setDateTime(LocalDateTime.now());

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            securityLog.setAction("200: LOGOUT_SUCCESS");
            securityLog.setUser(optionalUser.get());
            securityLogRepository.save(securityLog);

            return true;
        } else {
            securityLog.setAction("404: LOGOUT_FAILED - User not found: " + username);
            securityLogRepository.save(securityLog);
            throw new ResourceNotFoundException("User not found: " + username);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public void changePassword(Integer id, PasswordChangeRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password must be different from the old password");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
