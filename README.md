# 📦 Backend

이 레포지토리는 캠퍼스 내 **빈 강의실 조회 및 시간표 관리 시스템**을 위한 서버입니다.
Spring Boot와 PostgreSQL을 기반으로 REST API를 제공하며,
사용자 인증, 강의실 관리, 시간표 기반 공실 조회 기능을 담당합니다.

---

## 🛠 Tech Stack

### Backend

* Java 21
* Spring Boot 4.0.6
* Spring Data JPA
* Hibernate ORM 7.2.12
* Lombok (annotation processing)
* Gradle

### Database

* PostgreSQL 18.3

### Tools

* VSCode
* Postman (API 테스트)
* pgAdmin (DB 관리)

---

## 🧩 Core Features

### 🔐 Authentication & User Management

* ✅ 학번 기반 로그인
* ✅ 회원가입 (신규 사용자 등록)
* ✅ 계정 삭제
* ✅ 사용자 권한 구분 (ADMIN / USER)

---

### 🏫 Classroom (강의실)

* ✅ 강의실 생성
* ✅ 강의실 목록 조회
* ✅ 강의실 상세 조회
* ✅ 강의실 정보 수정
* ✅ 강의실 삭제

---

### 📅 Schedule (시간표)

* ✅ 시간표 등록 (강의실 중복 체크)
* ✅ 시간표 전체 조회
* ✅ 시간표 상세 조회
* ✅ 시간표 수정
* ✅ 시간표 삭제
* ✅ 🔥 **시간 겹침 방지 로직** (같은 강의실, 같은 요일, 시간 겹치면 등록 불가)
* ✅ 요일 제한 (MON ~ FRI만 가능, enum으로 타입 안전성 확보)

---

### 🪑 Seat (열람실 좌석) - TODO

* ⏳ 좌석 상태 조회
* ⏳ 좌석 사용 / 자리비움 / 비어있음 상태 관리
* ⏳ 사용자 기반 좌석 점유

---

### 🔍 Empty Classroom - TODO

* ⏳ 특정 시간 기준 빈 강의실 조회
* ⏳ 실시간 사용 상태 반영

---

## 🗄 Database Schema Overview

### User

| 컬럼         | 타입      | 설명                 |
| ---------- | ------- | ------------------ |
| student_id | VARCHAR | 학번 (PK)            |
| name       | VARCHAR | 이름                 |
| role       | VARCHAR | ADMIN / USER (enum) |

---

### Classroom

| 컬럼           | 타입      | 설명               |
| ------------ | ------- | ---------------- |
| classroom_id | VARCHAR | 강의실 ID (PK)     |
| room_name    | VARCHAR | 강의실 이름 (예: 공1201) |

---

### Schedule

| 컬럼           | 타입      | 설명                      |
| ------------ | ------- | ----------------------- |
| schedule_id  | BIGINT  | PK (Auto increment)     |
| classroom_id | VARCHAR | FK → Classroom (SERIAL) |
| day          | VARCHAR | MON / TUE / WED / THU / FRI (enum) |
| start_time   | TIME    | 시작 시간 (예: 09:00)      |
| end_time     | TIME    | 종료 시간 (예: 10:30)      |
| subject      | VARCHAR | 과목명                    |
| professor    | VARCHAR | 교수명                    |

**제약조건**: classroom_id + day + start_time + end_time이 충돌하면 INSERT 불가

---

### Seat - TODO

| 컬럼          | 타입        | 설명                   |
| ----------- | --------- | -------------------- |
| seat_id     | BIGINT    | PK                   |
| seat_number | INT       | 좌석 번호                |
| status      | VARCHAR   | empty / using / away |
| user_id     | VARCHAR   | 사용자                  |
| start_time  | TIMESTAMP | 사용 시작                |

---

## 🔗 API Overview

### 🔐 Auth

| Method | Endpoint | 설명 | 입력 |
|--------|----------|------|------|
| POST | `/login` | 로그인 | `{"studentId": "1111111"}` |
| POST | `/signup` | 회원가입 | `{"studentId": "2222222", "name": "김철수"}` |
| DELETE | `/users/{studentId}` | 계정 삭제 | - |

