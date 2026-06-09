package com.example.backend.RoomUsage;

import com.example.backend.Classroom.ClassroomEntity;
import com.example.backend.Classroom.ClassroomRepository;
import com.example.backend.Reservation.ReservationRepository;
import com.example.backend.Schedule.DayOfWeek;
import com.example.backend.Schedule.ScheduleService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomUsageService {

    private final ClassroomRepository classroomRepository;
    private final ScheduleService scheduleService;
    private final ReservationRepository reservationRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private static final Map<java.time.DayOfWeek, String> DAY_KO = Map.of(
            java.time.DayOfWeek.MONDAY, "월",
            java.time.DayOfWeek.TUESDAY, "화",
            java.time.DayOfWeek.WEDNESDAY, "수",
            java.time.DayOfWeek.THURSDAY, "목",
            java.time.DayOfWeek.FRIDAY, "금"
    );

    public Map<String, String> getStatusForRoom(String roomName) {
        java.time.DayOfWeek javaDow = LocalDate.now(KST).getDayOfWeek();
        LocalTime now = LocalTime.now(KST).withSecond(0).withNano(0);
        String dayKo = DAY_KO.get(javaDow);

        if (dayKo == null) {
            return buildEntry(roomName, "FREE");
        }

        DayOfWeek day = DayOfWeek.from(javaDow.toString());

        ClassroomEntity classroom = classroomRepository.findByRoomName(roomName).orElse(null);
        boolean occupied = false;
        if (classroom != null) {
            occupied = scheduleService.isOccupied(classroom.getClassroomId(), day, now)
                    || hasActiveReservation(roomName, dayKo, now);
        }

        return buildEntry(roomName, occupied ? "USED" : "FREE");
    }

    private boolean hasActiveReservation(String roomName, String dayKo, LocalTime now) {
        return reservationRepository.findByTypeAndLecture("lecture", roomName).stream()
                .filter(r -> r.getStatus() == 1)
                .filter(r -> dayKo.equals(r.getDay()))
                .anyMatch(r -> isTimeInRange(r.getTime(), now));
    }

    private boolean isTimeInRange(String timeRange, LocalTime now) {
        try {
            String[] parts = timeRange.split(" ~ ");
            LocalTime start = LocalTime.parse(parts[0].trim());
            LocalTime end = LocalTime.parse(parts[1].trim());
            return !now.isBefore(start) && now.isBefore(end);
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, String> buildEntry(String roomName, String status) {
        Map<String, String> m = new HashMap<>();
        m.put("roomName", roomName);
        m.put("status", status);
        return m;
    }
}
