package com.example.backend.Schedule;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DayOfWeek {
    MON("월요일"),
    TUE("화요일"),
    WED("수요일"),
    THU("목요일"),
    FRI("금요일");

    private final String description;

    DayOfWeek(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static DayOfWeek from(String value) {
        if (value == null) return null;
        String v = value.trim().toUpperCase();
        switch (v) {
            case "MON":
            case "MONDAY":
            case "월요일":
                return MON;
            case "TUE":
            case "TUES":
            case "TUESDAY":
            case "화요일":
                return TUE;
            case "WED":
            case "WEDNESDAY":
            case "수요일":
                return WED;
            case "THU":
            case "THUR":
            case "THURSDAY":
            case "목요일":
                return THU;
            case "FRI":
            case "FRIDAY":
            case "금요일":
                return FRI;
            default:
                throw new IllegalArgumentException("Unknown DayOfWeek: " + value);
        }
    }
}
