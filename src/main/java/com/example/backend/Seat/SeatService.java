package com.example.backend.Seat;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    @PostConstruct
    public void initializeSeats() {
        if (seatRepository.count() > 0) {
            return;
        }

        List<SeatEntity> seats = new ArrayList<>();
        for (int seatNumber = 1; seatNumber <= 38; seatNumber++) {
            seats.add(new SeatEntity(null, "center", seatNumber, SeatStatus.EMPTY, null, null, null));
        }
        for (int seatNumber = 1; seatNumber <= 40; seatNumber++) {
            seats.add(new SeatEntity(null, "side", seatNumber, SeatStatus.EMPTY, null, null, null));
        }
        seatRepository.saveAll(seats);
    }

    public Map<String, Object> getSummary(String studentId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("centerSeats", seatNumbersByLounge("center"));
        response.put("sideSeats", seatNumbersByLounge("side"));
        response.put("availableSeats", seatRepository.countByStatus(SeatStatus.EMPTY));
        response.put("mySeat", findMySeat(studentId));
        return response;
    }

    public Map<String, Object> reserveSeat(SeatRequest request) {
        SeatEntity seat = findTargetSeat(request);
        validateStudentId(request.studentId());

        List<SeatEntity> activeSeats = seatRepository.findByStudentIdAndStatusNot(request.studentId(), SeatStatus.EMPTY);
        if (!activeSeats.isEmpty()) {
            SeatEntity currentSeat = activeSeats.get(0);
            throw new IllegalArgumentException("이미 " + currentSeat.getLounge() + " 라운지 " + currentSeat.getSeatNumber() + "번 좌석을 이용 중입니다.");
        }

        if (seat.getStatus() != SeatStatus.EMPTY) {
            throw new IllegalArgumentException("이미 예약된 좌석입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        seat.setStatus(SeatStatus.USING);
        seat.setStudentId(request.studentId());
        seat.setStartTime(now);
        seat.setEndTime(now.plusHours(2));
        seatRepository.save(seat);

        return Map.of(
                "success", true,
                "message", "좌석 배정이 완료되었습니다.",
                "seat", seatToMap(seat),
                "summary", getSummary(request.studentId())
        );
    }

    public Map<String, Object> returnSeat(SeatRequest request) {
        SeatEntity seat = findTargetSeat(request);
        validateStudentId(request.studentId());
        ensureOwnedByCurrentUser(seat, request.studentId());

        seat.setStatus(SeatStatus.EMPTY);
        seat.setStudentId(null);
        seat.setStartTime(null);
        seat.setEndTime(null);
        seatRepository.save(seat);

        return Map.of(
                "success", true,
                "message", "반납이 완료되었습니다.",
                "summary", getSummary(request.studentId())
        );
    }

    public Map<String, Object> extendSeat(SeatRequest request) {
        SeatEntity seat = findTargetSeat(request);
        validateStudentId(request.studentId());
        ensureOwnedByCurrentUser(seat, request.studentId());

        LocalDateTime now = LocalDateTime.now();
        if (seat.getEndTime() != null && Duration.between(now, seat.getEndTime()).toMinutes() > 60) {
            throw new IllegalArgumentException("퇴실 1시간 전부터 연장이 가능합니다.");
        }

        seat.setEndTime((seat.getEndTime() == null ? now : seat.getEndTime()).plusHours(1));
        seatRepository.save(seat);

        return Map.of(
                "success", true,
                "message", "이용 시간이 1시간 연장되었습니다.",
                "seat", seatToMap(seat),
                "summary", getSummary(request.studentId())
        );
    }

    private SeatEntity findTargetSeat(SeatRequest request) {
        if (request == null || request.lounge() == null || request.seatId() == null) {
            throw new IllegalArgumentException("lounge와 seatId는 필수입니다.");
        }

        String lounge = request.lounge().trim().toLowerCase();
        return seatRepository.findByLoungeAndSeatNumber(lounge, request.seatId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));
    }

    private void validateStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("studentId는 필수입니다.");
        }
    }

    private void ensureOwnedByCurrentUser(SeatEntity seat, String studentId) {
        if (seat.getStatus() == SeatStatus.EMPTY || seat.getStudentId() == null || !seat.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException("해당 좌석을 이용 중인 사용자가 아닙니다.");
        }
    }

    private List<Integer> seatNumbersByLounge(String lounge) {
        return seatRepository.findByLoungeAndStatus(lounge, SeatStatus.USING)
                .stream()
                .map(SeatEntity::getSeatNumber)
                .toList();
    }

    private Map<String, Object> findMySeat(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return null;
        }

        return seatRepository.findByStudentIdAndStatusNot(studentId, SeatStatus.EMPTY)
                .stream()
                .findFirst()
                .map(this::seatToMap)
                .orElse(null);
    }

    private Map<String, Object> seatToMap(SeatEntity seat) {
        Map<String, Object> seatMap = new LinkedHashMap<>();
        seatMap.put("seatId", seat.getSeatNumber());
        seatMap.put("lounge", seat.getLounge());
        seatMap.put("status", seat.getStatus().name().toLowerCase());
        seatMap.put("studentId", seat.getStudentId());
        seatMap.put("startTime", seat.getStartTime() == null ? null : seat.getStartTime().toString());
        seatMap.put("endTime", seat.getEndTime() == null ? null : seat.getEndTime().toString());
        return seatMap;
    }
}