---

### 🏫 Classroom

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/classrooms` | 강의실 목록 조회 |
| GET | `/classrooms/{id}` | 강의실 상세 조회 |
| POST | `/classrooms` | 강의실 생성 |
| PUT | `/classrooms/{id}` | 강의실 수정 |
| DELETE | `/classrooms/{id}` | 강의실 삭제 |

**요청 예시** (생성):
```json
{
  "classroomId": "1201",
  "roomName": "공1201"
}
```

---

### 📅 Schedule

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/schedules` | 시간표 목록 조회 |
| GET | `/schedules/{id}` | 시간표 상세 조회 |
| POST | `/schedules` | 시간표 등록 |
| PUT | `/schedules/{id}` | 시간표 수정 |
| DELETE | `/schedules/{id}` | 시간표 삭제 |

**요청 예시** (생성):
```json
{
  "classroomId": "1201",
  "day": "MON",
  "startTime": "09:00",
  "endTime": "10:30",
  "subject": "자료구조",
  "professor": "김교수"
}
```

**응답 예시**:
```json
{
  "success": true,
  "data": {
    "scheduleId": 3,
    "classroomEntity": {
      "classroomId": "1201",
      "roomName": "공1201"
    },
    "day": "MON",
    "startTime": "09:00:00",
    "endTime": "10:30:00",
    "subject": "자료구조",
    "professor": "김교수"
  }
}
```

**에러 응답**:
```json
{
  "success": false,
  "message": "이미 해당 시간에 강의가 존재합니다"
}
```

---

### 🪑 Seat - TODO

* `GET /seats` → 좌석 조회
* `POST /seats/use` → 좌석 사용
* `POST /seats/leave` → 자리 비움

---

### 🔍 Empty Classroom - TODO

* `GET /classrooms/empty?time=09:00&day=MON` → 빈 강의실 조회

---

## ⚙️ Setup

### 1️⃣ DB 생성

```sql
CREATE DATABASE campus;
```

---

### 2️⃣ application.properties 설정

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/campus
spring.datasource.username=postgres
spring.datasource.password=1234
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

---

### 3️⃣ 데이터 초기화 (선택사항)

`src/main/resources/data.sql` 참조:
```sql
-- data.sql: 초기 사용자 데이터
INSERT INTO users (student_id, name, role)
VALUES ('admin', '관리자', 'ADMIN')
ON CONFLICT (student_id) DO NOTHING;
```

서버 재시작 시 자동 실행됩니다. (기존 데이터는 삭제되지 않음)

---

### 4️⃣ 실행

```bash
./gradlew bootRun
```

서버는 `http://localhost:8080`에서 실행됩니다.

---

## 🧠 Design Notes

* **시간표 충돌 방지**: 같은 강의실, 같은 요일에서 시간이 겹치면 등록 불가능
* **요일 제한**: DayOfWeek enum으로 MON~FRI만 허용하여 타입 안전성 확보
* **데이터 일관성**: 강의실 삭제 전 관련 시간표 확인 로직 필요 (향후 개선)
* **간단한 인증**: JWT 미사용, 학번 기반의 간단한 인증으로 프로토타입 단계 구현

---

## 🚀 Future Improvements

* ✅ JWT 기반 인증 (로그인 토큰화)
* ✅ 열람실 좌석 관리 (Seat CRUD)
* ✅ 빈 강의실 실시간 조회 API
* ✅ 건물 확장 (다중 건물 지원)
* ✅ 실시간 좌석 상태 (WebSocket)
* ✅ 모바일 앱 연동
* ✅ 예약 시스템 (강의실 / 좌석)

---

## 📝 Notes

* data.sql: `DELETE FROM users` 제거됨 → `ON CONFLICT DO NOTHING` 사용으로 안전한 초기화
* 서버 재시작 시 data.sql이 자동 실행되어 초기 데이터 유지
* PostgreSQL SERIAL 사용으로 scheduleId는 전역 자동 증가 (강의실별 리셋 X)
