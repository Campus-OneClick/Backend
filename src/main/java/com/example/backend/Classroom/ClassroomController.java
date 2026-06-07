package com.example.backend.Classroom;

import com.example.backend.Reservation.ReservationEntity;
import com.example.backend.Schedule.DayOfWeek;
import com.example.backend.Schedule.ScheduleService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;
    private final ScheduleService scheduleService;

    public ClassroomController(ClassroomService classroomService, ScheduleService scheduleService) {
        this.classroomService = classroomService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/status")
    public List<Map<String, Object>> status(String day, String time) {
        DayOfWeek d;
        LocalTime t;
        if (day == null || time == null) {
            java.time.DayOfWeek jd = LocalDate.now().getDayOfWeek();
            d = DayOfWeek.from(jd.toString());
            t = LocalTime.now().withSecond(0).withNano(0);
        } else {
            d = DayOfWeek.from(day);
            t = LocalTime.parse(time);
        }

        List<ClassroomEntity> rooms = classroomService.findAll();
        return rooms.stream()
                .map(c -> {
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("classroomId", c.getClassroomId());
                    m.put("roomName", c.getRoomName());
                    m.put("isOccupied", scheduleService.isOccupied(c.getClassroomId(), d, t));
                    return m;
                })
                .toList();
    }

    @GetMapping
    public List<ClassroomEntity> getAll() {
        return classroomService.findAll();
    }

    @GetMapping("/{id}")
    public ClassroomEntity getOne(@PathVariable String id) {
        return classroomService.findById(id);
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody ClassroomEntity classroomEntity) {
        try {
            ClassroomEntity saved = classroomService.create(classroomEntity);
            return Map.of("success", true, "data", saved);
        } catch (IllegalArgumentException e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable String id) {
        try {
            classroomService.delete(id);
            return Map.of("success", true);
        } catch (IllegalArgumentException e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @PostMapping("/reserve")
    public Map<String, Object> reserve(@RequestBody ClassroomReserveRequest request) {
        try {
            ReservationEntity saved = classroomService.reserve(request);
            return Map.of("success", true, "data", saved);
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @GetMapping("/{id}/reservations")
    public List<ReservationEntity> getReservations(@PathVariable String id) {
        return classroomService.getReservationsByRoom(id);
    }
}