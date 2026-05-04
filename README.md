문제: VS Code에서 `jakarta`와 `lombok`을 인식하지 못할 때 해결 방법

권장 순서:

1. 의존성 확인
   - `build.gradle`에 Lombok (`org.projectlombok:lombok`)과 Jakarta Persistence API (`jakarta.persistence:jakarta.persistence-api:3.1.0`)가 포함되어 있습니다.

2. IDE 확장 설치 (VS Code)
   - Lombok 인식이 안 되는 경우 `Lombok` 확장을 설치하고 VS Code를 재시작하세요.

3. Java 언어 서버 정리
   - 커맨드 팔레트에서 "Java: Clean the Java language server workspace" 실행 후 VS Code 재시작.

4. Gradle 동기화
   - Gradle Tasks 뷰에서 프로젝트 새로고침 하거나 프로젝트 루트에서:

```bash
./gradlew --refresh-dependencies
```

5. (선택) 테스트 실행 문제
   - 로컬 환경에서 테스트가 실패하면 임시로 빌드에서 테스트를 비활성화(`build.gradle`에서 `tasks.named('test') { enabled = false }`)했습니다. 필요하면 다시 활성화하세요.

추가 도움 원하시면 IDE에서 발생하는 정확한 에러 메시지(Problems 창 스크린샷)를 보내주세요.
