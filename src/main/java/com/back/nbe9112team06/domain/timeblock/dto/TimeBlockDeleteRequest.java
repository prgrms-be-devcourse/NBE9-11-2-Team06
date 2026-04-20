package com.back.nbe9112team06.domain.timeblock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimeBlockDeleteRequest {

    @NotBlank(message = "이름을 입력해주세요")
    private String guestName;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String guestPassword;
}
