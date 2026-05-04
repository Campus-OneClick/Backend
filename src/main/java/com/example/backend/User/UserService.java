package com.example.backend.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserEntity signup(SignupRequest request) {
        // 중복 체크
        if (userRepository.existsById(request.getStudentId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        
        // 새 사용자 생성
        UserEntity user = new UserEntity();
        user.setStudentId(request.getStudentId());
        user.setName(request.getName());
        user.setRole(UserRole.USER);  // 기본 역할은 USER
        
        return userRepository.save(user);
    }

    public void deleteUser(String studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        userRepository.deleteById(studentId);
    }
}
