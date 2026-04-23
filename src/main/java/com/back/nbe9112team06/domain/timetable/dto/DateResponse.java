package com.back.nbe9112team06.domain.timetable.dto;

import java.time.LocalDate;
import java.util.List;

public record DateResponse(
        LocalDate availableDate,
        List<TimeResponse> availableTimeInfos
) {
}
