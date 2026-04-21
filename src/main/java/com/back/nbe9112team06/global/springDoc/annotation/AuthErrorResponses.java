package com.back.nbe9112team06.global.springDoc.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

/**
 * 인증/인가 관련 오류 응답 (401, 403) 을 문서에 추가하는 메타 어노테이션
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "401",
        description = "인증 실패: 토큰 없음/위조/만료 (AUTH-001/002/003)",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(ref = "#/components/schemas/ProblemDetail")
        )
)
@ApiResponse(
        responseCode = "403",
        description = "인가 실패: 접근 권한 없음 (AUTH-005)",
        content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(ref = "#/components/schemas/ProblemDetail")
        )
)
public @interface AuthErrorResponses {}