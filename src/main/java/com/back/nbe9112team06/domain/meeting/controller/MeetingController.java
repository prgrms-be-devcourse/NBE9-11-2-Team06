package com.back.nbe9112team06.domain.meeting.controller;

import com.back.nbe9112team06.domain.meeting.dto.ConfirmedScheduleResponse;
import com.back.nbe9112team06.domain.meeting.dto.FinalizeRequest;
import com.back.nbe9112team06.domain.meeting.service.MeetingService;
import com.back.nbe9112team06.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    // 일정 확정
    @PostMapping("/{meetingId}/confirm")
    public ResponseEntity<ApiResponse<ConfirmedScheduleResponse>> confirm(
            @PathVariable Integer meetingId,
            @RequestParam @NotNull Integer memberId,
            @Valid @RequestBody FinalizeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                meetingService.confirm(meetingId, memberId, request)
        ));
    }

    // 확정 취소
    @DeleteMapping("/{meetingId}/confirm")
    public ResponseEntity<ApiResponse<Void>> cancelConfirm(
            @PathVariable Integer meetingId,
            @RequestParam @NotNull Integer memberId
    ) {
        meetingService.cancelConfirm(meetingId, memberId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 확정 일정 조회
    @GetMapping("/{meetingId}/confirm")
    public ResponseEntity<ApiResponse<ConfirmedScheduleResponse>> getConfirmedSchedule(
            @PathVariable Integer meetingId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                meetingService.getConfirmedSchedule(meetingId)
        ));
    }
}