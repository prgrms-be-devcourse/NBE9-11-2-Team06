package com.back.nbe9112team06.domain.timetable.controller;

import com.back.nbe9112team06.domain.timetable.dto.RecommendedScheduleResponse;
import com.back.nbe9112team06.domain.timetable.dto.TimeTableResponse;
import com.back.nbe9112team06.domain.timetable.service.TimeTableService;
import com.back.nbe9112team06.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meetings")
public class TimeTableController {

    private final TimeTableService timeTableService;

    @GetMapping("/{meetingId}/timetable")
    public ApiResponse<TimeTableResponse> getTimeTable(@PathVariable Integer meetingId) {
        // timeTableService.aggregate(meetingId); // TODO: 병합 시 TimeBlock POST 후 실행되도록 수정
        return new ApiResponse<>("200-1", "타임테이블 조회 성공", timeTableService.getTimeTable(meetingId));
    }

    @GetMapping("/{meetingId}/recommend")
    public ApiResponse<List<RecommendedScheduleResponse>> recommend(@PathVariable Integer meetingId) {
        return new ApiResponse<>("200-1", "추천 일정입니다.", timeTableService.recommend(meetingId));
    }
}
