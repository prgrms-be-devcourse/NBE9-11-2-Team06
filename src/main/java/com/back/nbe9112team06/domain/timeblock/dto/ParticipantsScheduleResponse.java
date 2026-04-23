package com.back.nbe9112team06.domain.timeblock.dto;

import java.util.List;

public record ParticipantsScheduleResponse(
        String name,
        List<TimeRangeResponse> availableTimeRanges) {
}
