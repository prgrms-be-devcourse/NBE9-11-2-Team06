package com.back.nbe9112team06.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.LocalDateTime;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // ==================== 공통 (COMMON) ====================
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON-001",
            "서버 내부 오류가 발생했습니다."
    ),
    INVALID_REQUEST_PARAMETER(
            HttpStatus.BAD_REQUEST,
            "COMMON-002",
            "잘못된 요청 파라미터입니다."
    ),
    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "COMMON-003",
            "요청한 리소스를 찾을 수 없습니다."
    ),
    METHOD_NOT_ALLOWED(
            HttpStatus.METHOD_NOT_ALLOWED,
            "COMMON-004",
            "지원하지 않는 HTTP 메서드입니다."
    ),
    UNSUPPORTED_MEDIA_TYPE(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "COMMON-005",
            "지원하지 않는 Content-Type입니다."
    ),
    TYPE_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "COMMON-006",
            "요청 파라미터의 타입이 올바르지 않습니다."
    ),
    PARAM_MISSING(
            HttpStatus.BAD_REQUEST,
            "COMMON-007",
            "필수 파라미터가 누락되었습니다."
    ),
    DUPLICATE_RESOURCE(
            HttpStatus.CONFLICT,
            "COMMON-008",
            "이미 존재하는 리소스입니다."
    ),
    VALIDATION_FAILED(
            HttpStatus.BAD_REQUEST,
            "COMMON-009",
            "입력값 검증에 실패했습니다."
    ),

    // ==================== 인증 (AUTH) ====================
    // 토큰 없음 — 필터에서 SecurityContext 미설정 → EntryPoint 경유
    TOKEN_MISSING(
            HttpStatus.UNAUTHORIZED,
            "AUTH-001",
            "인증 토큰이 없습니다."
    ),
    // 토큰 위조/형식 오류 — getPayload() null 반환 → EntryPoint 경유
    TOKEN_INVALID(
            HttpStatus.UNAUTHORIZED,
            "AUTH-002",
            "인증 토큰이 유효하지 않습니다."
    ),
    // 토큰 만료 — ExpiredJwtException → 필터에서 직접 응답
    TOKEN_EXPIRED(
            HttpStatus.UNAUTHORIZED,
            "AUTH-003",
            "인증 토큰이 만료되었습니다."
    ),
    // 로그인 실패 — AuthService → BusinessException → GlobalExceptionHandler 경유
    INVALID_LOGIN_CREDENTIALS(
            HttpStatus.UNAUTHORIZED,
            "AUTH-004",
            "이메일 또는 비밀번호가 올바르지 않습니다."
    ),
    // 인가 실패 — AccessDeniedHandler 경유 (현재 미사용, 확장 대비)
    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "AUTH-005",
            "접근 권한이 없습니다."
    ),
    // Rq.getActor() 실패 — SecurityContext 비어있을 때
    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "AUTH-006",
            "인증이 필요합니다."
    ),

    // ==================== 회원 (MEMBER) ====================
    MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MEMBER-001",
            "존재하지 않는 회원입니다."
    ),
    DUPLICATE_EMAIL(
            HttpStatus.CONFLICT,
            "MEMBER-002",
            "이미 등록된 이메일입니다."
    );

    private final HttpStatus status;
    private final String code;      // 기계 식별용: "AUTH-001"
    private final String message;   // 기본 detail 메시지

    private String buildTypeUri() {
        // "AUTH-001" → "auth/001"
        String[] parts = code.toLowerCase().split("-");
        return "https://api.nbe9112team06.com/errors/"
                + parts[0] + "/" + parts[1];
    }

    // ── ProblemDetail 생성 ──────────────────────────────

    // 가장 기본: detail + instance 직접 지정
    public ProblemDetail toProblemDetail(String detail, String instance) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setType(URI.create(buildTypeUri()));
        pd.setTitle(status.getReasonPhrase());
        pd.setProperty("errorCode", code);
        pd.setProperty("timestamp", LocalDateTime.now().toString());
        if (instance != null) {
            pd.setInstance(URI.create(instance));
        }
        return pd;
    }

    // instance 생략
    public ProblemDetail toProblemDetail(String detail) {
        return toProblemDetail(detail, null);
    }

    // detail도 기본 message 사용
    public ProblemDetail toProblemDetail() {
        return toProblemDetail(this.message, null);
    }
}