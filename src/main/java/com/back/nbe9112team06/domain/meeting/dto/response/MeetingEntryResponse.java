package com.back.nbe9112team06.domain.meeting.dto.response;

import java.time.LocalDate;

public record MeetingEntryResponse(
        Integer meetingId,
        String title,
        String category,
        Integer duration,
        String status,
        String roomUrl,
        LocalDate startDate,
        LocalDate endDate
) {
}

