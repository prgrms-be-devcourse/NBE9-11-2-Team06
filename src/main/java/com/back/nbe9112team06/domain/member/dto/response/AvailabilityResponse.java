package com.back.nbe9112team06.domain.member.dto.response;

// 이메일 중복여부 체크 dto
public record AvailabilityResponse(
        boolean available
) {}
