package com.example.backend.Classroom;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

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

    public List<ClassroomEntity> findAll() {
        return classroomRepository.findAll();
    }

    public ClassroomEntity findById(String id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("not found"));
    }
}