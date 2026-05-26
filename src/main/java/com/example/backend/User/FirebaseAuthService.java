package com.example.backend.User;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class FirebaseAuthService {

    private FirebaseAuth firebaseAuth;

    @Value("${firebase.service-account-path:}")
    private String serviceAccountPath;

    public FirebaseUserContext verifyAuthorizationHeader(String authorizationHeader) {
        String idToken = extractBearerToken(authorizationHeader);
        FirebaseToken decodedToken;

        try {
            decodedToken = getFirebaseAuth().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new IllegalArgumentException("Firebase 토큰 검증에 실패했습니다.", e);
        }

        return new FirebaseUserContext(decodedToken.getUid(), decodedToken.getEmail());
    }

    private synchronized FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth != null) {
            return firebaseAuth;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(loadCredentials())
                        .build();
                FirebaseApp.initializeApp(options);
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Firebase 관리자 인증 정보를 찾을 수 없습니다. GOOGLE_APPLICATION_CREDENTIALS 또는 firebase.service-account-path를 설정하세요.",
                        e
                );
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth;
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Authorization 헤더가 필요합니다.");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Bearer 토큰 형식이 아닙니다.");
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Firebase 토큰이 비어있습니다.");
        }

        return token;
    }

    private GoogleCredentials loadCredentials() throws IOException {
        String jsonCredentials = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
        if (jsonCredentials != null && !jsonCredentials.isBlank()) {
            try (InputStream inputStream = new ByteArrayInputStream(jsonCredentials.trim().getBytes(StandardCharsets.UTF_8))) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        String jsonCredentialsBase64 = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON_BASE64");
        if (jsonCredentialsBase64 != null && !jsonCredentialsBase64.isBlank()) {
            byte[] decoded = Base64.getDecoder().decode(jsonCredentialsBase64.trim());
            try (InputStream inputStream = new ByteArrayInputStream(decoded)) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        String googleCredentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        if (googleCredentialsPath != null && !googleCredentialsPath.isBlank()) {
            Path path = Paths.get(googleCredentialsPath.trim());
            if (!Files.exists(path)) {
                throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS 경로가 존재하지 않습니다: " + path);
            }

            try (InputStream inputStream = Files.newInputStream(path)) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        if (serviceAccountPath != null && !serviceAccountPath.isBlank()) {
            String configuredPath = serviceAccountPath.trim();

            if (configuredPath.startsWith("classpath:")) {
                String classpathLocation = configuredPath.substring("classpath:".length()).trim();
                ClassPathResource resource = new ClassPathResource(classpathLocation);
                if (!resource.exists()) {
                    throw new IllegalStateException("firebase.service-account-path 클래스패스 리소스를 찾을 수 없습니다: " + classpathLocation);
                }

                try (InputStream inputStream = resource.getInputStream()) {
                    return GoogleCredentials.fromStream(inputStream);
                }
            }

            Path path = Paths.get(configuredPath);
            if (!Files.exists(path)) {
                throw new IllegalStateException("firebase.service-account-path 파일이 존재하지 않습니다: " + path);
            }

            try (InputStream inputStream = Files.newInputStream(path)) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        ClassPathResource fallbackResource = new ClassPathResource("firebase-service-account.json");
        if (fallbackResource.exists()) {
            try (InputStream inputStream = fallbackResource.getInputStream()) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        throw new IllegalStateException(
            "Firebase 관리자 인증 정보를 찾을 수 없습니다. FIREBASE_SERVICE_ACCOUNT_JSON, FIREBASE_SERVICE_ACCOUNT_JSON_BASE64, GOOGLE_APPLICATION_CREDENTIALS 또는 firebase.service-account-path를 설정하세요."
        );
    }
}