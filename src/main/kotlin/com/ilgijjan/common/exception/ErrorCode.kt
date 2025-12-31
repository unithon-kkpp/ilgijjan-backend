package com.ilgijjan.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    // Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 생겼습니다."),

    // Client Error
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "적절하지 않은 HTTP 메소드입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "요청 값의 타입이 잘못되었습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "적절하지 않은 값입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리소스를 찾을 수 없습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),

    // Diary
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "일기 정보를 찾을 수 없습니다."),
    NOT_DIARY_OWNER(HttpStatus.FORBIDDEN, "해당 일기 작성자가 아닙니다."),
    PRIVATE_DIARY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "비공개 일기에 접근할 권한이 없습니다."),

    // Music
    MUSIC_GENERATE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "음악 생성 타임아웃"),

    // Jwt
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "요청 헤더에 토큰이 존재하지 않습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    DUPLICATE_NAME(HttpStatus.CONFLICT, "이미 사용 중인 이름입니다."),

    // Like
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 정보를 찾을 수 없습니다."),
    INVALID_LIKE_COUNT(HttpStatus.BAD_REQUEST, "좋아요 개수가 0개인 상태에서는 차감할 수 없습니다."),

    // Notification
    FCM_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "FCM 토큰 정보를 찾을 수 없습니다."),

    // Auth
    KAKAO_REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "카카오 로그인에 필요한 필드가 누락되었습니다."),
    KAKAO_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "카카오 서버와 통신 중 에러가 발생했습니다."),
    INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 카카오 토큰입니다.");
}
