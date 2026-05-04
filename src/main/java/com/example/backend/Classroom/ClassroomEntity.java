package com.example.backend.Classroom;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "classrooms")
@Getter @Setter

public class ClassroomEntity {

    @Id
    private String classroomId; // 공1201, 1201 등
    private String roomName; // 공1201..
}