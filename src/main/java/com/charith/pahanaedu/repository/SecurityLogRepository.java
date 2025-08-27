package com.charith.pahanaedu.repository;

import com.charith.pahanaedu.entity.SecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Integer> {
}
