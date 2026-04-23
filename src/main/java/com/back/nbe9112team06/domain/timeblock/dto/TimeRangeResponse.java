package com.back.nbe9112team06.domain.timeblock.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeRangeResponse(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}