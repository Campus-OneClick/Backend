package com.example.backend.Reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type", "num"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer num;

    @Column(name = "username", nullable = false)
    private String user;

    @Column(name = "classroom_id")
    private String classroomId;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String day;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private String requestedAt;

    private String processedAt;

    private LocalDateTime processedTimestamp;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    private String memo;
}
