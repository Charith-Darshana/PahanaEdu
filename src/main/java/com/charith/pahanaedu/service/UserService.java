package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.LoginRequestDTO;
import com.charith.pahanaedu.dto.PasswordChangeRequestDTO;
import com.charith.pahanaedu.dto.RegistrationRequestDTO;
import com.charith.pahanaedu.dto.UserDTO;
import com.charith.pahanaedu.entity.User;

public interface UserService {
    User register(RegistrationRequestDTO requestDTO);
    UserDTO login(LoginRequestDTO requestDTO);
    boolean logout(String username);
    User getUserByUsername(String username);
    public void changePassword(Integer id, PasswordChangeRequestDTO dto);
}
