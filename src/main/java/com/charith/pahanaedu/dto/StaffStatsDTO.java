package com.charith.pahanaedu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaffStatsDTO {
    private long totalStaff;
    private long admins;
    private long staffMembers;
}

