package com.example.backend.Schedule;

import java.time.LocalTime;

import com.example.backend.Classroom.ClassroomEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "schedules")
@Getter @Setter
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private ClassroomEntity classroomEntity;

    // Request body에서 classroomId를 직접 받기 위한 임시 필드 (응답에서 제외)
    @Transient
    @JsonIgnore
    private String classroomId;

    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    private LocalTime startTime;
    private LocalTime endTime;

    private String subject;
    private String professor;
}