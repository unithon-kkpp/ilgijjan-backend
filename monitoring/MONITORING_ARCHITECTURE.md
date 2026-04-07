# ilgijjan 모니터링/로깅 시스템 아키텍처

## 전체 구조

```
[ Spring Boot - prod VM ]
        │
        ├─ 콘솔 출력 (stdout)
        ├─ /logs/spring.log (파일, 7일 롤링)
        └─ ERROR 로그 → Discord 웹훅 (즉시, 비동기)

[ Promtail - prod VM ]
        │  /var/log/ilgijjan/*.log 읽기
        │  (docker-compose에서 ./logs:/var/log/ilgijjan 마운트)
        ↓
[ Loki - monitoring VM :3100 ]
        │  청크를 GCS(ilgijjan-490801-logs)에 저장
        ↓
[ Grafana - monitoring VM :3000 ]
        ├─ ilgijjan Logs 대시보드 (Loki 조회)
        └─ 서버 다운 알림 (Prometheus 조회 → Discord)

[ Prometheus - monitoring VM :9090 ]
        │  15초마다 prod VM /actuator/prometheus 스크랩
        └─ Spring Boot 메트릭 수집 (JVM, HTTP, DB 등)
```

---

## 1. 애플리케이션 로깅 (Spring Boot)

**설정 파일:** `src/main/resources/logback-spring.xml`

### 로그 패턴
```
%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%X{requestId:-no-id}] --- [%t] %-40.40logger{39} : %m%n
```
- `requestId`: 요청마다 생성되는 8자리 트레이스 ID (MDC)
- `userId`: 인증된 사용자 ID (MDC, 미인증 시 "guest")

### Appender 구성

| Appender | 역할 | 조건 |
|----------|------|------|
| CONSOLE | stdout 출력 | 항상 |
| FILE | 파일 저장 (`/logs/spring.log`) | 항상 |
| ASYNC_DISCORD | Discord 웹훅 알림 | ERROR 레벨만 |

### 파일 롤링 정책
- 파일 크기: 10MB 초과 시 분할
- 보관: 최대 7일 / 총 100MB
- 파일명: `spring.log.yyyy-MM-dd.i.gz`

### MDC 컨텍스트
- `ApiAccessLogFilter`: 요청마다 `requestId` 생성 및 요청/응답 로깅
- `LogContextInterceptor`: Spring Security 인증 후 `userId` 주입
- `MdcTaskDecorator`: `@Async` 스레드에도 MDC 전파
- 민감 필드(`accessToken`, `refreshToken`) 자동 마스킹

### ERROR 알림 (Discord)
- `DiscordAppender`: ERROR 로그 발생 즉시 Discord 웹훅으로 전송
- 비동기 처리 (queue: 256) — 메인 스레드 블로킹 없음
- 알림 내용: 환경, 시간, requestId, userId, 로거명, 메시지, 스택트레이스(5줄)

---

## 2. 로그 수집 (Promtail)

**설정 파일:** `monitoring/promtail-config.yml`
**실행 위치:** prod VM (Spring Boot와 같은 VM)

### 동작 방식
- `./logs:/var/log/ilgijjan` 볼륨 마운트로 Spring Boot 로그 파일 접근
- `/var/log/ilgijjan/*.log` 경로 감시
- 변경 감지 시 즉시 Loki로 push
- 재시작 시 `/tmp/positions.yaml`로 마지막 읽은 위치 복원 (중복 전송 방지)
- 레이블: `job=ilgijjan`

---

## 3. 로그 저장 (Loki)

**설정 파일:** `monitoring/loki-config.yml`
**실행 위치:** monitoring VM

### 스토리지 구조
- **인덱스**: monitoring VM 로컬 (`/loki/index`) — TSDB 형식
- **청크**: GCS 버킷 `ilgijjan-490801-logs` — 영구 보관
- **WAL**: `/loki/wal` — 장애 복구용 Write-Ahead Log

### 주요 설정
- 스키마: v13 (TSDB)
- 오래된 샘플 거부: 168시간(7일) 이상 된 로그 수신 거부
- 청크 flush: 5분 idle 후 GCS로 업로드

### GCS 저장 경로
```
ilgijjan-490801-logs/
└── fake/          ← Loki 기본 tenant ID
    ├── chunks/    ← 로그 청크
    └── index/     ← 인덱스
```

---

## 4. 메트릭 수집 (Prometheus)

**설정 파일:** `monitoring/prometheus.yml`
**실행 위치:** monitoring VM

### 스크랩 대상
- **Job**: `ilgijjan-server`
- **엔드포인트**: `http://{prod VM 내부 IP}:8080/actuator/prometheus`
- **주기**: 15초

### 수집 메트릭
Spring Boot Micrometer가 자동 노출하는 메트릭:
- JVM (힙 메모리, GC, 스레드)
- HTTP 요청 수, 응답시간, 상태코드
- DB 커넥션 풀 (HikariCP)
- `up` 메트릭 — 스크랩 성공: 1, 실패(서버 다운): 0

---

## 5. 시각화 및 알림 (Grafana)

**실행 위치:** monitoring VM
**접속:** https://monitoring.ilgijjan.store

### 프로비저닝 구조
Grafana 시작 시 `/etc/grafana/provisioning/` 자동 로드:

```
grafana/provisioning/
├── datasources/datasources.yml   → Loki, Prometheus 자동 등록
├── dashboards/dashboards.yml     → 대시보드 경로 지정
└── alerting/alerting.yml         → 알림 룰 + Discord contact point 자동 등록

grafana/dashboards/
└── logs.json                     → ilgijjan Logs 대시보드
```

### 대시보드: ilgijjan Logs
| 패널 | 쿼리 | 설명 |
|------|------|------|
| 전체 로그 | `{job="ilgijjan"}` | 실시간 로그 스트림 |
| ERROR 로그 | `{job="ilgijjan"} \|= "ERROR"` | ERROR 로그 필터링 |
| 레벨별 발생 수 | `count_over_time` + pattern 파싱 | 시간대별 로그 레벨 그래프 |

### 서버 다운 알림
- **감지 조건**: `up{job="ilgijjan-server"} < 1` (1분 지속)
- **평가 주기**: 1분
- **알림 채널**: Discord 웹훅
- **noDataState**: Alerting (Prometheus 자체가 죽어도 알림)

---

## 6. 알림 채널 정리

| 상황 | 알림 방식 | 지연 |
|------|----------|------|
| ERROR 로그 발생 | Logback DiscordAppender | 즉시 (비동기) |
| 서버 다운 | Grafana Alerting (Prometheus) | 최대 2분 (1분 감지 + 1분 pending) |

---

## 7. 데이터 보관 정책

| 데이터 | 위치 | 보관 기간 |
|--------|------|----------|
| 로그 파일 | prod VM `~/logs/` | 7일 (롤링) |
| Loki 청크 | GCS `ilgijjan-490801-logs` | 영구 |
| Prometheus 메트릭 | monitoring VM Docker 볼륨 | VM 유지 기간 |
| Grafana 설정 | monitoring VM Docker 볼륨 + 프로비저닝 파일 | VM 유지 기간 / 파일로 복구 가능 |
