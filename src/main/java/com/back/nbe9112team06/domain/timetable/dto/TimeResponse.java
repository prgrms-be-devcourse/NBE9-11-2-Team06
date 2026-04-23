package com.back.nbe9112team06.domain.timetable.dto;

import java.time.LocalTime;
import java.util.List;

public record TimeResponse(
        LocalTime time,
        List<String> participants,
        int count
) {
}
