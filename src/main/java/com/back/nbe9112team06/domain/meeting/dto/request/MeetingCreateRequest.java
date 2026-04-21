package com.back.nbe9112team06.domain.meeting.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record MeetingCreateRequest(
        @NotBlank(message = "모임명은 필수입니다.")
        String title,

        @NotNull(message = "날짜 선택은 필수입니다")
        @NotEmpty(message = "최소 1개의 날짜를 선택해주세요.")
        List<LocalDate> dates,

        @NotNull(message = "최소 회의 시간은 필수입니다.")
        @Min(value = 1, message = "최소 회의 시간은 1 이상이어야 합니다.")
        Integer duration,

        @NotBlank(message = "모임 성격은 필수입니다.")
        String category
) {

}

