# 일기짠 (ilgijjan) — Server

> 아동을 위한 **AI 일기장 앱**의 백엔드 API 서버

- 아이가 사진 한 장과 그날의 날씨·기분을 고르면, 여러 AI를 거쳐 하루를 **글·그림·음악**으로 완성해 주는 서비스의 서버입니다.
- 하나의 일기 요청을 **비동기 파이프라인**으로 처리하고, 완성되면 **앱에는 푸시 알림(FCM)**, **웹에는 폴링 응답**으로 결과를 전달합니다.

<br/>

## 📖 프로젝트 소개

- **무엇** : 아동용 AI 일기장 서비스의 백엔드 API 서버
- **누구를 위해** : 아이는 고르고 누르기만 하고, 무거운 AI 처리는 서버가 알아서 하도록
- **클라이언트** : **React 웹**과 **Flutter 앱**, 두 클라이언트와 연동
- **특징** : 글·그림·음악 생성을 **비동기 파이프라인**으로 처리해, 느린 AI 작업에도 앱은 멈추지 않음

<br/>

## ✨ 주요 기능

| 기능 | 설명 |
|------|------|
| 🔐 카카오 로그인 | 카카오 OAuth로 가입·로그인, JWT(Access/Refresh)로 인증 상태 유지 |
| ✏️ AI 일기 생성 | 글 정제(Gemini) → 그림(Gemini·Replicate) → 음악(Suno)을 순서대로 만드는 비동기 파이프라인 |
| 🖼 OCR 입력 | 손글씨·사진 일기를 Google Cloud Vision OCR로 글자로 변환해 입력 |
| 📚 일기 조회·목록 | 내가 쓴 일기를 월별로 모아 보고, 생성 진행 상태를 폴링으로 확인 |
| ❤️ 좋아요 · 공개 | 일기를 공개/비공개로 전환, 공개된 일기에 좋아요 |
| 🌍 공개 피드 | 다른 사용자가 공개한 일기 모아보기 (Redis 캐싱으로 빠르게 응답) |
| 🔔 완성 알림 | 생성 완료를 **앱은 FCM 푸시**로, **웹은 상태 폴링 조회**로 전달 |
| 🎵 음표 · 결제 | 일기 생성에 쓰이는 음표(재화)와 결제·웹훅(Billing) 처리 |
| ⚙️ 관리자 | 음표 충전 어드민 (Google 로그인 + 이메일 화이트리스트로 접근 제한) |

<br/>

## 🛠 기술 스택

| 구분 | 기술 | 역할 |
|------|------|------|
| 언어·런타임 | **Kotlin 1.9** · **JDK 21** | 간결하고 안전한 서버 코드 |
| 프레임워크 | **Spring Boot 3.5** | REST API, 의존성 주입, 트랜잭션 관리 |
| 비동기 통신 | **Spring WebFlux (WebClient)** | 외부 AI를 논블로킹으로 호출 |
| 보안 | **Spring Security** · **JWT** · **OAuth2** | 인증·인가, 카카오 소셜 로그인 |
| 데이터 | **Spring Data JPA** · **MySQL 8** | 일기·유저·재화 영속화 |
| 캐시 | **Redis** | 공개 피드 등 무거운 조회 결과 캐싱 |
| 비동기 파이프라인 | **Spring Event · @Async · Virtual Thread (JDK 21)** | 일기 생성 요청을 백그라운드로 처리 |
| 외부 AI | **Gemini · Replicate · Suno · Google Vision OCR** | 글·그림·음악 생성, OCR |
| 클라우드 | **GCP · Cloud Storage · Firebase(FCM)** | 결과물 저장, 푸시 알림 |
| 인프라 | **Terraform** · **Docker Compose** | 인프라 코드화(IaC), 배포 |
| 관측 | **Prometheus · Grafana · Loki** | 메트릭·로그 수집, 장애 시 Discord 알림 |
| 문서 | **SpringDoc (Swagger UI)** | API 문서 자동화 |

<br/>

## 🧩 이렇게 구현했어요

### 도메인 · 통합을 나눈 모듈 구조
비즈니스 로직(domain)과 외부 연동(integration)을 분리해, 외부 서비스가 바뀌어도 핵심 도메인은 영향을 받지 않도록 했습니다.

