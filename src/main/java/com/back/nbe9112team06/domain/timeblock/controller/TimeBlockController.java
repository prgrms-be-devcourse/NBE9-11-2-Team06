package com.back.nbe9112team06.domain.timeblock.controller;

import com.back.nbe9112team06.domain.timeblock.dto.ParticipantsScheduleResponse;
import com.back.nbe9112team06.domain.timeblock.dto.TimeBlockDeleteRequest;
import com.back.nbe9112team06.domain.timeblock.dto.TimeBlockRequest;
import com.back.nbe9112team06.domain.timeblock.service.TimeBlockService;
import com.back.nbe9112team06.domain.timetable.service.TimeTableService;
import com.back.nbe9112team06.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meetings")
public class TimeBlockController {

    private final TimeBlockService timeBlockService;
    private final TimeTableService timeTableService;

    @PostMapping("/{meetingId}/time-blocks")
    public ResponseEntity<Void> addTimeBlock(@PathVariable Integer meetingId, @RequestBody @Valid TimeBlockRequest timeBlockRequest){
        timeBlockService.registerTimeBlock(meetingId, timeBlockRequest);
        timeTableService.aggregate(meetingId);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{meetingId}/time-blocks")
    public ResponseEntity<Void> deleteTimeBlock(@PathVariable Integer meetingId, @RequestBody @Valid TimeBlockDeleteRequest timeBlockDeleteRequest){
        timeBlockService.deleteTimeBlock(meetingId, timeBlockDeleteRequest);
        timeTableService.aggregate(meetingId);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/{meetingId}/participants")
    public ApiResponse<List<ParticipantsScheduleResponse>> getParticipantSchedules(
            @PathVariable Integer meetingId) {
        return new ApiResponse<>("200-1", "참여자 목록입니다.", timeBlockService.getParticipantSchedules(meetingId));
    }
}
