package com.back.nbe9112team06.domain.timetable.controller;

import com.back.nbe9112team06.domain.timetable.dto.RecommendedScheduleResponse;
import com.back.nbe9112team06.domain.timetable.dto.TimeTableResponse;
import com.back.nbe9112team06.domain.timetable.service.TimeTableService;
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
public class TimeTableController {

    private final TimeTableService timeTableService;

    @GetMapping("/{meetingId}/timetable")
    public TimeTableResponse getTimeTable(@PathVariable Integer meetingId) {
        timeTableService.aggregate(meetingId); // TODO: 병합 시 TimeBlock POST 후 실행되도록 수정
        return timeTableService.getTimeTable(meetingId);
    }

    @GetMapping("/{meetingId}/recommend")
    public ResponseEntity<ApiResponse<List<RecommendedScheduleResponse>>> recommend(@PathVariable Integer meetingId) {
        return ResponseEntity.ok(ApiResponse.ok(timeTableService.recommend(meetingId)));
    }
}
