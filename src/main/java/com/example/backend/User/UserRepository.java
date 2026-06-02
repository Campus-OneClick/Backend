package com.example.backend.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<UserEntity, String> {

        Optional<UserEntity> findByEmail(String email);

        boolean existsByEmailIgnoreCase(String email);

        Optional<UserEntity> findByFirebaseUid(String firebaseUid);
}