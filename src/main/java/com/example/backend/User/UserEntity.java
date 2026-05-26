package com.example.backend.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Table(name = "users")
public class UserEntity {

    @Id
    private String studentId;

    private String name;

    private String department;

    @Column(unique = true)
    private String email;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}