```
src/main/kotlin/com/ilgijjan/
├── common/       # 공통 관심사 (config, jwt, log, aop, exception, resolver)
├── domain/       # 비즈니스 도메인
│   ├── auth        # 인증·JWT
│   ├── user        # 회원 (탈퇴·재가입 복원 포함)
│   ├── diary       # 일기 (핵심 도메인)
│   ├── like        # 좋아요
│   ├── wallet      # 음표(재화)
│   ├── billing     # 결제·웹훅
│   ├── fcmtoken    # 푸시 토큰
│   └── admin       # 관리자
└── integration/  # 외부 서비스 통합
    ├── text · image · music   # AI 생성기
    ├── ocr · oauth            # Google Vision OCR, 소셜 로그인
    ├── storage · cache        # GCS, Redis
    └── notification           # FCM
```

### 역할을 나눈 레이어 설계
한 클래스가 모든 일을 떠안지 않도록, 도메인 동작을 책임별로 나눴습니다.

```
Service (흐름 조립)  →  Creator · Reader · Updater (도메인 동작)  →  Repository (영속화)
```

- **Service** 는 흐름을 조립하고, 세부 동작은 아래로 위임합니다.
- **Creator / Reader / Updater** 가 생성·조회·수정 같은 도메인 책임을 나눠 맡습니다. (`DiaryCreator`, `DiaryReader`, `DiaryUpdater`)
- `@LoginUser`(인증 유저 주입), `@CheckDiaryOwner`(소유권 검사) 같은 **커스텀 어노테이션 + AOP**로 반복 로직을 분리했습니다.

### 멈추지 않는 비동기 생성 파이프라인
일기 작성 요청은 곧장 처리하지 않고, 트랜잭션 커밋 후 발행되는 Spring 이벤트를 받아 백그라운드에서 단계별로 가공합니다.

```
[일기 작성 요청]
      │  (즉시 응답, 상태 = 생성중)
      │  트랜잭션 커밋 후 이벤트 발행 (@TransactionalEventListener · AFTER_COMMIT)
      ▼
  @Async 가상 스레드에서 백그라운드 처리
      ▼
  ├─ 1) 글 정제   (Gemini)
  ├─ 2) 그림 생성  (Gemini · Replicate, 지수 백오프 재시도)
  └─ 3) 음악 생성  (Suno · Webhook 콜백 → GCS 업로드)
      │
      ▼
  완성  ──┬─ 📱 앱 : FCM 푸시 알림
          └─ 💻 웹 : 상태 폴링 조회 API
```

- 요청은 DB 커밋 후 **Spring `ApplicationEvent`** 로 발행하고, `@TransactionalEventListener(AFTER_COMMIT)`가 받아 **`@Async` 가상 스레드(JDK 21)** 에서 처리합니다.
- 외부 AI는 **WebClient**로 호출하고, 이미지·음악 등 독립 작업은 가상 스레드로 병렬 처리합니다.
- 음악은 시간이 오래 걸려 **콜백(Webhook)** 방식으로 받고, 임시 URL을 GCS에 옮겨 영구 저장합니다.
- 서버 재시작 시 미완성(PENDING) 일기는 **`ApplicationRunner` 기반 복구 로직**으로 다시 처리합니다. (큐의 재전송 대신)
- 완성 결과는 **앱은 FCM 푸시로 즉시 받고**, **웹은 생성 상태를 주기적으로 폴링**해 완료 시점에 결과 화면으로 넘어갑니다.

### 갈아끼울 수 있는 AI 생성기 (전략 패턴)
이미지 생성기는 `ImageGenerator` 인터페이스 아래 `GeminiImageGenerator` / `ReplicateImageGenerator`로 두어, **설정만 바꿔 AI 제공자를 교체**할 수 있습니다.

### 요청을 끝까지 추적하는 로깅
요청마다 트레이스 ID(`requestId`)를 부여하고, **MDC를 비동기 가상 스레드까지 전파**해 하나의 요청 흐름을 로그로 따라갈 수 있습니다.
수집된 로그·메트릭은 **Prometheus · Grafana · Loki**로 모으고, 로그는 GCS에 영구 저장합니다.

<br/>

## 🔐 인증 방식

- 카카오 OAuth 기반 소셜 로그인 + **JWT(Access/Refresh)** 인증을 사용합니다.
- 토큰이 만료되면 Refresh 토큰으로 자동 갱신되어, **다시 로그인할 필요 없이** 서비스를 이어서 사용할 수 있습니다.
- 회원 탈퇴 시 계정을 비활성화하고, **7일 이내 재가입 시 기존 계정을 복원**합니다.
