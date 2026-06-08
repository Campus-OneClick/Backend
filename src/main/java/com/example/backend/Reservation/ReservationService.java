package com.example.backend.Reservation;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RejectionLogRepository rejectionLogRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @PostConstruct
    public void seedReservations() {
        if (reservationRepository.count() > 0) {
            return;
        }

        reservationRepository.save(new ReservationEntity(null, "lecture", 1, "5711111", "공1201", "2026/05/10", "월", "13:00 ~ 14:00", 0, "05/19 14:22", null, null, null));
        reservationRepository.save(new ReservationEntity(null, "lecture", 2, "5633333", "공1202", "2026/05/11", "수", "14:00 ~ 15:30", 0, "05/19 15:10", null, null, null));
    }

    public List<ReservationEntity> findAll() {
        return reservationRepository.findAllByOrderByTypeAscNumAsc();
    }

    public ReservationEntity create(ReservationEntity reservationEntity) {
        validate(reservationEntity);
        if (reservationRepository.existsByTypeAndNum(reservationEntity.getType(), reservationEntity.getNum())) {
            throw new IllegalArgumentException("이미 존재하는 예약 번호입니다.");
        }
        return reservationRepository.save(reservationEntity);
    }

    @Transactional
    public ReservationEntity updateStatus(String type, Integer num, Integer status, String rejectionReason) {
        ReservationEntity reservation = findByTypeAndNum(type, num);
        reservation.setStatus(status);
        if (status == 0) {
            reservation.setProcessedAt(null);
            reservation.setProcessedTimestamp(null);
        } else {
            reservation.setProcessedAt(currentTimeString());
            reservation.setProcessedTimestamp(LocalDateTime.now(KST));
            if (status == 2) {
                // 거절: 사용자 알림용으로 RejectionLog에도 저장
                // reservations 레코드는 관리자 처리완료 탭에서 볼 수 있도록 유지 (7일 후 스케줄러가 삭제)
                rejectionLogRepository.save(new RejectionLog(
                        null,
                        reservation.getUser(),
                        reservation.getClassroomId(),
                        reservation.getDay(),
                        reservation.getTime(),
                        rejectionReason,
                        currentTimeString()
                ));
            }
        }
        return reservationRepository.save(reservation);
    }

    public List<RejectionLog> findRejectedByUser(String studentId) {
        return rejectionLogRepository.findByStudentId(studentId);
    }

    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void deleteOldProcessedReservations() {
        LocalDateTime cutoff = LocalDateTime.now(KST).minusDays(7);
        List<ReservationEntity> old = reservationRepository
                .findByStatusNotAndProcessedTimestampBefore(0, cutoff);
        reservationRepository.deleteAll(old);
    }

    public void delete(String type, Integer num) {
        ReservationEntity reservation = findByTypeAndNum(type, num);
        reservationRepository.delete(reservation);
    }

    private ReservationEntity findByTypeAndNum(String type, Integer num) {
        return reservationRepository.findByTypeAndNum(type, num)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
    }

    private void validate(ReservationEntity reservationEntity) {
        if (reservationEntity == null) {
            throw new IllegalArgumentException("예약 정보가 필요합니다.");
        }
        if (reservationEntity.getType() == null || reservationEntity.getType().isBlank()) {
            throw new IllegalArgumentException("type은 필수입니다.");
        }
        if (reservationEntity.getNum() == null) {
            throw new IllegalArgumentException("num은 필수입니다.");
        }
        if (reservationEntity.getUser() == null || reservationEntity.getUser().isBlank()) {
            throw new IllegalArgumentException("user는 필수입니다.");
        }
        if (reservationEntity.getStatus() == null) {
            throw new IllegalArgumentException("status는 필수입니다.");
        }
    }

    private String currentTimeString() {
        return ZonedDateTime.now(KST).format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
    }
}
