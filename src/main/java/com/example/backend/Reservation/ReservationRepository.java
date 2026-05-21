package com.example.backend.Reservation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findAllByOrderByTypeAscNumAsc();

    Optional<ReservationEntity> findByTypeAndNum(String type, Integer num);

    boolean existsByTypeAndNum(String type, Integer num);
}