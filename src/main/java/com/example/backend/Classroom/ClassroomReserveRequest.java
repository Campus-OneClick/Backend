package com.example.backend.Classroom;

public record ClassroomReserveRequest(
        String roomId,
        String day,       // MON, TUE, WED, THU, FRI
        String date,      // "yyyy/MM/dd" - 선택한 슬롯의 실제 날짜
        String startTime, // "9:00"
        String endTime,   // "10:30"
        String studentId,
        String memo
) {}
