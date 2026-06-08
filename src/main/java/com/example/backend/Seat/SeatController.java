package com.example.backend.Seat;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seats")
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public Map<String, Object> getSeats(@RequestParam(required = false) String studentId) {
        return seatService.getSummary(studentId);
    }

    @PostMapping("/use")
    public Map<String, Object> useSeat(@RequestBody SeatRequest request) {
        return seatService.reserveSeat(request);
    }

    @PostMapping("/end")
    public Map<String, Object> endSeat(@RequestBody SeatRequest request) {
        return seatService.returnSeat(request);
    }

    @PostMapping("/extend")
    public Map<String, Object> extendSeat(@RequestBody SeatRequest request) {
        return seatService.extendSeat(request);
    }

    @GetMapping("/admin/active")
    public List<Map<String, Object>> getActiveSeats() {
        return seatService.getActiveSeats();
    }

    @PostMapping("/admin/force-release")
    public Map<String, Object> forceReleaseSeat(@RequestBody Map<String, Object> body) {
        String lounge = (String) body.get("lounge");
        Integer seatId = (Integer) body.get("seatId");
        return seatService.forceReleaseSeat(lounge, seatId);
    }
}