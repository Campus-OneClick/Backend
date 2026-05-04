package com.example.backend.Schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Getter @Setter
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    private String classroomId;   // 공1201

    private String day;           // MON, TUE

    private LocalTime startTime;
    private LocalTime endTime;

    private String subject;
    private String professor;
}