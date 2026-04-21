package com.back.nbe9112team06.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "로그인 응답")
public record LoginResponse(

        @Schema(description = "사용자 ID")
        int memberId,

        @Schema(description = "닉네임")
        String nickname
){ }
