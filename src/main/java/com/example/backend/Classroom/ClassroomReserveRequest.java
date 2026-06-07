package com.example.backend.Classroom;

public record ClassroomReserveRequest(
        String roomId,
        String day,       // MON, TUE, WED, THU, FRI
        String startTime, // "9:00"
        String endTime,   // "10:30"
        String studentId,
        String memo
) {}
