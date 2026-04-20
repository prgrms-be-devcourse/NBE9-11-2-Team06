package com.back.nbe9112team06.global.springDoc.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * 인증 도메인 특화 오류 응답 (로그인 실패: AUTH-004)
 * <p>
 * ⚠️ MEMBER-001(회원없음) 은 Member 도메인 책임이므로 포함하지 않음
 * 내부적으로 MemberService 호출 시 발생하더라도,
 * AuthController 는 이를 AUTH-004 로 추상화하여 응답합니다.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "401",
        description = "로그인 실패: 이메일 또는 비밀번호 불일치 (AUTH-004)",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(ref = "#/components/schemas/ProblemDetail"),
                examples = @ExampleObject(
                        name = "invalidCredentials",
                        value = """
                {
                  "type": "https://api.nbe9112team06.com/errors/auth/004",
                  "title": "Unauthorized",
                  "status": 401,
                  "detail": "이메일 또는 비밀번호가 올바르지 않습니다.",
                  "errorCode": "AUTH-004",
                  "timestamp": "2024-01-15T10:30:00Z"
                }
                """
                )
        )
)
public @interface AuthSpecificErrorResponses {}