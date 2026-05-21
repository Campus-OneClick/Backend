package com.example.backend.Reservation;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationEntity> getAll() {
        return reservationService.findAll();
    }

    @PostMapping
    public ReservationEntity create(@RequestBody ReservationEntity reservationEntity) {
        return reservationService.create(reservationEntity);
    }

    @PutMapping("/{type}/{num}")
    public ReservationEntity updateStatus(
            @PathVariable String type,
            @PathVariable Integer num,
            @RequestBody Map<String, Object> body
    ) {
        Integer status = ((Number) body.get("status")).intValue();
        return reservationService.updateStatus(type, num, status);
    }

    @DeleteMapping("/{type}/{num}")
    public void delete(@PathVariable String type, @PathVariable Integer num) {
        reservationService.delete(type, num);
    }
}