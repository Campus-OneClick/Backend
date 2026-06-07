package com.example.backend.User;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FirebaseAuthService firebaseAuthService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestHeader("Authorization") String authorization) {
        try {
            FirebaseUserContext firebaseUser = firebaseAuthService.verifyAuthorizationHeader(authorization);
            UserEntity user = userService.loginWithFirebase(firebaseUser);

            return Map.of(
                    "success", true,
                    "user", user
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestHeader("Authorization") String authorization,
                                      @RequestBody SignupRequest request) {
        try {
            FirebaseUserContext firebaseUser = firebaseAuthService.verifyAuthorizationHeader(authorization);
            UserEntity user = userService.signupWithFirebase(firebaseUser, request);
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

    @GetMapping("/users/check-email")
    public Map<String, Object> checkEmail(@RequestParam String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return Map.of(
                    "success", true,
                    "exists", exists
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }

    @GetMapping("/users/check-student-id")
    public Map<String, Object> checkStudentId(@RequestParam String studentId) {
        try {
            boolean exists = userService.existsByStudentId(studentId);
            return Map.of(
                    "success", true,
                    "exists", exists
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }

    @GetMapping("/users/find-email")
    public Map<String, Object> findEmail(@RequestParam String studentId,
                                         @RequestParam String name) {
        try {
            String email = userService.findEmailByStudentIdAndName(studentId, name);
            return Map.of(
                    "success", true,
                    "email", email
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }

}
