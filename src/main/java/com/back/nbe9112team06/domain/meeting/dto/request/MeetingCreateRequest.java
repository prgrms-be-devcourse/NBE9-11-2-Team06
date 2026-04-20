package com.back.nbe9112team06.domain.meeting.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MeetingCreateRequest(
        @NotBlank(message = "모임명은 필수입니다.")
        String title,

        @NotNull(message = "시작일은 필수입니다.")
        LocalDate startDate,

        @NotNull(message = "종료일은 필수입니다.")
        LocalDate endDate,

        @NotNull(message = "최소 회의 시간은 필수입니다.")
        @Min(value = 1, message = "최소 회의 시간은 1 이상이어야 합니다.")
        Integer duration,

        @NotBlank(message = "성격은 필수입니다.")
        String category
) {
}

