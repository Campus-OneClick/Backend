package com.example.backend.Schedule;

import com.example.backend.Classroom.ClassroomEntity;
import com.example.backend.Classroom.ClassroomRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassroomRepository classroomRepository;

    public Object createSchedule(ScheduleEntity scheduleEntity) {

        // 평면 JSON(classroomId)로 들어오면 실제 연관 엔티티를 세팅
        if (scheduleEntity.getClassroomEntity() == null
                && scheduleEntity.getClassroomId() != null
                && !scheduleEntity.getClassroomId().isBlank()) {
            Optional<ClassroomEntity> classroom = classroomRepository.findById(scheduleEntity.getClassroomId());
            if (classroom.isEmpty()) {
                return Map.of(
                    "success", false,
                    "message", "존재하지 않는 강의실입니다"
                );
            }
            scheduleEntity.setClassroomEntity(classroom.get());
        }

        if (scheduleEntity.getClassroomEntity() == null) {
            return Map.of(
                "success", false,
                "message", "강의실 정보가 필요합니다"
            );
        }

        // 1. 겹치는 시간 있는지 검사
        List<ScheduleEntity> conflicts =
            scheduleRepository.findConflicts(
                scheduleEntity.getClassroomEntity().getClassroomId(),
                scheduleEntity.getDay(),
                scheduleEntity.getStartTime(),
                scheduleEntity.getEndTime()
            );

        if (!conflicts.isEmpty()) {
            return Map.of(
                "success", false,
                "message", "이미 해당 시간에 강의가 존재합니다"
            );
        }

        // 2. 저장
        ScheduleEntity saved = scheduleRepository.save(scheduleEntity);

        return Map.of(
            "success", true,
            "data", saved
        );
    }

    public List<ScheduleEntity> findAll() {
        return scheduleRepository.findAll();
    }

    public ScheduleEntity findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("스케줄을 찾을 수 없습니다"));
    }

    public Object updateSchedule(Long id, ScheduleEntity updateEntity) {
        ScheduleEntity existing = scheduleRepository.findById(id)
                .orElse(null);
        if (existing == null) {
            return Map.of(
                "success", false,
                "message", "존재하지 않는 스케줄입니다"
            );
        }

        // classroomId가 전달되면 강의실 엔티티로 변환
        if (updateEntity.getClassroomEntity() == null
                && updateEntity.getClassroomId() != null
                && !updateEntity.getClassroomId().isBlank()) {
            Optional<ClassroomEntity> classroom = classroomRepository.findById(updateEntity.getClassroomId());
            if (classroom.isEmpty()) {
                return Map.of(
                    "success", false,
                    "message", "존재하지 않는 강의실입니다"
                );
            }
            updateEntity.setClassroomEntity(classroom.get());
        }

        if (updateEntity.getClassroomEntity() == null) {
            updateEntity.setClassroomEntity(existing.getClassroomEntity());
        }

        // 충돌 체크 (자신 제외)
        List<ScheduleEntity> conflicts =
            scheduleRepository.findConflicts(
                updateEntity.getClassroomEntity().getClassroomId(),
                updateEntity.getDay(),
                updateEntity.getStartTime(),
                updateEntity.getEndTime()
            ).stream()
            .filter(s -> !s.getScheduleId().equals(id))
            .toList();

        if (!conflicts.isEmpty()) {
            return Map.of(
                "success", false,
                "message", "이미 해당 시간에 강의가 존재합니다"
            );
        }

        // 필드 업데이트
        existing.setClassroomEntity(updateEntity.getClassroomEntity());
        existing.setDay(updateEntity.getDay());
        existing.setStartTime(updateEntity.getStartTime());
        existing.setEndTime(updateEntity.getEndTime());
        existing.setSubject(updateEntity.getSubject());
        existing.setProfessor(updateEntity.getProfessor());

        ScheduleEntity updated = scheduleRepository.save(existing);
        return Map.of(
            "success", true,
            "data", updated
        );
    }

    public Object deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            return Map.of(
                "success", false,
                "message", "존재하지 않는 스케줄입니다"
            );
        }
        scheduleRepository.deleteById(id);
        return Map.of("success", true);
    }
}