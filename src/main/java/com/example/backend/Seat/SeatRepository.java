package com.example.backend.Seat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findByLoungeAndStatus(String lounge, SeatStatus status);

    Optional<SeatEntity> findByLoungeAndSeatNumber(String lounge, Integer seatNumber);

    List<SeatEntity> findByStudentIdAndStatusNot(String studentId, SeatStatus status);

    List<SeatEntity> findByStatusNotAndEndTimeLessThanEqual(SeatStatus status, LocalDateTime endTime);

    List<SeatEntity> findByStatus(SeatStatus status);

    long countByStatus(SeatStatus status);
}
