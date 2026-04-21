package com.back.nbe9112team06.domain.timetable.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record RecommendedScheduleResponse(LocalDate date,
                                          LocalTime startTime,
                                          LocalTime endTime,
                                          int availableCount) {

}
