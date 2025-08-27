package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.StaffStatsDTO;
import com.charith.pahanaedu.dto.UserDTO;
import com.charith.pahanaedu.entity.User;
import com.charith.pahanaedu.entity.UserRole;
import com.charith.pahanaedu.exception.ResourceNotFoundException;
import com.charith.pahanaedu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;

    @Override
    public List<UserDTO> getAllStaff() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public UserDTO getStaffById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        return mapToDTO(user);
    }

    @Override
    public void deleteStaff(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public StaffStatsDTO getStaffStats() {
        List<User> users = userRepository.findAll();
        long total = users.size();
        long admins = users.stream().filter(u -> u.getRole() == UserRole.ADMIN).count();
        long staff = users.stream().filter(u -> u.getRole() == UserRole.STAFF).count();
        return new StaffStatsDTO(total, admins, staff);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
