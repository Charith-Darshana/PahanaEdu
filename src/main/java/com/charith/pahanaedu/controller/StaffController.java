package com.charith.pahanaedu.controller;

import com.charith.pahanaedu.dto.StaffStatsDTO;
import com.charith.pahanaedu.dto.UserDTO;
import com.charith.pahanaedu.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getStaffById(@PathVariable Integer id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Integer id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<StaffStatsDTO> getStaffStats() {
        return ResponseEntity.ok(staffService.getStaffStats());
    }
}
