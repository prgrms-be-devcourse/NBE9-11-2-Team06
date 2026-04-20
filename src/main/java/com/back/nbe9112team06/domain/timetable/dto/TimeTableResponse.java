package com.back.nbe9112team06.domain.timetable.dto;

import java.util.List;

public record TimeTableResponse(List<DateResponse> availableDateTimes) {
}
