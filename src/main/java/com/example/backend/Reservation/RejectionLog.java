package com.example.backend.Reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rejection_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentId;

    private String classroomId;

    private String day;

    private String time;

    private String rejectionReason;

    @Column(nullable = false)
    private String rejectedAt;
}
