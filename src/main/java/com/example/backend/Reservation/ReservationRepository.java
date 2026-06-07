package com.example.backend.Reservation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findAllByOrderByTypeAscNumAsc();

    Optional<ReservationEntity> findByTypeAndNum(String type, Integer num);

    boolean existsByTypeAndNum(String type, Integer num);

    List<ReservationEntity> findByTypeAndLecture(String type, String lecture);

    List<ReservationEntity> findByUserAndStatus(String user, Integer status);

    @Query("SELECT MAX(r.num) FROM ReservationEntity r WHERE r.type = :type")
    Optional<Integer> findMaxNumByType(@Param("type") String type);
}