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

    @Enumerated(EnumType.STRING)
    private UserRole role;
}