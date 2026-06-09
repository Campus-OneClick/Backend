package com.example.backend.Reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectionLogRepository extends JpaRepository<RejectionLog, Long> {
    List<RejectionLog> findByStudentId(String studentId);
}
