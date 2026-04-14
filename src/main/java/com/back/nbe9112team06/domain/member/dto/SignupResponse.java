package com.back.nbe9112team06.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답")
public record SignupResponse(
        @Schema(description = "사용자 ID")
        int memberId,

        @Schema(description = "이메일")
        String email,

        @Schema(description = "메시지")
        String message
) {}