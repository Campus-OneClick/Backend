package com.example.backend.User;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {

        return userRepository.findById(request.getStudentId())
                .map(user -> Map.of(
                        "success", true,
                        "user", user
                ))
                .orElse(Map.of(
                        "success", false
                ));
    }

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody SignupRequest request) {
        try {
            UserEntity user = userService.signup(request);
            return Map.of(
                    "success", true,
                    "user", user
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }

    @DeleteMapping("/users/{studentId}")
    public Map<String, Object> deleteUser(@PathVariable String studentId) {
        try {
            userService.deleteUser(studentId);
            return Map.of("success", true);
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }
}