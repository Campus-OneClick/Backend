package com.example.backend.Classroom;

import com.example.backend.Reservation.ReservationEntity;
import com.example.backend.Reservation.ReservationRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ReservationRepository reservationRepository;

    public ClassroomService(ClassroomRepository classroomRepository, ReservationRepository reservationRepository) {
        this.classroomRepository = classroomRepository;
        this.reservationRepository = reservationRepository;
    }

    private static final Map<String, String> DAY_KO = Map.of(
            "MON", "월", "TUE", "화", "WED", "수", "THU", "목", "FRI", "금"
    );

    public ClassroomEntity create(ClassroomEntity classroomEntity) {
        if (classroomEntity.getClassroomId() == null) {
            throw new IllegalArgumentException("classroomId는 필수입니다.");
        }
        if (classroomEntity.getRoomName() == null || classroomEntity.getRoomName().isBlank()) {
            throw new IllegalArgumentException("roomName은 필수입니다.");
        }
        if (classroomRepository.existsById(classroomEntity.getClassroomId())) {
            throw new IllegalArgumentException("이미 존재하는 classroomId 입니다.");
        }
        return classroomRepository.save(classroomEntity);
    }

    public void delete(String id) {
        if (!classroomRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 강의실입니다.");
        }
        classroomRepository.deleteById(id);
    }

    public ReservationEntity reserve(ClassroomReserveRequest req) {
        if (req.studentId() == null || req.studentId().isBlank()) {
            throw new IllegalArgumentException("studentId는 필수입니다.");
        }
        if (req.roomId() == null || req.roomId().isBlank()) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }

        int nextNum = reservationRepository.findMaxNumByType("lecture")
                .map(n -> n + 1)
                .orElse(1);

        String dayKo = DAY_KO.getOrDefault(req.day(), req.day());
        String timeRange = req.startTime() + " ~ " + req.endTime();
        ZonedDateTime kstNow = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        String now = kstNow.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
        String dateStr = kstNow.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        ReservationEntity entity = new ReservationEntity(
                null, "lecture", nextNum, req.studentId(),
                req.roomId(), dateStr, dayKo, timeRange, 0, now, null, null, req.memo()
        );
        return reservationRepository.save(entity);
    }

    public List<ReservationEntity> getReservationsByRoom(String roomId) {
        return reservationRepository.findByTypeAndClassroomId("lecture", roomId)
                .stream()
                .filter(r -> r.getStatus() != 2)
                .toList();
    }

    public List<ClassroomEntity> findAll() {
        return classroomRepository.findAll();
    }

    public ClassroomEntity findById(String id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("not found"));
    }
}
