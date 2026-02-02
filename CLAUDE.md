# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Ilgijjan (일기짠) is a Korean diary AI service backend built with Spring Boot 3.5.4 and Kotlin 1.9.25. Users write diary entries that are processed through AI pipelines to generate refined text, images, and music.

## Build & Run Commands

```bash
# Build
./gradlew clean build           # Full build with tests
./gradlew build -x test         # Build without tests

# Run
./gradlew bootRun               # Run application (port 8080)

# Test
./gradlew test                  # Run all tests
./gradlew test --tests "*.SomeTestClass"  # Run specific test class
```

## Tech Stack

- **Language**: Kotlin 1.9.25, JDK 21
- **Framework**: Spring Boot 3.5.4, Spring Security (JWT), Spring Data JPA
- **Database**: MySQL 8.0.33, Redis
- **Messaging**: RabbitMQ (Spring AMQP)
- **External APIs**: Google Cloud (Storage, Vision, Gemini AI), Suno AI (music), Replicate (image), Kakao OAuth, Naver Clova OCR, Firebase (FCM)

## Architecture

### Package Structure

```
src/main/kotlin/com/ilgijjan/
├── common/         # Cross-cutting concerns (annotations, AOP, config, logging, JWT, exceptions)
├── domain/         # Business domains (auth, billing, diary, fcmtoken, like, user, wallet)
└── integration/    # External service integrations (cache, image, messaging, music, notification, oauth, ocr, storage, text)
```

### Key Patterns

**Domain Layer**:
- Command/Reader pattern: `DiaryCreator`, `DiaryReader`, `DiaryUpdater` for domain operations
- Custom annotations: `@LoginUser` for user extraction via argument resolver, `@CheckDiaryOwner` for authorization via AOP

**Event-Driven Processing**:
- RabbitMQ queues for async diary processing pipeline: text refinement → image generation → music generation
- Spring `@Async` with custom executor (4-8 threads) for parallel operations
- External callbacks from Suno API via webhook endpoints

**Logging Infrastructure**:
- `ApiAccessLogFilter`: Captures HTTP requests/responses with trace IDs
- `LogContextInterceptor`: Injects authenticated user ID into MDC after Spring Security
- `ReadableRequestWrapper`: Caches request body for both logging and controller use
- `MdcTaskDecorator`: Propagates MDC context to async threads
- Excluded paths defined in `LogConstants` to prevent OOM on file uploads

**Integration Layer**:
- Strategy pattern for generators (e.g., `GeminiImageGenerator` vs `ReplicateImageGenerator`)
- WebClient for async HTTP calls to external APIs

### Security

- JWT authentication with access/refresh tokens
- Stateless sessions
- Permitted endpoints: `/api/auth/*`, `/api/music/**`, `/billing/webhooks/**`, Swagger UI, Actuator

## Configuration

- `application.yml`: Default config (active profile: local)
- `application-local.yml`: Local development
- `application-prod.yml`: Production
- Credentials and API keys are loaded from environment variables

## API Documentation

Swagger UI available at `/swagger-ui/index.html` when running locally.
