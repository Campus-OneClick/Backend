package com.example.backend.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserEntity loginWithFirebase(FirebaseUserContext firebaseUser) {
        return userRepository.findByFirebaseUid(firebaseUser.uid())
                .or(() -> userRepository.findByEmail(firebaseUser.email()))
                .orElseThrow(() -> new IllegalArgumentException("가입된 사용자가 없습니다. 먼저 회원가입을 진행해주세요."));
    }

    public UserEntity signupWithFirebase(FirebaseUserContext firebaseUser, SignupRequest request) {
        String studentId = normalize(request.getStudentId());
        String name = normalize(request.getName());
        String department = normalize(request.getDepartment());

        if (studentId == null || name == null || department == null) {
            throw new IllegalArgumentException("학번, 이름, 학과를 모두 입력해주세요.");
        }

        String firebaseUid = normalize(firebaseUser.uid());
        String email = normalize(firebaseUser.email());

        if (firebaseUid == null || email == null) {
            throw new IllegalArgumentException("Firebase 계정 정보를 확인할 수 없습니다.");
        }

        userRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getStudentId().equals(studentId)) {
                throw new IllegalArgumentException("이미 다른 학번으로 등록된 이메일입니다.");
            }
        });

        userRepository.findByFirebaseUid(firebaseUid).ifPresent(existing -> {
            if (!existing.getStudentId().equals(studentId)) {
                throw new IllegalArgumentException("이미 다른 학번으로 연결된 Firebase 계정입니다.");
            }
        });

        UserEntity existingUser = userRepository.findByFirebaseUid(firebaseUid)
                .or(() -> userRepository.findByEmail(email))
                .orElseGet(() -> userRepository.findById(studentId).orElse(null));

        if (existingUser != null && !existingUser.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException("이미 다른 학번으로 연결된 Firebase 계정입니다.");
        }

        UserEntity user = existingUser != null ? existingUser : new UserEntity();
        user.setStudentId(studentId);
        user.setName(name);
        user.setDepartment(department);
        user.setEmail(email);
        user.setFirebaseUid(firebaseUid);
        user.setRole(user.getRole() == null ? UserRole.USER : user.getRole());

        return userRepository.save(user);
    }

    public void deleteUser(String studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        userRepository.deleteById(studentId);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
