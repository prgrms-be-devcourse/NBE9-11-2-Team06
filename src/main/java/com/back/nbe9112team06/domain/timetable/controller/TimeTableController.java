package com.back.nbe9112team06.domain.timetable.controller;

import com.back.nbe9112team06.domain.timetable.dto.TimeTableResponse;
import com.back.nbe9112team06.domain.timetable.service.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
