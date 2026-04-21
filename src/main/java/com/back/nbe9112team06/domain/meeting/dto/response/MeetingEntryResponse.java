package com.back.nbe9112team06.domain.meeting.dto.response;

import com.back.nbe9112team06.domain.meeting.entity.MeetingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MeetingEntryResponse(
        Integer meetingId,
        String title,
        String category,
        Integer duration,
        MeetingStatus status,
        String roomUrl,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt
) {
}

