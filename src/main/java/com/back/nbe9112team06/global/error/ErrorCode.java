package com.back.nbe9112team06.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // ==================== 공통 ====================
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON-001",
            "서버 내부 오류가 발생했습니다.",
            "https://api.example.com/errors/common/internal-error"
    ),
    INVALID_REQUEST_PARAMETER(
            HttpStatus.BAD_REQUEST,
            "COMMON-002",
            "잘못된 요청 파라미터입니다.",
            "https://api.example.com/errors/common/invalid-parameter"
    ),
    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "COMMON-003",
            "접근 권한이 없습니다.",
            "https://api.example.com/errors/common/access-denied"
    ),
    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "COMMON-004",
            "요청한 리소스를 찾을 수 없습니다.",
            "https://api.example.com/errors/common/not-found"
    ),

    // ==================== 인증/인가 ====================
    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "AUTH-001",
            "인증 정보가 없거나 유효하지 않습니다.",
            "https://api.example.com/errors/auth/unauthorized"
    ),
    TOKEN_EXPIRED(
            HttpStatus.UNAUTHORIZED,
            "AUTH-002",
            "토큰이 만료되었습니다.",
            "https://api.example.com/errors/auth/token-expired"
    ),

    // ==================== VALIDATION ====================
    VALIDATION_FAILED(
            HttpStatus.BAD_REQUEST,
            "VALIDATION-001",
            "입력값 검증에 실패했습니다.",
            "https://api.example.com/errors/validation/failed"
    ),

    // ==================== MEMBER/회원 관리 ====================
    DUPLICATE_EMAIL(
            HttpStatus.CONFLICT,
            "MEMBER-001",
            "이미 등록된 이메일입니다.",
            "https://api.example.com/errors/member/duplicate-email"
    ),

    // ==================== 추가 예외 코드 (테스트용) ====================
    TYPE_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "TYPE_MISMATCH",
            "타입 변환에 실패했습니다.",
            "https://api.example.com/errors/common/type-mismatch"
    ),
    PARAM_MISSING(
            HttpStatus.BAD_REQUEST,
            "PARAM_MISSING",
            "필수 파라미터가 누락되었습니다.",
            "https://api.example.com/errors/common/param-missing"
    ),
    METHOD_NOT_ALLOWED(
            HttpStatus.METHOD_NOT_ALLOWED,
            "METHOD_NOT_ALLOWED",
            "지원하지 않는 HTTP 메서드입니다.",
            "https://api.example.com/errors/common/method-not-allowed"
    ),
    UNSUPPORTED_MEDIA_TYPE(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "UNSUPPORTED_MEDIA_TYPE",
            "지원하지 않는 Content-Type 입니다.",
            "https://api.example.com/errors/common/unsupported-media-type"
    ),
    DUPLICATE_RESOURCE(
            HttpStatus.CONFLICT,
            "DUPLICATE_RESOURCE",
            "이미 존재하는 리소스입니다.",
            "https://api.example.com/errors/common/duplicate-resource"
    );

    private final HttpStatus status;
    private final String code;              // 머신 가독용: "COMMON-001"
    private final String message;           // 사람 가독용
    private final String typeUri;           // RFC 9457 type URI

    /**
     * ProblemDetail 생성 헬퍼 (기본)
     */
    public ProblemDetail toProblemDetail(String detail, String instance) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);

        problemDetail.setType(URI.create(typeUri));
        problemDetail.setTitle(status.getReasonPhrase());  // null 시 자동 폴백됨
        problemDetail.setProperty("errorCode", code);
        problemDetail.setProperty("timestamp", java.time.LocalDateTime.now().toString());

        if (instance != null) {
            problemDetail.setInstance(URI.create(instance));
        }

        return problemDetail;
    }

    /**
     * ProblemDetail 생성 헬퍼 (instance 생략)
     */
    public ProblemDetail toProblemDetail(String detail) {
        return toProblemDetail(detail, null);
    }

    /**
     * ProblemDetail 생성 헬퍼 (message 사용)
     */
    public ProblemDetail toProblemDetail() {
        return toProblemDetail(this.message, null);
    }
}