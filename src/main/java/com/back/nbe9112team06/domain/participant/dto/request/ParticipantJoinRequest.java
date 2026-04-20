package com.back.nbe9112team06.domain.participant.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ParticipantJoinRequest(
        @NotBlank(message = "참가자 이름은 필수입니다.")
        String guestName,

        @NotBlank(message = "참가자 비밀번호는 필수입니다.")
        String guestPassword
) {
}

