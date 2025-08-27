package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.StaffStatsDTO;
import com.charith.pahanaedu.dto.UserDTO;

import java.util.List;

public interface StaffService {
    List<UserDTO> getAllStaff();
    UserDTO getStaffById(Integer id);
    void deleteStaff(Integer id);
    StaffStatsDTO getStaffStats();
}
