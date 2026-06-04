# Build stage
FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace
COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
COPY src src
RUN ./gradlew bootJar -x test

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
