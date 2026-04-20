package com.back.nbe9112team06.domain.timeblock.controller;

import com.back.nbe9112team06.domain.timeblock.dto.ParticipantsScheduleResponse;
import com.back.nbe9112team06.domain.timeblock.service.TimeBlockService;
import com.back.nbe9112team06.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TimeBlockController {

    private final TimeBlockService timeBlockService;

    @GetMapping("/{meetingId}/participants")
    public ResponseEntity<ApiResponse<List<ParticipantsScheduleResponse>>> getParticipantSchedules(
            @PathVariable Integer meetingId) {
        return ResponseEntity.ok(ApiResponse.ok(
                timeBlockService.getParticipantSchedules(meetingId)
        ));
    }
}
