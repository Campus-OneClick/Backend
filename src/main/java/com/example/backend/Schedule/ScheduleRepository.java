package com.example.backend.Schedule;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository
        extends JpaRepository<ScheduleEntity, Long> {

    @Query("""
        SELECT s FROM ScheduleEntity s
        WHERE s.classroomEntity.classroomId = :classroomId
        AND s.day = :day
        AND (
            (:startTime < s.endTime) AND (:endTime > s.startTime)
        )
    """)

    // 겹치는 경우: 시작 < 기존 끝 AND 끝 > 기존 시작

List<ScheduleEntity> findConflicts(
    String classroomId,
        DayOfWeek day,
        LocalTime startTime,
        LocalTime endTime
    );

    @Query("""
        SELECT s FROM ScheduleEntity s
        WHERE s.classroomEntity.classroomId = :classroomId
        AND s.day = :day
        AND s.startTime <= :time
        AND s.endTime > :time
    """)
    List<ScheduleEntity> findActiveAt(
        String classroomId,
        DayOfWeek day,
        LocalTime time
    );
}