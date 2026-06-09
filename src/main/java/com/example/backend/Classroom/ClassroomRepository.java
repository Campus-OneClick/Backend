package com.example.backend.Classroom;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<ClassroomEntity, String> {

    java.util.Optional<ClassroomEntity> findByRoomName(String roomName);
}