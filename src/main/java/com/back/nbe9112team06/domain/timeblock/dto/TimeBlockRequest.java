package com.back.nbe9112team06.domain.timeblock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TimeBlockRequest {

    @NotBlank(message = "이름을 입력해주세요")
    private String guestName;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String guestPassword;

    @NotEmpty(message = "가능한 시간을 선택해주세요")
    private List<String> availableDateTimes;
}
